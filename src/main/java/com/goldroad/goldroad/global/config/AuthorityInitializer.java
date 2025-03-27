package com.goldroad.goldroad.global.config;

import com.goldroad.goldroad.domain.authority.AuthorityRepository;
import com.goldroad.goldroad.domain.authority.BasicAuthorityType;
import com.goldroad.goldroad.domain.entity.Authority;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AuthorityInitializer implements ApplicationRunner {

	private final AuthorityRepository authorityRepository;

	public AuthorityInitializer(AuthorityRepository authorityRepository) {
		this.authorityRepository = authorityRepository;
	}

	@Override
	public void run(ApplicationArguments args) {

		// 사용자 기본 권한 세팅
		if(!authorityRepository.existsByName(BasicAuthorityType.ROLE_USER.name())) {
			authorityRepository.save(new Authority(BasicAuthorityType.ROLE_USER.name(), BasicAuthorityType.ROLE_USER.getDescription()));
		}

		// 관리자 기본 권한 세팅
		if(!authorityRepository.existsByName(BasicAuthorityType.ROLE_ADMIN.name())) {
			authorityRepository.save(new Authority(BasicAuthorityType.ROLE_ADMIN.name(), BasicAuthorityType.ROLE_ADMIN.getDescription()));
		}
	}
}
