package com.portalescolar.news.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "subtitle", length = 200)
    private String subtitle;

    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "TEXT", nullable = false)
    private NewsStatus newsStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void publish() {
        this.newsStatus = NewsStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void archive() {
        this.newsStatus = NewsStatus.ARCHIVED;
    }

    public void backToDraft() {
        this.newsStatus = NewsStatus.DRAFT;
        this.publishedAt = null;
    }

    public boolean isPublished() {
        return this.newsStatus == NewsStatus.PUBLISHED;
    }

    public boolean isDraft() {
        return this.newsStatus == NewsStatus.DRAFT;
    }

    
}
