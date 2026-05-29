package com.enterprise.raizesnordeste.domain.dto.response;

import com.enterprise.raizesnordeste.domain.enuns.CategoriaItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ItemCardapioResponseDTO(
        Long id,
        Long idItem,
        String nomeItem,
        String descricaoItem,
        BigDecimal precoItem,
        CategoriaItem categoriaItem,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Boolean disponivel
        ) {
}
