package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ItemPedidoRequestDTO(

        @NotNull(message = "O item é obrigatório")
        Long idItem,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
