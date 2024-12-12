package com.goldroad.goldroad.domain.member.dto;


import com.goldroad.goldroad.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class SignUpResponseDto {

	private String accessToken;

	private String refreshToken;

	private String url;

	private String gptKey;

	private String interest;

	private String model;

	private String temperature;

	private String prompt;

	private String preferredTime;

	public SignUpResponseDto() {
	}

	public SignUpResponseDto(String accessToken, String refreshToken, String url, String gptKey, String interest, String model, String temperature, String prompt, String preferredTime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.url = url;
		this.gptKey = gptKey;
		this.interest = interest;
		this.model = model;
		this.temperature = temperature;
		this.prompt = prompt;
		this.preferredTime = preferredTime;
	}
}
