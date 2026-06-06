package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UsuarioUpdateRequestDTO(

        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @Email(message = "Email inválido")
        String email,

        Long idUnidade
) {}