package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.PedidoRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemPedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PedidoResponseDTO;
import com.enterprise.raizesnordeste.domain.enuns.StatusPedido;
import com.enterprise.raizesnordeste.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints de gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar pedidos", description = "Retorna todos os pedidos paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<PedidoResponseDTO>> listarPedidos(Pageable pageable) {
        var pedidos = pedidoService.getAll(pageable);
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar pedido por id", description = "Retorna um pedido pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getPedidoById(id));
    }

    @GetMapping("/{id}/itens")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar itens do pedido", description = "Retorna os itens de um pedido específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<ItemPedidoResponseDTO>> buscarItens(
            @PathVariable Long id, Pageable pageable) {
        var itens = pedidoService.buscarItensPorPedido(id, pageable);
        return itens.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(itens);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE')")
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido após confirmação do pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou pagamento recusado"),
            @ApiResponse(responseCode = "404", description = "Cliente, unidade ou item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody @Valid PedidoRequestDTO request) {
        var pedido = pedidoService.createPedido(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pedido.id()).toUri();
        return ResponseEntity.created(uri).body(pedido);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE')")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido cancelado ou finalizado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.atualizarStatusPedido(id, status));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'GERENTE')")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido e devolve os itens ao estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pedido já cancelado ou finalizado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }
}