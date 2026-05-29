package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.UnidadeRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UnidadeResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMapper {

    public Unidade toUnidade(UnidadeRequestDTO dto) {
        return Unidade.builder()
                .nomeFantasia(dto.nomeFantasia())
                .cnpj(dto.cnpj())
                .endereco(dto.endereco())
                .contato(dto.contato())
                .estado(dto.estado())
                .regiao(dto.regiao())
                .build();
    }

    public UnidadeResponseDTO toUnidadeResponseDTO(Unidade unidade) {
        return UnidadeResponseDTO.builder()
                .id(unidade.getId())
                .nomeFantasia(unidade.getNomeFantasia())
                .cnpj(unidade.getCnpj())
                .endereco(unidade.getEndereco())
                .contato(unidade.getContato())
                .estado(unidade.getEstado())
                .regiao(unidade.getRegiao())
                .status(unidade.getStatus())
                .createdAt(unidade.getCreatedAt())
                .createdBy(unidade.getCreatedBy())
                .updatedAt(unidade.getUpdatedAt())
                .updatedBy(unidade.getUpdatedBy())
                .build();
    }
}
