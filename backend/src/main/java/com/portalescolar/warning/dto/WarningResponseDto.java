package com.portalescolar.warning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record WarningResponseDto(
        UUID id,
        String title,
        String content,
        String priority,
        Boolean active,
        Boolean pinned,
        LocalDateTime createdAt
) {
}
