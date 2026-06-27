package com.portalescolar.poll.dto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
public record PollResponseDto(
        UUID id,
        String question,
        String description,
        String status,
        LocalDateTime opensAt,
        LocalDateTime closesAt,
        Boolean allowMultipleVotes,
        LocalDateTime createdAt,
        List<PollOptionResponseDto> options
) {
}
