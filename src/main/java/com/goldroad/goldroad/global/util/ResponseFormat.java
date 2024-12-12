package com.goldroad.goldroad.global.util;

import lombok.Data;

@Data
public class ResponseFormat {

	private String type;

	public ResponseFormat(String type) {
		this.type = type;
	}
}
