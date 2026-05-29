package com.enterprise.raizesnordeste.domain.dto.response;

import java.util.Set;

public record AuthResponseDTO(
        String token,
        String refreshToken,
        String email,
        String nome,
        Set<String> perfis
) {}
