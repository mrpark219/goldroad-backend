package com.goldroad.goldroad.domain.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.entity.MemberMeet;
import com.goldroad.goldroad.domain.meet.MemberMeetRepository;
import com.goldroad.goldroad.domain.member.dto.*;
import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class MemberController {

	private final MemberService memberService;

	public final String ACCESS_TOKEN_HEADER;

	public final String REFRESH_TOKEN_HEADER;
	private final MemberRepository memberRepository;
	private final MemberMeetRepository memberMeetRepository;

	public MemberController(MemberService memberService, @Value("${jwt.access-header}") String accessTokenHeader, @Value("${jwt.refresh-header}") String refreshTokenHeader, MemberRepository memberRepository, MemberMeetRepository memberMeetRepository) {
		this.memberService = memberService;
		ACCESS_TOKEN_HEADER = accessTokenHeader;
		REFRESH_TOKEN_HEADER = refreshTokenHeader;
		this.memberRepository = memberRepository;
		this.memberMeetRepository = memberMeetRepository;
	}

	@PostMapping("/sign-up")
	public ResponseEntity<TokenDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) throws JsonProcessingException {
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

	@PostMapping("/feedback")
	public void feedback(@RequestBody FeedbackRequestDto feedbackRequestDto) {

		memberService.updateFeedBack(feedbackRequestDto);
	}

	@GetMapping("")
	public InfoResponseDto myInfo() {

		Optional<Member> byEmail = memberRepository.findByEmail(SecurityUtil.getCurrentMemberEmail().get());

		List<MemberMeet> byMemberAndAttend = memberMeetRepository.findByMemberAndAttend((byEmail.get()), true);

		int size = byMemberAndAttend.size();

		return InfoResponseDto.builder()
			.nickname(byEmail.get().getNickname())
			.email(byEmail.get().getEmail())
			.genderType(byEmail.get().getGenderType())
			.age(byEmail.get().getAge())
			.familyComposition(byEmail.get().getFamilyComposition())
			.preferredTime(byEmail.get().getPreferredTime())
			.preferredPeople(byEmail.get().getPreferredPeople())
			.interest(byEmail.get().getInterest())
			.feedbackWater(byEmail.get().getFeedbackWater())
			.feedbackSun(byEmail.get().getFeedbackSun())
			.feedbackManure(byEmail.get().getFeedbackManure())
			.attendCount((long) size)
			.build();
	}
}
