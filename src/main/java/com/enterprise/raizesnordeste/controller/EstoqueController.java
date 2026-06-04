package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.EstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.MovimentacaoEstoqueRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.EstoqueResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.MovimentacaoEstoqueResponseDTO;
import com.enterprise.raizesnordeste.service.EstoqueService;
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

import java.util.List;

@RestController
@RequestMapping("/unidades/{idUnidade}/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Endpoints de gerenciamento de estoque por unidade")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar estoque", description = "Retorna o estoque completo de uma unidade paginado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<EstoqueResponseDTO>> listar(
            @PathVariable Long idUnidade, Pageable pageable) {
        var estoque = estoqueService.getAllByUnidade(idUnidade, pageable);
        return estoque.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(estoque);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar item do estoque por id", description = "Retorna um item do estoque pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<EstoqueResponseDTO> buscarPorId(@PathVariable Long idUnidade, @PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.getEstoqueById(id));
    }

    @GetMapping("/critico")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar itens com estoque crítico", description = "Retorna itens abaixo da quantidade mínima")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<List<EstoqueResponseDTO>> listarEstoqueCritico(@PathVariable Long idUnidade) {
        var estoque = estoqueService.getAllAbaixoDoMinimo(idUnidade);
        return estoque.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(estoque);
    }

    @GetMapping("/{idItem}/disponibilidade")
    @Operation(summary = "Verificar disponibilidade", description = "Verifica se um item está disponível no estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade verificada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item ou unidade não encontrado")
    })
    public ResponseEntity<Boolean> verificarDisponibilidade(@PathVariable Long idUnidade, @PathVariable Long idItem) {
        return ResponseEntity.ok(estoqueService.verificarDisponibilidade(idUnidade, idItem));
    }

    @PostMapping("/entrada")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Registrar entrada de estoque", description = "Registra uma entrada de itens no estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrada registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> registrarEntrada(@PathVariable Long idUnidade, @RequestBody @Valid MovimentacaoEstoqueRequestDTO request) {
        return ResponseEntity.status(201).body(estoqueService.registrarEntrada(request));
    }

    @PatchMapping("/{id}/quantidade-minima")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Atualizar quantidade mínima", description = "Atualiza a quantidade mínima de alerta do estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantidade mínima atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<EstoqueResponseDTO> atualizarQuantidadeMinima(@PathVariable Long idUnidade, @PathVariable Long id,
            @RequestBody @Valid EstoqueRequestDTO request) {
        return ResponseEntity.ok(estoqueService.atualizaQuantidadeMinima(id, request));
    }
}
