package com.goldroad.goldroad.domain.member.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponseDto {

	private String accessToken;

	private String refreshToken;

	public SignUpResponseDto() {
	}

	public SignUpResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
