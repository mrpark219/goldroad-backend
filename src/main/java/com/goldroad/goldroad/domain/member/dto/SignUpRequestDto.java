package com.goldroad.goldroad.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	@NotNull
	private String email;

	@NotNull
	private String password;

	@NotNull
	private String etc;
}
