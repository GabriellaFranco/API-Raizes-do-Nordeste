package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegistroRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve conter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
        )
        String senha,

        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento,

        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter entre 10 e 11 dígitos numéricos")
        String telefone,

        String endereco,

        @NotNull(message = "O consentimento é obrigatório")
        @AssertTrue(message = "É necessário aceitar os termos de uso para se cadastrar")
        Boolean statusConsentimento
) {
}
