package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPedidoResponseDTO(
        Long id,
        Long idItem,
        String nomeItem,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}
