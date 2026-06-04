package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.ItemCardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemCardapioResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cardapio;
import com.enterprise.raizesnordeste.domain.entity.ItemCardapio;
import com.enterprise.raizesnordeste.domain.mapper.ItemCardapioMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.CardapioRepository;
import com.enterprise.raizesnordeste.repository.ItemCardapioRepository;
import com.enterprise.raizesnordeste.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemCardapioService {

    private final ItemCardapioRepository itemCardapioRepository;
    private final CardapioRepository cardapioRepository;
    private final ItemRepository itemRepository;
    private final ItemCardapioMapper itemCardapioMapper;

    public Page<ItemCardapioResponseDTO> getAllByCardapio(Long idCardapio, Pageable pageable) {
        buscarCardapio(idCardapio);
        return itemCardapioRepository.findAllByCardapioId(idCardapio, pageable)
                .map(itemCardapioMapper::toItemCardapioResponseDTO);
    }

    public List<ItemCardapioResponseDTO> getDisponivelByCardapio(Long idCardapio) {
        buscarCardapio(idCardapio);
        return itemCardapioRepository.findAllByCardapioIdAndDisponivelTrue(idCardapio).stream()
                .map(itemCardapioMapper::toItemCardapioResponseDTO).toList();
    }

    public ItemCardapioResponseDTO buscarPorId(Long id) {
        return itemCardapioMapper.toItemCardapioResponseDTO(buscarItemCardapio(id));
    }

    @Transactional
    public ItemCardapioResponseDTO adicionar(Long idCardapio, ItemCardapioRequestDTO request) {
        var cardapio = buscarCardapio(idCardapio);
        var item = itemRepository.findById(request.idItem())
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + request.idItem()));

        if (itemCardapioRepository.existsByCardapioIdAndItemId(idCardapio, request.idItem())) {
            throw new BusinessException("Item já existe no cardápio");
        }

        var itemCardapio = itemCardapioMapper.toItemCardapio(request, item, cardapio);
        itemCardapioRepository.save(itemCardapio);
        return itemCardapioMapper.toItemCardapioResponseDTO(itemCardapio);
    }

    @Transactional
    public ItemCardapioResponseDTO atualizar(Long id, ItemCardapioRequestDTO request) {
        ItemCardapio itemCardapio = buscarItemCardapio(id);
        var item = itemRepository.findById(request.idItem())
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + request.idItem()));

        itemCardapio.setItem(item);
        itemCardapio.setDisponivel(request.disponivel());

        itemCardapioRepository.save(itemCardapio);
        return itemCardapioMapper.toItemCardapioResponseDTO(itemCardapio);
    }

    @Transactional
    public void alterarDisponibilidade(Long id) {
        var itemCardapio = buscarItemCardapio(id);
        itemCardapio.setDisponivel(!itemCardapio.getDisponivel());
        itemCardapioRepository.save(itemCardapio);
    }

    @Transactional
    public void alterarStatus(Long id) {
        var itemCardapio = buscarItemCardapio(id);
        itemCardapio.setAtivo(!itemCardapio.getAtivo());
        itemCardapioRepository.save(itemCardapio);
    }

    private ItemCardapio buscarItemCardapio(Long id) {
        return itemCardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item do cardápio não encontrado: " + id));
    }

    private Cardapio buscarCardapio(Long id) {
        return cardapioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado: " + id));
    }
}
