package com.enterprise.raizesnordeste.domain.dto.request;

import com.enterprise.raizesnordeste.domain.enuns.Estado;
import com.enterprise.raizesnordeste.domain.enuns.Regiao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UnidadeRequestDTO(

        @NotBlank(message = "O nome fantasia é obrigatório")
        @Size(max = 100, message = "O nome fantasia deve ter no máximo 100 caracteres")
        String nomeFantasia,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos")
        String cnpj,

        @NotBlank(message = "O endereço é obrigatório")
        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres")
        String endereco,

        @NotBlank(message = "A informação de contato é obrigatória")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone de contato deve conter entre 10 e 11 dígitos numéricos")
        String contato,

        @NotNull(message = "Estado é obrigatório")
        Estado estado,

        @NotNull(message = "Região é obrigatória")
        Regiao regiao
) {
}
