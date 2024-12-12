package com.goldroad.goldroad.domain.member;

import lombok.Data;

@Data
public class FeedbackRequestDto {

	private String feedBackType;

	private String type;

	private Long memberId;
}
