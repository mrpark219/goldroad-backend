package com.goldroad.goldroad.domain.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.goldroad.goldroad.domain.MemberAuthority.MemberAuthorityRepository;
import com.goldroad.goldroad.domain.authority.AuthorityRepository;
import com.goldroad.goldroad.domain.entity.*;
import com.goldroad.goldroad.domain.meet.MeetRepository;
import com.goldroad.goldroad.domain.meet.MemberMeetRepository;
import com.goldroad.goldroad.domain.member.dto.LoginRequestDto;
import com.goldroad.goldroad.domain.member.dto.SignUpRequestDto;
import com.goldroad.goldroad.domain.member.dto.SignUpResponseDto;
import com.goldroad.goldroad.domain.member.dto.TokenDto;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.security.RefreshToken;
import com.goldroad.goldroad.global.security.RefreshTokenRepository;
import com.goldroad.goldroad.global.security.TokenProvider;
import com.goldroad.goldroad.global.security.TokenType;
import com.goldroad.goldroad.global.util.GptResponseDto;
import com.goldroad.goldroad.global.util.GptUtil;
import com.goldroad.goldroad.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Service
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthorityRepository authorityRepository;
	private final MemberAuthorityRepository memberAuthorityRepository;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MeetRepository meetRepository;
	private final MemberMeetRepository memberMeetRepository;

	public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AuthorityRepository authorityRepository, MemberAuthorityRepository memberAuthorityRepository, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, MeetRepository meetRepository, MemberMeetRepository memberMeetRepository) {
		this.passwordEncoder = passwordEncoder;
		this.memberRepository = memberRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.authorityRepository = authorityRepository;
		this.memberAuthorityRepository = memberAuthorityRepository;
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.meetRepository = meetRepository;
		this.memberMeetRepository = memberMeetRepository;
	}

	@Transactional
	public SignUpResponseDto signup(SignUpRequestDto signupRequestDto) throws JsonProcessingException {

		if(memberRepository.findByEmail(signupRequestDto.getEmail()).orElse(null) != null) {
			throw new ApiException("이미 가입되어 있는 유저입니다.", HttpStatus.CONFLICT);
		}
		Authority authority = authorityRepository.findByName("ROLE_USER")
			.orElseThrow(() -> new RuntimeException("Role Not Found"));

		Member member = new Member(
			signupRequestDto.getNickname(),
			signupRequestDto.getEmail(),
			passwordEncoder.encode(signupRequestDto.getPassword()),
			signupRequestDto.getGenderType(),
			signupRequestDto.getAge(),
			signupRequestDto.getFamilyComposition(),
			signupRequestDto.getPreferredTime(),
			signupRequestDto.getPreferredPeople(),
			signupRequestDto.getInterest());

		MemberAuthority memberAuthority = new MemberAuthority();
		memberAuthority.changeMember(member);
		memberAuthority.changeAuthority(authority);

		Member saveMember = memberRepository.save(member);
		memberAuthorityRepository.save(memberAuthority);

		String interest = new StringTokenizer(member.getInterest()).nextToken().replace(",", "");

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signupRequestDto.getEmail(), signupRequestDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		String accessToken = tokenProvider.createToken(authentication, TokenType.ACCESS);
		String refreshToken = tokenProvider.createToken(authentication, TokenType.REFRESH);

		//refresh 토큰 관련 처리
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findById(signupRequestDto.getEmail());
		if(savedRefreshToken.isPresent()) {
			refreshTokenRepository.save(savedRefreshToken.get().updateToken(refreshToken));
		}
		else {
			RefreshToken newRefreshToken = new RefreshToken(signupRequestDto.getEmail(), refreshToken);
			refreshTokenRepository.save(newRefreshToken);
		}

		return SignUpResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.url("https://api.openai.com/v1/chat/completions")
			.gptKey("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")
			.model("gpt-4o-mini")
			.temperature("0.7F")
			.prompt("Please extract information as a JSON object. Please use English for the json key value. Please use Korean for the json value. 은퇴 후 제주도로 이주를 고민하는 사람들을 위한 모임을 생성하려고 합니다. 이 사람들의 공통 관심사는 다음과 같습니다.\n" +
				"주제: " + interest + "\n" +
				"위 주제를 바탕으로 다음 형식으로 제주 이주에 도움이 될 수 있는 모임을 제안해주세요. 모임은 화상 통화에서 이루어집니다. 모임을 주도하는 사람은 없으니 교육, 세미나 등의 내용은 제외하고 자유롭게 소통 가능한 주제로 만들어주세요. 주제에 대한 제목과 주제에 대한 설명을 해주세요. 아직 제주도에 정착하지 않은 이주 희망자들을 위한 모임임에 유의하세요. 추천활동에는 제3자의 개입이 없는 활동들로 5개만 추천해주세요. 마지막 활동으로는 서로 응원하고 격려하는 등 긍정적인 마무리가 좋습니다. 글은 순수 텍스트만 넣어주세요.\n" +
				"Do not include any explanations, only provide a RFC8259 compliant JSON response  following this format without deviation.\n" +
				"{\n" +
				"  \"meetingPurpose\": \"meetingPurpose title phrase\",\n" +
				"  \"explain\": \"explain about meeting purpose phrase\",\n" +
				"  \"recommendedActivities\": \"recommended activities text\"\n" +
				"}\n")
			.preferredTime(signupRequestDto.getPreferredTime())
			.interest(interest)
			.build();
	}

	@Transactional
	public TokenDto login(LoginRequestDto loginRequestDto) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		String accessToken = tokenProvider.createToken(authentication, TokenType.ACCESS);
		String refreshToken = tokenProvider.createToken(authentication, TokenType.REFRESH);

		//refresh 토큰 관련 처리
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findById(loginRequestDto.getEmail());
		if(savedRefreshToken.isPresent()) {
			refreshTokenRepository.save(savedRefreshToken.get().updateToken(refreshToken));
		}
		else {
			RefreshToken newRefreshToken = new RefreshToken(loginRequestDto.getEmail(), refreshToken);
			refreshTokenRepository.save(newRefreshToken);
		}

		return TokenDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(String currentMemberEmail) {

		refreshTokenRepository.findById(currentMemberEmail)
			.orElseThrow(() -> new ApiException("유효한 Refresh 토큰이 없습니다.", HttpStatus.UNAUTHORIZED));

		refreshTokenRepository.deleteById(currentMemberEmail);
		SecurityContextHolder.clearContext();
	}

	@Transactional
	public void updateFeedBack(FeedbackRequestDto feedbackRequestDto) {
		Optional<Member> member = memberRepository.findById(feedbackRequestDto.getMemberId());

		int n;
		if(feedbackRequestDto.getType().equals("+")) {
			n = 1;
		}
		else {
			n = -1;
		}

		if(feedbackRequestDto.getFeedBackType().equals("water")) {
			member.get().setFeedbackWater(member.get().getFeedbackWater() + n);
		}
		else if(feedbackRequestDto.getFeedBackType().equals("sun")) {
			member.get().setFeedbackSun(member.get().getFeedbackSun() + n);
		}
		else {
			member.get().setFeedbackManure(member.get().getFeedbackManure() + n);
		}
	}


}
