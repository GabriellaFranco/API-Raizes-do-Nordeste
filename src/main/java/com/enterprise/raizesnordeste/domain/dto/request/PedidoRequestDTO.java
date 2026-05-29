package com.enterprise.raizesnordeste.domain.dto.request;

import com.enterprise.raizesnordeste.domain.enuns.CanalPedido;
import com.enterprise.raizesnordeste.domain.enuns.MeioPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record PedidoRequestDTO(

        Long idCliente,

        @NotNull(message = "A unidade é obrigatória")
        Long idUnidade,

        @NotNull(message = "O canal é obrigatório")
        CanalPedido canal,

        @NotNull(message = "Meio de pagamento é obrigatório")
        MeioPagamento meioPagamento,

        @NotNull(message = "Itens são obrigatórios")
        @Size(min = 1, message = "Pedido deve ter pelo menos um item")
        List<ItemPedidoRequestDTO> itens,

        @NotNull(message = "Informe se deseja usar pontos")
        Boolean usarPontos
) {
}

