package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.ItemRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item toItem(ItemRequestDTO request) {
        return Item.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .categoria(request.categoria())
                .build();
    }

    public ItemResponseDTO toItemResponseDTO(Item item) {
        return ItemResponseDTO.builder()
                .id(item.getId())
                .nome(item.getNome())
                .categoria(item.getCategoria())
                .descricao(item.getDescricao())
                .preco(item.getPreco())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .createdBy(item.getCreatedBy())
                .updatedAt(item.getUpdatedAt())
                .updatedBy(item.getUpdatedBy())
                .build();
    }
}
