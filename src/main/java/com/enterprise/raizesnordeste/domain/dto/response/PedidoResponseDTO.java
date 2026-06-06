package com.enterprise.raizesnordeste.domain.dto.response;

import com.enterprise.raizesnordeste.domain.enuns.CanalPedido;
import com.enterprise.raizesnordeste.domain.enuns.MeioPagamento;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PedidoResponseDTO(

        Long id,
        Long idCliente,
        String nomeCliente,
        Long idUnidade,
        String nomeFantasiaUnidade,
        CanalPedido canal,
        StatusPedido status,
        BigDecimal valorTotal,
        MeioPagamento meioPagamento,
        List<ItemPedidoResponseDTO> itens,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {
}
