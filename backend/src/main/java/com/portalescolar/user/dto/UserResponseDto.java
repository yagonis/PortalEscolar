package com.portalescolar.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto (
        UUID id,
        String name,
        String email,
        String role, // Mapeado do Enum (ex: role.name())
        Boolean active,
        LocalDateTime createdAt
){
}
