package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.ItemPedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemPedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Item;
import com.enterprise.raizesnordeste.domain.entity.ItemCardapio;
import com.enterprise.raizesnordeste.domain.entity.ItemPedido;
import com.enterprise.raizesnordeste.domain.entity.Pedido;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ItemPedidoMapper {

    public ItemPedido toItemPedido(ItemPedidoRequestDTO dto, Pedido pedido, ItemCardapio itemCardapio) {
        return ItemPedido.builder()
                .pedido(pedido)
                .itemCardapio(itemCardapio)
                .quantidade(dto.quantidade())
                .precoUnitario(itemCardapio.getItem().getPreco())
                .build();
    }

    public ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido itemPedido) {
        BigDecimal subtotal = itemPedido.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(itemPedido.getQuantidade()));

        return ItemPedidoResponseDTO.builder()
                .id(itemPedido.getId())
                .idItem(itemPedido.getItemCardapio().getItem().getId())
                .nomeItem(itemPedido.getItemCardapio().getItem().getNome())
                .quantidade(itemPedido.getQuantidade())
                .precoUnitario(itemPedido.getPrecoUnitario())
                .subtotal(subtotal)
                .build();
    }
}