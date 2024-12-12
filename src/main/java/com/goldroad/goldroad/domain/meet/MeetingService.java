package com.goldroad.goldroad.domain.meet;

import com.goldroad.goldroad.domain.entity.Meeting;
import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.entity.MemberMeet;
import com.goldroad.goldroad.domain.member.MemberRepository;
import com.goldroad.goldroad.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MeetingService {

	private final MeetRepository meetRepository;
	private final MemberRepository memberRepository;
	private final MemberMeetRepository memberMeetRepository;

	public MeetingService(MeetRepository meetRepository, MemberRepository memberRepository, MemberMeetRepository memberMeetRepository) {
		this.meetRepository = meetRepository;
		this.memberRepository = memberRepository;
		this.memberMeetRepository = memberMeetRepository;
	}

	@Transactional
	public void update(Long meetingId) {

		Optional<String> currentMemberEmail = SecurityUtil.getCurrentMemberEmail();
		Optional<Member> member = memberRepository.findByEmail(currentMemberEmail.get());

		Optional<Meeting> meeting = meetRepository.findById(meetingId);

		MemberMeet byMemberAndMeeting = memberMeetRepository.findOneByMemberAndMeeting(member.get(), meeting.get());

		byMemberAndMeeting.setAttend(true);
	}
}
