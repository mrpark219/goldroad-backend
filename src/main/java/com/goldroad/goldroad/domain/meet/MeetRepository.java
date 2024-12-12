package com.goldroad.goldroad.domain.meet;

import com.goldroad.goldroad.domain.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meeting, Long> {
	Optional<Meeting> findByIdAndCreatedDateAfter(Long id, LocalDateTime createdDateAfter);

	Meeting findByIdAndCreatedDateBefore(Long id, LocalDateTime createdDateBefore);

	List<Meeting> findAllByOrderByCreatedDateDesc();
}
