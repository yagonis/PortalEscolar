package com.portalescolar.warning.repository;

import com.portalescolar.warning.entity.Priority;
import com.portalescolar.warning.entity.Warning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WarningRepository extends JpaRepository<Warning, UUID> {
    Page<Warning> findAllByActiveTrue(Pageable pageable);
    List<Warning>findAllByActiveTrueAndPinnedTrue();
    Page<Warning> findAllByActiveTrueAndPriority(Priority priority, Pageable pageable);
    Page<Warning> findAllByActiveTrueAndCreatedAtBetween(LocalDateTime begin, LocalDateTime end, Pageable pageable);


}
