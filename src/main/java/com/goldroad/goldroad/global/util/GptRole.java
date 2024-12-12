package com.goldroad.goldroad.global.util;

import lombok.Data;

@Data
public class GptRole {

	private String role;

	private String content;

	public GptRole(String role, String content) {
		this.role = role;
		this.content = content;
	}
}
