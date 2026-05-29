package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.response.FidelidadeResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Fidelidade;
import org.springframework.stereotype.Component;

@Component
public class FidelidadeMapper {

    public FidelidadeResponseDTO toFidelidadeResponseDTO(Fidelidade fidelidade) {
        return FidelidadeResponseDTO.builder()
                .id(fidelidade.getId())
                .idCliente(fidelidade.getCliente().getId())
                .nomeCliente(fidelidade.getCliente().getUsuario().getNome())
                .pontosAcumulados(fidelidade.getPontosAcumulados())
                .totalGasto(fidelidade.getTotalGasto())
                .createdAt(fidelidade.getCreatedAt())
                .updatedAt(fidelidade.getUpdatedAt())
                .build();
    }
}
