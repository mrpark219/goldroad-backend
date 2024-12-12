package com.goldroad.goldroad.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	private String nickname;

	private String email;
	
	private String password;

	private String genderType;

	private String age;

	private String familyComposition;

	private String preferredTime;

	private String preferredPeople;

	private String interest;
}
