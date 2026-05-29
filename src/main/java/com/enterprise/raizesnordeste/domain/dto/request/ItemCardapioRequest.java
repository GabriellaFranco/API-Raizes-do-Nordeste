package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ItemCardapioRequest(

        @NotNull(message = "O item é obrigatório")
        Long idItem,

        @NotNull(message = "A disponibilidade é obrigatória")
        Boolean disponivel
) {
}
