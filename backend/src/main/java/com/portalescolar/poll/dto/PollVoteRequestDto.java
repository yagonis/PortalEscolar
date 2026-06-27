package com.portalescolar.poll.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PollVoteRequestDto(
        @NotNull
        UUID optionId
){
}
