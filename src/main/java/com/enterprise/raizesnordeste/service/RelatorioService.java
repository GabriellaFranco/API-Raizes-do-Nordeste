package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.entity.ItemPedido;
import com.enterprise.raizesnordeste.domain.entity.Pedido;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.ItemPedidoRepository;
import com.enterprise.raizesnordeste.repository.PedidoRepository;
import com.enterprise.raizesnordeste.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RelatorioService {


    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UnidadeRepository unidadeRepository;

    public Map<String, Object> vendasPorUnidade(Long idUnidade, LocalDateTime inicio, LocalDateTime fim) {
        buscarUnidade(idUnidade);

        var pedidos = pedidoRepository.findAllByUnidadeIdAndStatusNotAndCreatedAtBetween(
                idUnidade, StatusPedido.CANCELADO, inicio, fim);

        BigDecimal totalVendas = pedidos.stream()
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "idUnidade", idUnidade,
                "totalPedidos", pedidos.size(),
                "totalVendas", totalVendas,
                "periodo", Map.of("inicio", inicio, "fim", fim)
        );
    }

    public Map<String, Object> consolidado(LocalDateTime inicio, LocalDateTime fim) {
        var pedidos = pedidoRepository.findAllByStatusNotAndCreatedAtBetween(
                StatusPedido.CANCELADO, inicio, fim);

        var dadosPorRegiao = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getUnidade().getRegiao().name(),
                        Collectors.collectingAndThen(Collectors.toList(), lista -> Map.of(
                                "totalPedidos", lista.size(),
                                "totalVendas", lista.stream()
                                        .map(Pedido::getValorTotal)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        ))
                ));

        return Map.of(
                "periodo", Map.of("inicio", inicio, "fim", fim),
                "dadosPorRegiao", dadosPorRegiao
        );
    }

    public List<Map<String, Object>> produtosMaisVendidos(Long idUnidade, int limite) {
        buscarUnidade(idUnidade);

        return itemPedidoRepository.findAllByPedidoUnidadeId(idUnidade)
                .stream()
                .collect(Collectors.groupingBy(
                        ip -> ip.getItemCardapio().getItem().getNome(),
                        Collectors.summingInt(ItemPedido::getQuantidade)
                ))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limite)
                .map(e -> Map.of(
                        "item", (Object) e.getKey(),
                        "quantidadeVendida", (Object) e.getValue()
                ))
                .toList();
    }

    private void buscarUnidade(Long id) {
        unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + id));
    }
}