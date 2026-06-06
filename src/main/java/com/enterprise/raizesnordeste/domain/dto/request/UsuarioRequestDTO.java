package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UsuarioRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "o nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
        )        String senha,

        @NotNull(message = "Unidade é obrigatória")
        Long idUnidade,

        @NotNull(message = "Perfil é obrigatório")
        Long idPerfil) {}
