package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ClienteRequestDTO(

        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento,

        @Pattern(regexp = "\\d{10,11}", message = "O telefone informado deve conter entre 10 e 11 dígitos numéricos")
        String telefone,

        String endereco,

        @NotNull(message = "O consentimento para armazenamento de dados é obrigatório")
        Boolean statusConsentimento
) {}
