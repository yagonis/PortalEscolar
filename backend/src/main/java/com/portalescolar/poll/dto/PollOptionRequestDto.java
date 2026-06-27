package com.portalescolar.poll.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PollOptionRequestDto(
        @NotBlank
        String text,
        @NotNull @Min(1)
        Integer displayOrder
) {
}
