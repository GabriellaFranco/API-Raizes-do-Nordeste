package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FidelidadeResponseDTO(
        Long id,
        Long idCliente,
        String nomeCliente,
        Integer pontosAcumulados,
        BigDecimal totalGasto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
