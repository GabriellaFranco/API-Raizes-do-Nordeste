package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MovimentacaoEstoqueResponseDTO(
        Long id,
        Long idEstoque,
        String nomeItem,
        String nomeFantasiaUnidade,
        Integer quantidade,
        String observacao,
        String responsavel,
        LocalDateTime createdAt
) {
}
