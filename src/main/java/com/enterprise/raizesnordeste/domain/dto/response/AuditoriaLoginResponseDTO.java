package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditoriaLoginResponseDTO(
        Long id,
        Long idUsuario,
        String nomeUsuario,
        String email,
        String ip,
        Boolean sucesso,
        LocalDateTime dataHora
) {
}
