package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.ItemCardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.ItemCardapioResponseDTO;
import com.enterprise.raizesnordeste.service.ItemCardapioService;
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

import java.util.List;

@RestController
@RequestMapping("/cardapios/{idCardapio}/itens")
@RequiredArgsConstructor
@Tag(name = "Itens do Cardápio", description = "Endpoints de gerenciamento de itens do cardápio")
public class ItemCardapioController {

    private final ItemCardapioService itemCardapioService;

    @GetMapping
    @Operation(summary = "Listar itens do cardápio", description = "Retorna todos os itens de um cardápio paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Lista vazia"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado")
    })
    public ResponseEntity<Page<ItemCardapioResponseDTO>> listarItensDoCardapio(@PathVariable Long idCardapio, Pageable pageable) {
        var itens = itemCardapioService.getAllByCardapio(idCardapio, pageable);
        return itens.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(itens);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar itens disponíveis", description = "Retorna apenas os itens disponíveis no cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado")
    })
    public ResponseEntity<List<ItemCardapioResponseDTO>> listarDisponiveis(@PathVariable Long idCardapio) {
        var itens = itemCardapioService.getDisponivelByCardapio(idCardapio);
        return itens.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por id", description = "Retorna um item do cardápio pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemCardapioResponseDTO> buscarPorId(@PathVariable Long idCardapio, @PathVariable Long id) {
        return ResponseEntity.ok(itemCardapioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Adicionar item ao cardápio", description = "Adiciona um item ao cardápio da unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Item já existe no cardápio"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<ItemCardapioResponseDTO> adicionarItemAoCardapio(@PathVariable Long idCardapio, @RequestBody @Valid ItemCardapioRequestDTO request) {
        var item = itemCardapioService.adicionar(idCardapio, request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(item.id()).toUri();
        return ResponseEntity.created(uri).body(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Atualizar item do cardápio", description = "Atualiza os dados de um item no cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<ItemCardapioResponseDTO> atualizarItemDoCardapio(@PathVariable Long idCardapio, @PathVariable Long id, @RequestBody @Valid ItemCardapioRequestDTO request) {
        return ResponseEntity.ok(itemCardapioService.atualizar(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Alterar status do item", description = "Ativa ou desativa um item do cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> alterarStatus(@PathVariable Long idCardapio, @PathVariable Long id) {
        itemCardapioService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Alterar disponibilidade", description = "Altera a disponibilidade sazonal de um item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilidade alterada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> alterarDisponibilidadeSazonal(@PathVariable Long idCardapio, @PathVariable Long id) {
        itemCardapioService.alterarDisponibilidade(id);
        return ResponseEntity.noContent().build();
    }
}
