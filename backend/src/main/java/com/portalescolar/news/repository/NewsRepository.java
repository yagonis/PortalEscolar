package com.portalescolar.news.repository;

import com.portalescolar.news.entity.News;

import com.portalescolar.news.entity.NewsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {

    Page<News> findAllByNewsStatus(NewsStatus status, Pageable pageable);

    Page<News> findAllByNewsStatusOrderByPublishedAtDesc(NewsStatus status, Pageable pageable);

    Optional<News> findAllByIdAndNewsStatus(UUID id, NewsStatus status);
}
