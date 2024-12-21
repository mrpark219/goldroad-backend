package com.goldroad.goldroad.global.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//TokenProvider, JwtFilter를 SecurityConfig에 적용할 때 사용할 클래스
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final TokenProvider tokenProvider;

	private final String ACCESS_TOKEN_HEADER;

	private final String REFRESH_TOKEN_HEADER;

	private final String[] PERMITTED_PATHS;

	public JwtSecurityConfig(TokenProvider tokenProvider, String accessTokenHeader, String refreshTokenHeader, String[] permittedPaths) {
		this.tokenProvider = tokenProvider;
		ACCESS_TOKEN_HEADER = accessTokenHeader;
		REFRESH_TOKEN_HEADER = refreshTokenHeader;
		PERMITTED_PATHS = permittedPaths;
	}

	//Security 로직에 필터 등록
	@Override
	public void configure(HttpSecurity http) {
		JwtFilter customFilter = new JwtFilter(tokenProvider, ACCESS_TOKEN_HEADER, REFRESH_TOKEN_HEADER, PERMITTED_PATHS);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
}