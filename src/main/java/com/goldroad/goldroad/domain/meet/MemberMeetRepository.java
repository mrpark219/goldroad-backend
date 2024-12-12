package com.goldroad.goldroad.domain.meet;

import com.goldroad.goldroad.domain.entity.Member;
import com.goldroad.goldroad.domain.entity.MemberMeet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberMeetRepository extends JpaRepository<MemberMeet, Long> {

	List<MemberMeet> findByMember(Member member);
}
