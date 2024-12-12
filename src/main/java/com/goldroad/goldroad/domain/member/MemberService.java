package com.goldroad.goldroad.domain.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.goldroad.goldroad.domain.MemberAuthority.MemberAuthorityRepository;
import com.goldroad.goldroad.domain.authority.AuthorityRepository;
import com.goldroad.goldroad.domain.entity.*;
import com.goldroad.goldroad.domain.meet.MeetRepository;
import com.goldroad.goldroad.domain.meet.MemberMeetRepository;
import com.goldroad.goldroad.domain.member.dto.LoginRequestDto;
import com.goldroad.goldroad.domain.member.dto.SignUpRequestDto;
import com.goldroad.goldroad.domain.member.dto.SignUpResponseDto;
import com.goldroad.goldroad.domain.member.dto.TokenDto;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.security.RefreshToken;
import com.goldroad.goldroad.global.security.RefreshTokenRepository;
import com.goldroad.goldroad.global.security.TokenProvider;
import com.goldroad.goldroad.global.security.TokenType;
import com.goldroad.goldroad.global.util.GptResponseDto;
import com.goldroad.goldroad.global.util.GptUtil;
import com.goldroad.goldroad.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Service
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthorityRepository authorityRepository;
	private final MemberAuthorityRepository memberAuthorityRepository;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MeetRepository meetRepository;
	private final MemberMeetRepository memberMeetRepository;

	public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AuthorityRepository authorityRepository, MemberAuthorityRepository memberAuthorityRepository, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, MeetRepository meetRepository, MemberMeetRepository memberMeetRepository) {
		this.passwordEncoder = passwordEncoder;
		this.memberRepository = memberRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.authorityRepository = authorityRepository;
		this.memberAuthorityRepository = memberAuthorityRepository;
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.meetRepository = meetRepository;
		this.memberMeetRepository = memberMeetRepository;
	}

	@Transactional
	public SignUpResponseDto signup(SignUpRequestDto signupRequestDto) throws JsonProcessingException {

		if(memberRepository.findByEmail(signupRequestDto.getEmail()).orElse(null) != null) {
			throw new ApiException("이미 가입되어 있는 유저입니다.", HttpStatus.CONFLICT);
		}
		Authority authority = authorityRepository.findByName("ROLE_USER")
			.orElseThrow(() -> new RuntimeException("Role Not Found"));

		Member member = new Member(
			signupRequestDto.getNickname(),
			signupRequestDto.getEmail(),
			passwordEncoder.encode(signupRequestDto.getPassword()),
			signupRequestDto.getGenderType(),
			signupRequestDto.getAge(),
			signupRequestDto.getFamilyComposition(),
			signupRequestDto.getPreferredTime(),
			signupRequestDto.getPreferredPeople(),
			signupRequestDto.getInterest());

		MemberAuthority memberAuthority = new MemberAuthority();
		memberAuthority.changeMember(member);
		memberAuthority.changeAuthority(authority);

		Member saveMember = memberRepository.save(member);
		memberAuthorityRepository.save(memberAuthority);

		String interest = new StringTokenizer(member.getInterest()).nextToken().replace(",", "");

		GptResponseDto match = GptUtil.match(interest);

		Meeting meeting = new Meeting(match.getMeetingPurpose(), match.getExplain(), match.getRecommendedActivities(), member.getPreferredTime(), interest);
		meetRepository.save(meeting);

		List<Member> byInterestLikeAndPreferredPeople = memberRepository.findByInterestLikeAndPreferredPeople("%" + interest + "%", member.getPreferredPeople());

		byInterestLikeAndPreferredPeople.forEach(member1 -> {
			MemberMeet memberMeet = new MemberMeet(member1, meeting, false);
			memberMeetRepository.save(memberMeet);

		});

		return SignUpResponseDto.form(saveMember);
	}

	@Transactional
	public TokenDto login(LoginRequestDto loginRequestDto) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		String accessToken = tokenProvider.createToken(authentication, TokenType.ACCESS);
		String refreshToken = tokenProvider.createToken(authentication, TokenType.REFRESH);

		//refresh 토큰 관련 처리
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findById(loginRequestDto.getEmail());
		if(savedRefreshToken.isPresent()) {
			refreshTokenRepository.save(savedRefreshToken.get().updateToken(refreshToken));
		}
		else {
			RefreshToken newRefreshToken = new RefreshToken(loginRequestDto.getEmail(), refreshToken);
			refreshTokenRepository.save(newRefreshToken);
		}

		return TokenDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(String currentMemberEmail) {

		refreshTokenRepository.findById(currentMemberEmail)
			.orElseThrow(() -> new ApiException("유효한 Refresh 토큰이 없습니다.", HttpStatus.UNAUTHORIZED));

		refreshTokenRepository.deleteById(currentMemberEmail);
		SecurityContextHolder.clearContext();
	}

	@Transactional
	public void updateFeedBack(FeedbackRequestDto feedbackRequestDto) {
		Optional<String> currentMemberEmail = SecurityUtil.getCurrentMemberEmail();

		Optional<Member> member = memberRepository.findByEmail(currentMemberEmail.get());

		int n;
		if(feedbackRequestDto.getType().equals("+")) {
			n = 1;
		}
		else {
			n = -1;
		}

		if(feedbackRequestDto.getFeedBackType().equals("water")) {
			member.get().setFeedbackWater(member.get().getFeedbackWater() + n);
		}
		else if(feedbackRequestDto.getFeedBackType().equals("sun")) {
			member.get().setFeedbackSun(member.get().getFeedbackSun() + n);
		}
		else {
			member.get().setFeedbackManure(member.get().getFeedbackManure() + n);
		}
	}


}
