package com.goldroad.goldroad.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnumConfig {

	@Bean
	public EnumMapper enumMapper() {

		EnumMapper enumMapper = new EnumMapper();

		return enumMapper;
	}
}
