package com.goldroad.goldroad.global.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
//토큰의 생성, 토큰의 유효성 검증 등을 담당
public class TokenProvider implements InitializingBean {

	private static final String AUTHORITIES_KEY = "auth";
	private final String secret;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;
	private SecretKey key;
	private final RefreshTokenRepository refreshTokenRepository;

	public TokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds, @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds, RefreshTokenRepository refreshTokenRepository) {
		this.secret = secret;
		this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	//빈이 생성되고 주입 받은 후에 secret 값을 Base64 Decode해서 key 변수에 할당
	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	//Authentication 객체의 권한정보를 이용해서 토큰을 생성
	public String createToken(Authentication authentication, TokenType tokenType) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity = tokenType == TokenType.ACCESS ? new Date(now + this.accessTokenValidityInMilliseconds) : new Date(now + this.refreshTokenValidityInMilliseconds);

		return Jwts.builder()
			.subject(authentication.getName())
			.claim(AUTHORITIES_KEY, authorities)
			.signWith(key)
			.expiration(validity)
			.compact();
	}

	//Token에 담겨있는 정보를 이용해 Authentication 객체를 리턴
	public Authentication getAuthentication(String token) {

		Claims claims = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload();

		//권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	// 어세스 토큰 헤더 설정
	public void setHeaderAccessToken(HttpServletResponse response, String accessTokenHeader, String accessToken) {
		response.setHeader(accessTokenHeader, accessToken);
	}

	//토큰의 유효성 검증을 수행
	public boolean tokenValidation(String token) {

		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		}
		catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("잘못된 JWT 서명입니다.");
		}
		catch(ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		}
		catch(UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		}
		catch(IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}

	// 리프레시 토큰 검증
	public boolean validateRefreshToken(String refreshToken) {

		if(!tokenValidation(refreshToken)) {
			return false;
		}

		Authentication authentication = getAuthentication(refreshToken);
		Optional<RefreshToken> redisRefreshToken = refreshTokenRepository.findById(authentication.getName());

		return redisRefreshToken.isPresent() && refreshToken.equals(redisRefreshToken.get().getRefreshToken());
	}
}
