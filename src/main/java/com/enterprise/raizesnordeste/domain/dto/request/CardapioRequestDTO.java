package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record CardapioRequestDTO(

        @NotNull(message = "A unidade é obrigatória")
        Long idUnidade,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "Os itens são obrigatórios")
        @Size(min = 1, message = "O cardápio deve ter pelo menos um item")
        List<Long> idItens
) {
}
