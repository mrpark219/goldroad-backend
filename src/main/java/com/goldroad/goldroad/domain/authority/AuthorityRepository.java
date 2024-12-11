package com.goldroad.goldroad.domain.authority;

import com.goldroad.goldroad.domain.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	Optional<Authority> findByName(String name);
}
