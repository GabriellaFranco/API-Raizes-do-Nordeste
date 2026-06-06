package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CardapioResponseDTO(
        Long id,
        Long idUnidade,
        String nome,
        List<ItemCardapioResponseDTO> itens,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Boolean status

) {
}
