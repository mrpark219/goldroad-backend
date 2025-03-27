package com.goldroad.goldroad.domain.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goldroad.goldroad.IntegrationTestSupport;
import com.goldroad.goldroad.domain.MemberAuthority.MemberAuthorityRepository;
import com.goldroad.goldroad.domain.authority.AuthorityRepository;
import com.goldroad.goldroad.domain.meet.MeetingService;
import com.goldroad.goldroad.domain.member.dto.SignUpRequestDto;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.security.RefreshTokenRepository;
import com.goldroad.goldroad.global.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;

@Transactional
class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private MemberAuthorityRepository memberAuthorityRepository;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@MockitoBean
	private MeetingService meetingService;

	@Autowired
	private MemberService memberService;

	@DisplayName("회원가입 시 중복된 이메일이 있다면 예외가 발생한다.")
	@Test
	void signup() throws JsonProcessingException {

		// given
		// meeting 생성 시 open AI API 요청이 있어 mock으로 대체한다.
		willDoNothing().given(meetingService)
			.createMeeting(anyString(), anyString(), anyString());

		String email = "test@test.com";
		SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
			.email(email)
			.password("password")
			.interest("test1,test2")
			.build();

		SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
			.email(email)
			.password("password")
			.interest("test1,test2")
			.build();

		memberService.signup(signUpRequestDto1);

		// when // then
		assertThatThrownBy(() -> memberService.signup(signUpRequestDto2))
			.isInstanceOf(ApiException.class)
			.hasMessage("이미 가입되어 있는 유저입니다.");
	}


}