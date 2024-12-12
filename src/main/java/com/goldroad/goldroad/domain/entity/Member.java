package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
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

	private String nickname;

	private String email;

	private String password;

	private String genderType;

	private String age;

	private String familyComposition;

	private String preferredTime;

	private String preferredPeople;
	private String interest;


	@OneToMany(mappedBy = "member")
	private Set<MemberAuthority> memberAuthorities = new HashSet<>();

	public Member(String nickname, String email, String password, String genderType, String age, String familyComposition, String preferredTime, String preferredPeople, String interest) {
		this.nickname = nickname;
		this.email = email;
		this.password = password;
		this.genderType = genderType;
		this.age = age;
		this.familyComposition = familyComposition;
		this.preferredTime = preferredTime;
		this.preferredPeople = preferredPeople;
		this.interest = interest;
	}

	@Override
	public String toString() {
		return "Member{" +
			"id=" + id +
			", genderType='" + genderType + '\'' +
			", age='" + age + '\'' +
			", familyComposition='" + familyComposition + '\'' +
			", preferredTime='" + preferredTime + '\'' +
			", preferredPeople='" + preferredPeople + '\'' +
			", interest='" + interest + '\'' +
			'}';
	}
}
