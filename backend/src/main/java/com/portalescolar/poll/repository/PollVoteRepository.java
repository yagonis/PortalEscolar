package com.portalescolar.poll.repository;
import com.portalescolar.poll.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface PollVoteRepository extends JpaRepository<PollVote, UUID>{
    boolean existsByPollIdAndUserId(UUID pollId, UUID userId);
    int countByPollId(UUID pollId);
}
