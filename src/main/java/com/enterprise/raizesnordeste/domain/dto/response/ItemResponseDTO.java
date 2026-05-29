package com.enterprise.raizesnordeste.domain.dto.response;

import com.enterprise.raizesnordeste.domain.enuns.CategoriaItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ItemResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        CategoriaItem categoria,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Boolean status
) {}
