package com.goldroad.goldroad.global.config;

import lombok.Getter;

@Getter
public class EnumValue {

	private final String key;
	private final String value;

	public EnumValue(EnumModel enumModel) {
		this.key = enumModel.getKey();
		this.value = enumModel.getValue();
	}
}
