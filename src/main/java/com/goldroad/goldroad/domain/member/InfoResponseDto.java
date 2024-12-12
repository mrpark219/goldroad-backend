package com.goldroad.goldroad.domain.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoResponseDto {

	private String nickname;

	private String email;

	private String password;

	private String genderType;

	private String age;

	private String familyComposition;

	private String preferredTime;

	private String preferredPeople;

	private String interest;

	private Long feedbackWater;

	private Long feedbackSun;

	private Long feedbackManure;

	private Long attendCount;
}
