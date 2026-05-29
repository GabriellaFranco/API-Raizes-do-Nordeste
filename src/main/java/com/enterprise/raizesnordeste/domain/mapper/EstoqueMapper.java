package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.EstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.EstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Estoque;
import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import org.springframework.stereotype.Component;

@Component
public class EstoqueMapper {

    public Estoque toEstoque(EstoqueRequestDTO dto, Item item, Unidade unidade) {
        return Estoque.builder()
                .item(item)
                .unidade(unidade)
                .quantidade(dto.quantidade())
                .quantidadeMinima(dto.quantidadeMinima())
                .build();
    }

    public EstoqueResponseDTO toEstoqueResponseDTO(Estoque estoque) {
        return EstoqueResponseDTO.builder()
                .id(estoque.getId())
                .idItem(estoque.getItem().getId())
                .idUnidade(estoque.getItem().getId())
                .nomeFantasiaUnidade(estoque.getUnidade().getNomeFantasia())
                .quantidade(estoque.getQuantidade())
                .quantidadeMinima(estoque.getQuantidadeMinima())
                .abaixoDoMinimo(estoque.getQuantidade() <= estoque.getQuantidadeMinima())
                .createdAt(estoque.getCreatedAt())
                .createdBy(estoque.getCreatedBy())
                .updatedAt(estoque.getUpdatedAt())
                .updatedBy(estoque.getUpdatedBy())
                .build();
    }

}
