package com.portalescolar.poll.dto;

import java.util.UUID;

public record PollOptionResultDto(
        UUID id,
        String text,
        Integer displayOrder,
        Integer totalVotes,
        Double percentage
) {
}
