package com.enterprise.raizesnordeste.domain.mapper;

import com.enterprise.raizesnordeste.domain.dto.request.PedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemPedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.Pedido;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PedidoMapper {

    private final ItemPedidoMapper itemPedidoMapper;

    public Pedido toPedido(PedidoRequestDTO dto, Cliente cliente, Unidade unidade) {
        return Pedido.builder()
                .cliente(cliente)
                .unidade(unidade)
                .canal(dto.canal())
                .meioPagamento(dto.meioPagamento())
                .status(StatusPedido.CONFIRMADO)
                .build();
    }

    public PedidoResponseDTO toPedidoResponseDTO(Pedido pedido) {
        List<ItemPedidoResponseDTO> itens = pedido.getItens()
                .stream()
                .map(itemPedidoMapper::toItemPedidoResponseDTO)
                .toList();

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .idCliente(pedido.getCliente() != null ? pedido.getCliente().getId() : null)
                .nomeCliente(pedido.getCliente() != null ? pedido.getCliente().getUsuario().getNome() : null)
                .idUnidade(pedido.getUnidade().getId())
                .nomeFantasiaUnidade(pedido.getUnidade().getNomeFantasia())
                .canal(pedido.getCanal())
                .status(pedido.getStatus())
                .valorTotal(pedido.getValorTotal())
                .meioPagamento(pedido.getMeioPagamento())
                .itens(itens)
                .createdAt(pedido.getCreatedAt())
                .updatedAt(pedido.getUpdatedAt())
                .createdBy(pedido.getCreatedBy())
                .updatedBy(pedido.getUpdatedBy())
                .build();
    }
}
