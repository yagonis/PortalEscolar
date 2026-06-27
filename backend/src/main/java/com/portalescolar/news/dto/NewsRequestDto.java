package com.portalescolar.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record NewsRequestDto(
        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 300)
        String subtitle,

        @NotBlank
        String body,

        @URL
        String imageUrl


) {
}
