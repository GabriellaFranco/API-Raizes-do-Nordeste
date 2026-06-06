package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.CardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.CardapioResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemCardapioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cardapio;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CardapioMapper {

    private final ItemCardapioMapper itemCardapioMapper;

    public Cardapio toCardapio(CardapioRequestDTO request, Unidade unidade) {
        return Cardapio.builder()
                .unidade(unidade)
                .nome(request.nome())
                .build();
    }

    public CardapioResponseDTO toCardapioResponseDTO(Cardapio cardapio) {
        List<ItemCardapioResponseDTO> itens = cardapio.getItens()
                .stream()
                .map(itemCardapioMapper::toItemCardapioResponseDTO)
                .toList();

        return CardapioResponseDTO.builder()
                .id(cardapio.getId())
                .nome(cardapio.getNome())
                .idUnidade(cardapio.getUnidade().getId())
                .itens(itens)
                .status(cardapio.getAtivo())
                .createdAt(cardapio.getCreatedAt())
                .createdBy(cardapio.getCreatedBy())
                .updatedAt(cardapio.getUpdatedAt())
                .updatedBy(cardapio.getUpdatedBy())
                .build();

    }
}