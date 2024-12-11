package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class MemberAuthority extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_authority_id")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "authority_id")
	private Authority authority;

	public void changeMember(Member member) {
		this.member = member;
		member.getMemberAuthorities().add(this);
	}

	public void changeAuthority(Authority authority) {
		this.authority = authority;
	}
}
