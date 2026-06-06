package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.CardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.CardapioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cardapio;
import com.enterprise.raizesnordeste.domain.entity.ItemCardapio;
import com.enterprise.raizesnordeste.domain.mapper.CardapioMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.CardapioRepository;
import com.enterprise.raizesnordeste.repository.ItemRepository;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ItemRepository itemRepository;
    private final CardapioMapper cardapioMapper;

    public Page<CardapioResponseDTO> getAll(Pageable pageable) {
        return cardapioRepository.findAll(pageable).map(cardapioMapper::toCardapioResponseDTO);
    }

    public CardapioResponseDTO getCardapioById(Long id) {
        return cardapioMapper.toCardapioResponseDTO(buscarCardapio(id));
    }

    public CardapioResponseDTO getCardapioByUnidade(Long idUnidade) {
        unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + idUnidade));

        return cardapioRepository.findByUnidadeIdAndAtivoTrue(idUnidade).map(cardapioMapper::toCardapioResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio ativo não encontrado para unidade: " + idUnidade));
    }

    @Transactional
    public CardapioResponseDTO createCardapio(CardapioRequestDTO request) {
        var unidade = unidadeRepository.findById(request.idUnidade())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + request.idUnidade()));

        if (cardapioRepository.existsByUnidadeIdAndAtivoTrue(request.idUnidade())) {
            throw new BusinessException("Unidade já possui um cardápio ativo");
        }

        var cardapio = cardapioMapper.toCardapio(request, unidade);
        cardapioRepository.save(cardapio);

        List<ItemCardapio> itens = request.idItens().stream().map(idItem -> {
                    var item = itemRepository.findById(idItem)
                            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + idItem));

                    return ItemCardapio.builder()
                            .cardapio(cardapio)
                            .item(item)
                            .disponivel(true)
                            .build();
                }).toList();

        cardapio.setItens(itens);
        cardapioRepository.save(cardapio);
        return cardapioMapper.toCardapioResponseDTO(cardapio);
    }

    @Transactional
    public CardapioResponseDTO updateCardapio(Long id, CardapioRequestDTO request) {
        var cardapio = buscarCardapio(id);

        if (!cardapio.getUnidade().getId().equals(request.idUnidade())) {
            throw new BusinessException("Não é possível alterar a unidade do cardápio");
        }

        cardapio.setNome(request.nome());
        cardapioRepository.save(cardapio);
        return cardapioMapper.toCardapioResponseDTO(cardapio);
    }

    @Transactional
    public void desativar(Long id) {
        var cardapio = buscarCardapio(id);

        if (!cardapio.getAtivo()) {
            throw new BusinessException("Cardápio já está inativo");
        }

        cardapio.setAtivo(false);
        cardapioRepository.save(cardapio);
    }

    private Cardapio buscarCardapio(Long id) {
        return cardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado: " + id));
    }
}

