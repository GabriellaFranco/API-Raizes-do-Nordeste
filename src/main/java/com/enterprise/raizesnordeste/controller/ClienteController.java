package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.UpdateClienteDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ClienteResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PedidoResponseDTO;
import com.enterprise.raizesnordeste.service.ClienteService;
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

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints de gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Lista vazia"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(Pageable pageable) {
        var clientes = clienteService.getAll(pageable);
        return clientes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar cliente por id", description = "Retorna um cliente pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.getClienteById(id));
    }

    @GetMapping("/{id}/pedidos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'MATRIZ')")
    @Operation(summary = "Histórico de pedidos", description = "Retorna o histórico de pedidos do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<PedidoResponseDTO>> buscarPedidos(@PathVariable Long id, Pageable pageable) {
        var pedidos = pedidoService.getPedidosByCliente(id, pageable);
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE')")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados pessoais do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @RequestBody @Valid UpdateClienteDTO request) {
        return ResponseEntity.ok(clienteService.updateCliente(id, request));
    }

    @DeleteMapping("/{id}/consentimento")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Revogar consentimento", description = "Revoga o consentimento LGPD e anonimiza os dados do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consentimento revogado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Consentimento já revogado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> revogarConsentimentoDados(@PathVariable Long id) {
        clienteService.revogarConsentimento(id);
        return ResponseEntity.noContent().build();
    }

}
