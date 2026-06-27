package com.portalescolar.warning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record WarningRequestDto(
        @NotBlank
        @Size(max = 150)
        String title,
        @NotBlank
        String content,
        @NotNull
        String priority,
        @NotNull
        Boolean pinned
) {
}
