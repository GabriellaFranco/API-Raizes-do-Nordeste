package com.enterprise.raizesnordeste.domain.dto.response;

import lombok.Builder;

@Builder
public record RegistroResponseDTO(
        Long idUsuario,
        Long idCliente,
        String nome,
        String email
) {
}
