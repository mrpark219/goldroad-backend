package com.goldroad.goldroad.domain.meet;

import com.goldroad.goldroad.domain.entity.Meeting;
import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.entity.MemberMeet;
import com.goldroad.goldroad.domain.member.MemberRepository;
import com.goldroad.goldroad.global.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {

	private final MeetRepository meetRepository;
	private final MemberRepository memberRepository;
	private final MemberMeetRepository memberMeetRepository;
	private final MeetingService meetingService;

	public MeetingController(MeetRepository meetRepository, MemberRepository memberRepository, MemberMeetRepository memberMeetRepository, MeetingService meetingService) {
		this.meetRepository = meetRepository;
		this.memberRepository = memberRepository;
		this.memberMeetRepository = memberMeetRepository;
		this.meetingService = meetingService;
	}

	@GetMapping("")
	public List<Meeting> listAllMeeting() {
		return meetRepository.findAllByOrderByCreatedDateDesc();
	}

	@GetMapping("/now")
	public List<Meeting> listNowMeeting() {
		Optional<String> currentMemberEmail = SecurityUtil.getCurrentMemberEmail();
		Optional<Member> member = memberRepository.findByEmail(currentMemberEmail.get());

		List<MemberMeet> byMember = memberMeetRepository.findByMember(member.get());

		List<Meeting> meetings = new ArrayList<>();
		for(MemberMeet mm : byMember) {
			Optional<Meeting> meeting = meetRepository.findByIdAndCreatedDateAfter((mm.getMeeting().getId()), LocalDateTime.now().minusDays(1L));
			meetings.add(meeting.get());
		}

		return meetings;
	}

	@GetMapping("/past")
	public List<Meeting> listPastMeeting() {
		Optional<String> currentMemberEmail = SecurityUtil.getCurrentMemberEmail();
		Optional<Member> member = memberRepository.findByEmail(currentMemberEmail.get());

		List<MemberMeet> byMember = memberMeetRepository.findByMember(member.get());

		List<Meeting> meetings = new ArrayList<>();
		for(MemberMeet mm : byMember) {
			Optional<Meeting> meeting = meetRepository.findByIdAndCreatedDateBefore((mm.getMeeting().getId()), LocalDateTime.now());
			meetings.add(meeting.get());
		}

		return meetings;
	}

	@PostMapping("/join")
	public void join(@RequestBody JoinRequestDto joinRequestDto) {

		meetingService.update(joinRequestDto.getMeetingId());
	}
}
