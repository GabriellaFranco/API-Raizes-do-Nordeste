package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PerfilAutoridadeRequestDTO
        (
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
        String nome,

        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String descricao
) {
}
