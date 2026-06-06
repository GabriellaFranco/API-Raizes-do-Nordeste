package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
        @NotBlank(message = "Refresh token é obrigatório")
        String refreshToken
) { }
