package com.portalescolar.poll.dto;

import java.util.UUID;

public record PollOptionResponseDto(
        UUID id,
        String text,
        Integer displayOrder
) {
}
