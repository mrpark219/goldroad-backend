package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class MemberMeet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_meet_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "meeting_id")
	private Meeting meeting;

	private Boolean attend;

	public MemberMeet(Member member, Meeting meeting, Boolean attend) {
		this.member = member;
		this.meeting = meeting;
		this.attend = attend;
	}
}
