package com.goldroad.goldroad.domain.meet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldroad.goldroad.domain.entity.Meeting;
import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.entity.MemberMeet;
import com.goldroad.goldroad.domain.member.MemberRepository;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.util.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingService {

	private final MeetRepository meetRepository;
	private final MemberRepository memberRepository;
	private final MemberMeetRepository memberMeetRepository;
	private final String URL;
	private final String API_KEY;

	public MeetingService(MeetRepository meetRepository, MemberRepository memberRepository, MemberMeetRepository memberMeetRepository, @Value("${open-ai.url}") String url, @Value("${open-ai.api-key}") String apiKey) {
		this.meetRepository = meetRepository;
		this.memberRepository = memberRepository;
		this.memberMeetRepository = memberMeetRepository;
		this.URL = url;
		this.API_KEY = apiKey;
	}

	@Transactional
	public void update(Long meetingId) {

		Optional<String> currentMemberEmail = SecurityUtil.getCurrentMemberEmail();
		Optional<Member> member = memberRepository.findByEmail(currentMemberEmail.orElseThrow(() -> new ApiException("사용자 정보가 없습니다.")));

		Optional<Meeting> meeting = meetRepository.findById(meetingId);

		MemberMeet byMemberAndMeeting = memberMeetRepository.findOneByMemberAndMeeting(
			member.orElseThrow(() -> new ApiException("사용자 정보가 없습니다.")),
			meeting.orElseThrow(() -> new ApiException("미팅 정보가 없습니다."))
		);

		byMemberAndMeeting.setAttend(true);
	}

	@Transactional
	public void createMeeting(String interest, String preferredTime, String preferredPeople) throws JsonProcessingException {

		GptResponseDto match = match(interest);

		if(match != null) {
			Meeting meeting = new Meeting(match.getMeetingPurpose(), match.getExplain(), match.getRecommendedActivities(), preferredTime, interest);
			meetRepository.save(meeting);

			List<Member> byInterestLikeAndPreferredPeople = memberRepository.findByInterestLikeAndPreferredPeopleAndPreferredTime("%" + interest + "%", preferredPeople, preferredTime);

			byInterestLikeAndPreferredPeople.forEach(member1 -> {
				MemberMeet memberMeet = new MemberMeet(member1, meeting, false);
				memberMeetRepository.save(memberMeet);
			});
		}
	}

	public GptResponseDto match(String subject) throws JsonProcessingException {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(API_KEY);
		headers.setContentType(MediaType.APPLICATION_JSON);

		GptRequestDto gptRequestDto = new GptRequestDto("gpt-4o-mini", 0.7F);

		gptRequestDto.getMessages().add(new GptRole("user", "Please extract information as a JSON object. Please use English for the json key value. Please use Korean for the json value. 은퇴 후 제주도로 이주를 고민하는 사람들을 위한 모임을 생성하려고 합니다. 이 사람들의 공통 관심사는 다음과 같습니다.\n" +
			"주제: " + subject + "\n" +
			"위 주제를 바탕으로 다음 형식으로 제주 이주에 도움이 될 수 있는 모임을 제안해주세요. 모임은 화상 통화에서 이루어집니다. 모임을 주도하는 사람은 없으니 교육, 세미나 등의 내용은 제외하고 자유롭게 소통 가능한 주제로 만들어주세요. 주제에 대한 제목과 주제에 대한 설명을 해주세요. 아직 제주도에 정착하지 않은 이주 희망자들을 위한 모임임에 유의하세요. 추천활동에는 제3자의 개입이 없는 활동들로 5개만 추천해주세요. 마지막 활동으로는 서로 응원하고 격려하는 등 긍정적인 마무리가 좋습니다. 글은 순수 텍스트만 넣어주세요.\n" +
			"Do not include any explanations, only provide a RFC8259 compliant JSON response  following this format without deviation.\n" +
			"{\n" +
			"  \"meetingPurpose\": \"meetingPurpose title phrase\",\n" +
			"  \"explain\": \"explain about meeting purpose phrase\",\n" +
			"  \"recommendedActivities\": \"recommended activities text\"\n" +
			"}\n"));


		HttpEntity<GptRequestDto> request = new HttpEntity<>(gptRequestDto, headers);

		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(URL, request, String.class);

		String body = stringResponseEntity.getBody();

		String regex = "\"content\": \"(\\{.*\\})\"";
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
		java.util.regex.Matcher matcher = pattern.matcher(body);

		if(matcher.find()) {
			String extractedContent = matcher.group(1).replace("\\n", "").replace("\\", "");

			ObjectMapper objectMapper = new ObjectMapper();
			GptResponseDto gptResponseDto = objectMapper.readValue(extractedContent, GptResponseDto.class);

			return gptResponseDto;
		}

		return null;
	}
}
