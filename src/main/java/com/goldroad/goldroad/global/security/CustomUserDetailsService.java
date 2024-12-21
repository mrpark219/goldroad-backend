package com.goldroad.goldroad.global.security;


import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.member.MemberJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberJpaRepository memberJpaRepository;

	public CustomUserDetailsService(MemberJpaRepository memberJpaRepository) {
		this.memberJpaRepository = memberJpaRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String email) {
		return memberJpaRepository.findMemberWithAuthoritiesByEmail(email)
			.map(this::createUser)
			.orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
	}

	private User createUser(Member member) {
		List<GrantedAuthority> grantedAuthorities = member.getMemberAuthorities().stream()
			.map(memberAuthority -> new SimpleGrantedAuthority(memberAuthority.getAuthority().getName()))
			.collect(Collectors.toList());
		return new User(member.getEmail(),
			member.getPassword(),
			grantedAuthorities);
	}
}
