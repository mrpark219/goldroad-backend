package com.goldroad.goldroad.global.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldroad.goldroad.global.dto.GlobalResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;

	public final String ACCESS_TOKEN_HEADER;

	public final String REFRESH_TOKEN_HEADER;

	public JwtFilter(TokenProvider tokenProvider, String accessTokenHeader, String refreshTokenHeader) {
		this.tokenProvider = tokenProvider;
		this.ACCESS_TOKEN_HEADER = accessTokenHeader;
		this.REFRESH_TOKEN_HEADER = refreshTokenHeader;
	}

	//토큰의 인증정보를 Security Context에 저장하는 역할 수행
	@Override
	public void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {

		String accessToken = resolveToken(request, TokenType.ACCESS);
		String refreshToken = resolveToken(request, TokenType.REFRESH);

		String requestURI = request.getRequestURI();

		if(accessToken != null) {
			if(tokenProvider.tokenValidation(accessToken)) {
				Authentication authentication = tokenProvider.getAuthentication(accessToken);
				//SecurityContext에 Authentication 객체 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
				log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
			}
			else if(refreshToken != null) {
				if(tokenProvider.validateRefreshToken(refreshToken)) {
					Authentication authentication = tokenProvider.getAuthentication(refreshToken);
					String newAccessToken = tokenProvider.createToken(authentication, TokenType.ACCESS);
					tokenProvider.setHeaderAccessToken(response, ACCESS_TOKEN_HEADER, newAccessToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
				}
				else {
					jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	//Request Header에서 토큰 정보를 꺼내옴
	private String resolveToken(HttpServletRequest request, TokenType tokenType) {
		return tokenType == TokenType.ACCESS ? request.getHeader(ACCESS_TOKEN_HEADER) : request.getHeader(REFRESH_TOKEN_HEADER);
	}

	public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
		response.setStatus(status.value());
		response.setContentType("application/json");
		try {
			String json = new ObjectMapper().writeValueAsString(new GlobalResponseDto(msg, status.value()));
			response.getWriter().write(json);
		}
		catch(Exception e) {
			log.error(e.getMessage());
		}
	}
}