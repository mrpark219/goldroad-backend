package com.goldroad.goldroad.domain.meet;

import com.goldroad.goldroad.domain.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetRepository extends JpaRepository<Meeting, Long> {
}
