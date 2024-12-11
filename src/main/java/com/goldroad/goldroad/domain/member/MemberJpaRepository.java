package com.goldroad.goldroad.domain.member;

import com.goldroad.goldroad.domain.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.goldroad.goldroad.domain.entity.QAuthority.authority;
import static com.goldroad.goldroad.domain.entity.QMember.member;
import static com.goldroad.goldroad.domain.entity.QMemberAuthority.memberAuthority;

@Repository
public class MemberJpaRepository {

	private final JPAQueryFactory queryFactory;

	public MemberJpaRepository(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	public Optional<Member> findMemberWithAuthoritiesByEmail(String email) {

		return Optional.ofNullable(queryFactory
			.selectFrom(member)
			.leftJoin(member.memberAuthorities, memberAuthority)
			.leftJoin(memberAuthority.authority, authority)
			.where(member.email.eq(email))
			.fetchOne()
		);
	}
}
