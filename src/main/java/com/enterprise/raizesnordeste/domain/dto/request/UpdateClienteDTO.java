package com.enterprise.raizesnordeste.domain.dto.request;

import jakarta.validation.constraints.Pattern;

public record UpdateClienteDTO(

        @Pattern(regexp = "\\d{10,11}", message = "O telefone informado deve conter entre 10 e 11 dígitos numéricos")
        String telefone,

        String endereco

) {
}
