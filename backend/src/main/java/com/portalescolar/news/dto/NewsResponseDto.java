package com.portalescolar.news.dto;

import java.util.UUID;

public record NewsResponseDto(
        UUID id,
        String title,
        String subtitle,
        String body,
        String imageUrl,
        String status,
        String publishedAt,
        String updatedAt
) {
}
