package com.goldroad.goldroad.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GptRequestDto {

	private String result;

	private String interest;

	private String preferredTime;
}
