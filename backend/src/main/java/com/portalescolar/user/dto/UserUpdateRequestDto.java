package com.portalescolar.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email
) {
}
