package com.goldroad.goldroad.domain.member.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {

	private String accessToken;

	private String refreshToken;

	public LoginResponseDto() {
	}

	public LoginResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
