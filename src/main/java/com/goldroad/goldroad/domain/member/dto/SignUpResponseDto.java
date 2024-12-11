package com.goldroad.goldroad.domain.member.dto;


import com.goldroad.goldroad.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpResponseDto {

	private String email;

	private String etc;

	public SignUpResponseDto(String email, String etc) {
		this.email = email;
		this.etc = etc;
	}

	public static SignUpResponseDto form(Member member) {
		return new SignUpResponseDto(member.getEmail(), member.getEtc());
	}
}
