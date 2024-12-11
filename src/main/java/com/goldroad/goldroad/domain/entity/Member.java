package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String email;

	private String password;

	private String etc;

	@OneToMany(mappedBy = "member")
	private Set<MemberAuthority> memberAuthorities = new HashSet<>();

	public Member(String email, String password, String etc) {
		this.email = email;
		this.password = password;
		this.etc = etc;
	}
}
