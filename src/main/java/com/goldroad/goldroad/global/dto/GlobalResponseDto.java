package com.goldroad.goldroad.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
public class GlobalResponseDto {

	private String message;
	private int statusCode;

	public GlobalResponseDto(String message, int statusCode) {
		this.message = message;
		this.statusCode = statusCode;
	}
}
