package com.goldroad.goldroad.domain.member;

import com.goldroad.goldroad.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByEmailAndPassword(String email, String password);

	List<Member> findByInterestLikeAndPreferredPeople(String interest, String preferredPeople);
}
