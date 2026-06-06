package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ClienteResponseDTO (
        Long id,
        String nome,
        String email,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        String endereco,
        Boolean statusConsentimento,
        LocalDateTime dataConsentimento,
        LocalDateTime dataRevogacao,
        Boolean anonimizado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
){}

