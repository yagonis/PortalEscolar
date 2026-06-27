package com.portalescolar.poll.repository;
import com.portalescolar.poll.entity.Poll;
import com.portalescolar.poll.entity.PollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public interface PollRepository extends JpaRepository<Poll, UUID> {
    Page<Poll> findAllByStatus(PollStatus status, Pageable pageable);
    List<Poll> findAllByStatusAndClosesAtBefore(PollStatus status, LocalDateTime now);

}
