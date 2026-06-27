package com.portalescolar.user.repository;

import com.portalescolar.user.entity.Role;
import com.portalescolar.user.entity.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Page<User> findByActiveTrue(Pageable pageable);
    boolean existsByEmail(String email);
    Page<User> findAllByRole(Role role, Pageable pageable);
}
