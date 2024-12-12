package com.goldroad.goldroad.domain.member.dto;


import com.goldroad.goldroad.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpResponseDto {

	private String email;

	private String genderType;

	private String age;

	private String familyComposition;

	private String preferredTime;

	private String preferredPeople;
	private String interest;

	public SignUpResponseDto(String email, String genderType, String age, String familyComposition, String preferredTime, String preferredPeople, String interest) {
		this.email = email;
		this.genderType = genderType;
		this.age = age;
		this.familyComposition = familyComposition;
		this.preferredTime = preferredTime;
		this.preferredPeople = preferredPeople;
		this.interest = interest;
	}

	public static SignUpResponseDto form(Member member) {
		return new SignUpResponseDto(member.getEmail(), member.getGenderType(), member.getAge(), member.getFamilyComposition(), member.getPreferredTime(), member.getPreferredPeople(), member.getInterest());
	}
}
