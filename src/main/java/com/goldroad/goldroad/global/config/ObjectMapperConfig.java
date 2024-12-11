package com.goldroad.goldroad.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.TimeZone;

@Configuration
public class ObjectMapperConfig {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {

		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}

}

