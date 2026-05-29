package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PerfilAutoridadeResponseDTO(
        String nome,
        String descricao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        boolean status
){}
