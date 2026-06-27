package com.portalescolar.poll.dto;

import java.util.List;
import java.util.UUID;
public record PollResultResponseDto(
        UUID pollId,
        String question,
        String status,
        Integer totalVotes,
        Boolean closed,
        List<PollOptionResultDto> options
) {
}
