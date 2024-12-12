package com.goldroad.goldroad.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldroad.goldroad.domain.entity.Member;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.swing.*;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class GptUtil {

	private static final String URL = "https://api.openai.com/v1/chat/completions";

	private static final String API_KEY = "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";

	public static GptResponseDto match(String subject) throws JsonProcessingException {
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

		if (matcher.find()) {
			String extractedContent = matcher.group(1).replace("\\n", "").replace("\\", ""); // 추출한 content 부분
			// content 내에서 키와 값을 동적으로 처리하는 방법 (예: 모든 value 값만 추출)
			ObjectMapper objectMapper = new ObjectMapper();
			GptResponseDto gptResponseDto = objectMapper.readValue(extractedContent, GptResponseDto.class);

			return gptResponseDto;
		}

		return null;
	}
}
