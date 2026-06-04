package com.enterprise.raizesnordeste.service;

import com.enterprise.raizesnordeste.domain.dto.request.PedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemPedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.entity.Cliente;
import com.enterprise.raizesnordeste.domain.entity.ItemPedido;
import com.enterprise.raizesnordeste.domain.entity.Pedido;
import com.enterprise.raizesnordeste.domain.entity.Unidade;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import com.enterprise.raizesnordeste.domain.mapper.ItemPedidoMapper;
import com.enterprise.raizesnordeste.domain.mapper.PedidoMapper;
import com.enterprise.raizesnordeste.exception.BusinessException;
import com.enterprise.raizesnordeste.exception.ResourceNotFoundException;
import com.enterprise.raizesnordeste.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UnidadeRepository unidadeRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PagamentoMockService pagamentoMockService;
    private final EstoqueService estoqueService;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;

    public Page<PedidoResponseDTO> getAll(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(pedidoMapper::toPedidoResponseDTO);
    }

    public PedidoResponseDTO getPedidoById(Long id) {
        return pedidoMapper.toPedidoResponseDTO(buscarPedido(id));
    }

    public Page<ItemPedidoResponseDTO> buscarItensPorPedido(Long idPedido, Pageable pageable) {
        buscarPedido(idPedido);
        return itemPedidoRepository.findAllByPedidoId(idPedido, pageable)
                .map(itemPedidoMapper::toItemPedidoResponseDTO);
    }

    @Transactional
    public PedidoResponseDTO createPedido(PedidoRequestDTO request) {
        var unidade = buscarUnidade(request.idUnidade());

        Cliente cliente = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = authentication.getName();

        var isCliente = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));

        if (isCliente) {
            cliente = clienteRepository.findByUsuarioEmail(email).orElse(null);
        } else if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + request.idCliente()));
        }

        var pedido = pedidoMapper.toPedido(request, cliente, unidade);

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<ItemPedido> itensPedido = request.itens().stream().map(itemRequest -> {
            var itemCardapio = itemCardapioRepository.findByCardapioIdAndItemId(unidade.getId(), itemRequest.idItem())
                    .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no cardápio: " + itemRequest.idItem()));

            if (!itemCardapio.getDisponivel()) {
                throw new BusinessException("Item indisponível: " + itemCardapio.getItem().getNome());
            }

            estoqueService.decrementarEstoquePorPedidoRealizado(unidade.getId(), itemCardapio.getItem().getId(),itemRequest.quantidade());

            return itemPedidoMapper.toItemPedido(itemRequest, pedido, itemCardapio);
        }).toList();

        for (ItemPedido item : itensPedido) {
            valorTotal = valorTotal.add(
                    item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
            );
        }

        if (request.usarPontos() && cliente != null) {
            var fidelidade = fidelidadeRepository.findByClienteId(cliente.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fidelidade não encontrada"));

            if (fidelidade.getPontosAcumulados() < 100) {
                throw new BusinessException("Pontos insuficientes para resgate. Mínimo: 100 pontos");
            }

            BigDecimal desconto = BigDecimal.valueOf(fidelidade.getPontosAcumulados());
            if (desconto.compareTo(valorTotal) > 0) {
                desconto = valorTotal;
            }

            var pontosUsados = desconto.intValue();
            valorTotal = valorTotal.subtract(desconto);

            fidelidade.setPontosAcumulados(fidelidade.getPontosAcumulados() - pontosUsados);
            pedido.setPontosUtilizados(pontosUsados);
            pedido.setDescontoPontos(desconto);
            fidelidadeRepository.save(fidelidade);
        }

        pedido.setValorTotal(valorTotal);
        pedido.setItens(itensPedido);
        pagamentoMockService.processarPagamento(request.meioPagamento(), valorTotal);
        pedidoRepository.save(pedido);

        if (cliente != null) {
            BigDecimal finalValorTotal = valorTotal;
            fidelidadeRepository.findByClienteId(cliente.getId()).ifPresent(fidelidade -> {
                fidelidade.setPontosAcumulados(fidelidade.getPontosAcumulados() + 10);
                fidelidade.setTotalGasto(fidelidade.getTotalGasto().add(finalValorTotal));
                fidelidadeRepository.save(fidelidade);
            });
        }

        return pedidoMapper.toPedidoResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        var pedido = buscarPedido(id);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Um pedido cancelado não pode ter seu status alterado");
        }
        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new BusinessException("Um pedido finalizado não pode ter seu status alterado");
        }

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
        return pedidoMapper.toPedidoResponseDTO(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        var pedido = buscarPedido(id);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já está cancelado");
        }
        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new BusinessException("Um pedido finalizado não pode ser cancelado");
        }

        pedido.getItens().forEach(item ->
                estoqueService.incrementarEstoquePorCancelamento(
                        pedido.getUnidade().getId(),
                        item.getItemCardapio().getItem().getId(),
                        item.getQuantidade()
                )
        );

        if (pedido.getPontosUtilizados() != null && pedido.getCliente() != null) {
            fidelidadeRepository.findByClienteId(pedido.getCliente().getId())
                    .ifPresent(fidelidade -> {
                        fidelidade.setPontosAcumulados(
                                fidelidade.getPontosAcumulados() + pedido.getPontosUtilizados()
                        );
                        fidelidadeRepository.save(fidelidade);
                    });
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    public Page<PedidoResponseDTO> getPedidosByCliente(Long idCliente, Pageable pageable) {
        return pedidoRepository.findAllByClienteId(idCliente, pageable).map(pedidoMapper::toPedidoResponseDTO);
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    private Unidade buscarUnidade(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada: " + id));
    }
}
