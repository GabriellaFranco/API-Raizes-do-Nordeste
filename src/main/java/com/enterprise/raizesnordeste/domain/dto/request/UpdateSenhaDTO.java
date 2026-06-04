package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdateSenhaDTO(

        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
        )
        String novaSenha
) {}
