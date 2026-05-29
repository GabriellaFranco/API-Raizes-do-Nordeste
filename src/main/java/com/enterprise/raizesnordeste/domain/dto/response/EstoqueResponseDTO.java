package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EstoqueResponseDTO(
        Long id,
        Long idItem,
        String nomeItem,
        Long idUnidade,
        String nomeFantasiaUnidade,
        Integer quantidade,
        Integer quantidadeMinima,
        Boolean abaixoDoMinimo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {
}
