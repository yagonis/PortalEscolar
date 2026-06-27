package com.portalescolar.auth.dto;

import com.portalescolar.user.dto.UserResponseDto;

import java.time.LocalDateTime;

public record LoginResponseDto(
        String token,
        String type,        // sempre "Bearer"
        LocalDateTime expiresAt,
        UserResponseDto user
) {
}
