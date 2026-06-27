package com.portalescolar.poll.repository;

import com.portalescolar.poll.entity.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PollOptionRepository extends JpaRepository<PollOption, UUID>{
    List<PollOption> findAllByPollIdOrderByDisplayOrder(UUID pollId);
    Optional<PollOption> findByIdAndPollId(UUID optionId, UUID pollId);
}
