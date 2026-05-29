package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy,
        Set<String> perfis
) {}