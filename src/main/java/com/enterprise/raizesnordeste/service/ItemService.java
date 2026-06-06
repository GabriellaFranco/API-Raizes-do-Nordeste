package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.ItemRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.mapper.ItemMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemResponseDTO getItemById(Long id) {
        return itemMapper.toItemResponseDTO(buscarItem(id));
    }

    @Transactional
    public ItemResponseDTO createItem(ItemRequestDTO request) {
        if (itemRepository.existsByNomeAndCategoria(request.nome(), request.categoria())) {
            throw new BusinessException("Item já cadastrado com esse nome e categoria");
        }

        var item = itemMapper.toItem(request);
        itemRepository.save(item);
        return itemMapper.toItemResponseDTO(item);
    }

    @Transactional
    public ItemResponseDTO updateItem(Long id, ItemRequestDTO request) {
        var item = buscarItem(id);

        item.setNome(request.nome());
        item.setDescricao(request.descricao());
        item.setPreco(request.preco());
        item.setCategoria(request.categoria());

        itemRepository.save(item);
        return itemMapper.toItemResponseDTO(item);
    }

    @Transactional
    public void alterarStatus(Long id) {
        var item = buscarItem(id);
        item.setStatus(!item.getStatus());
        itemRepository.save(item);
    }

    private Item buscarItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado: " + id));
    }
}
