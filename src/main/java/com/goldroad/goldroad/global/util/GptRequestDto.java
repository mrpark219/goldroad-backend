package com.goldroad.goldroad.global.util;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class GptRequestDto {

	private String model;

	private ResponseFormat response_format;

	private List<GptRole> messages = new ArrayList<>();

	private Float temperature;

	public GptRequestDto(String model, Float temperature) {
		this.model = model;
		this.response_format = new ResponseFormat("json_object");
		this.temperature = temperature;
	}
}
