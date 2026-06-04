package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.ItemCardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemCardapioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cardapio;
import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.entity.ItemCardapio;
import org.springframework.stereotype.Component;

@Component
public class ItemCardapioMapper {

    public ItemCardapio toItemCardapio(ItemCardapioRequestDTO request, Item item, Cardapio cardapio) {
        return ItemCardapio.builder()
                .item(item)
                .cardapio(cardapio)
                .disponivel(request.disponivel())
                .build();
    }

    public ItemCardapioResponseDTO toItemCardapioResponseDTO(ItemCardapio itemCardapio) {
        return ItemCardapioResponseDTO.builder()
                .id(itemCardapio.getId())
                .idItem(itemCardapio.getItem().getId())
                .nomeItem(itemCardapio.getItem().getNome())
                .categoriaItem(itemCardapio.getItem().getCategoria())
                .descricaoItem(itemCardapio.getItem().getDescricao())
                .precoItem(itemCardapio.getItem().getPreco())
                .disponivel(itemCardapio.getDisponivel())
                .createdAt(itemCardapio.getCreatedAt())
                .createdBy(itemCardapio.getCreatedBy())
                .updatedAt(itemCardapio.getUpdatedAt())
                .updatedBy(itemCardapio.getUpdatedBy())
                .build();
    }
}
