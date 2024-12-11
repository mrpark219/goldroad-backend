package com.goldroad.goldroad.domain.member;

import com.goldroad.goldroad.domain.member.dto.*;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;

	public final String ACCESS_TOKEN_HEADER;

	public final String REFRESH_TOKEN_HEADER;

	public MemberController(MemberService memberService, @Value("${jwt.access-header}") String accessTokenHeader, @Value("${jwt.refresh-header}") String refreshTokenHeader) {
		this.memberService = memberService;
		ACCESS_TOKEN_HEADER = accessTokenHeader;
		REFRESH_TOKEN_HEADER = refreshTokenHeader;
	}

	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
		return ResponseEntity.ok(memberService.signup(signUpRequestDto));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {

		TokenDto tokenDto = memberService.login(loginRequestDto);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(ACCESS_TOKEN_HEADER, tokenDto.getAccessToken());
		httpHeaders.add(REFRESH_TOKEN_HEADER, tokenDto.getRefreshToken());

		return new ResponseEntity<>(new LoginResponseDto(tokenDto.getAccessToken(), tokenDto.getRefreshToken()), httpHeaders, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {

		String currentMemberEmail = SecurityUtil.getCurrentMemberEmail()
			.orElseThrow(() -> new ApiException("로그인되지 않은 회원입니다.", HttpStatus.UNAUTHORIZED));

		memberService.logout(currentMemberEmail);

		return ResponseEntity.ok().build();
	}
}
