package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record EstoqueRequestDTO(

        @NotNull(message = "O item é obrigatório")
        Long idItem,

        @NotNull(message = "A unidade é obrigatória")
        Long idUnidade,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantidade,

        @NotNull(message = "A quantidade mínima é obrigatória")
        @Positive(message = "A quantidade mínima deve ser positiva")
        Integer quantidadeMinima
) {
}
