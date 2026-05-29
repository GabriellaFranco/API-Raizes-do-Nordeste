package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record MovimentacaoEstoqueRequestDTO(

        @NotNull(message = "O id do estoque é obrigatório")
        Long idEstoque,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser positiva")
        Integer quantidade,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacao
) {
}
