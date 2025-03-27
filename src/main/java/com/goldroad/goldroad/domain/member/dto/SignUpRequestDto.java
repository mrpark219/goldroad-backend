package com.goldroad.goldroad.domain.member.dto;

import lombok.Builder;
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

	public SignUpRequestDto() {
	}

	@Builder
	public SignUpRequestDto(String nickname, String email, String password, String genderType, String age, String familyComposition, String preferredTime, String preferredPeople, String interest) {
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.genderType = genderType;
		this.age = age;
		this.familyComposition = familyComposition;
		this.preferredTime = preferredTime;
		this.preferredPeople = preferredPeople;
		this.interest = interest;
	}
}
