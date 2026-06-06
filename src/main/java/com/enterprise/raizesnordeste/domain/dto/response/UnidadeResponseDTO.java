package com.enterprise.raizesnordeste.domain.dto.response;

import com.enterprise.raizesnordeste.domain.enuns.Estado;
import com.enterprise.raizesnordeste.domain.enuns.Regiao;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UnidadeResponseDTO (
        Long id,
        String nomeFantasia,
        String cnpj,
        String endereco,
        String contato,
        Estado estado,
        Regiao regiao,
        Boolean status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
){
}
