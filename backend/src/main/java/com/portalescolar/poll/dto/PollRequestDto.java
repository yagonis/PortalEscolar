package com.portalescolar.poll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PollRequestDto(
        @NotBlank @Size(max = 500)
        String question,
        String description,
        @NotNull
        LocalDateTime opensAt,
        @NotNull
        LocalDateTime closesAt,
        Boolean allowMultipleVotes,
        @NotNull @Size(min = 2)
        List<PollOptionRequestDto> options) {
}
