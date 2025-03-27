package com.goldroad.goldroad.domain.authority;

import lombok.Getter;

@Getter
public enum BasicAuthorityType {

	ROLE_USER("모든 사용자 유저가 가지고 있는 권한입니다."),
	ROLE_ADMIN("모든 관리자 유저가 가지고 있는 권한입니다.");

	private final String description;

	BasicAuthorityType(String description) {
		this.description = description;
	}
}
