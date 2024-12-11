package com.goldroad.goldroad.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

	private String accessToken;

	private String refreshToken;

	public TokenDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
