package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.CardapioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.CardapioResponseDTO;
import com.enterprise.raizesnordeste.service.CardapioService;
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
@RequestMapping("/cardapios")
@RequiredArgsConstructor
@Tag(name = "Cardápios", description = "Endpoints de gerenciamento de cardápios")
public class CardapioController {

    private final CardapioService cardapioService;

    @GetMapping
    @Operation(summary = "Listar cardápios", description = "Retorna todos os cardápios paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Lista vazia"),
    })
    public ResponseEntity<Page<CardapioResponseDTO>> listarCardapios(Pageable pageable) {
        var cardapios = cardapioService.getAll(pageable);
        return cardapios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cardapios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cardápio por id", description = "Retorna um cardápio pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardápio encontrado"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado")
    })
    public ResponseEntity<CardapioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.getCardapioById(id));
    }

    @GetMapping("/unidade/{idUnidade}")
    @Operation(summary = "Buscar cardápio por unidade", description = "Retorna o cardápio ativo da unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardápio encontrado"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado para a unidade")
    })
    public ResponseEntity<CardapioResponseDTO> buscarAtivoPorUnidade(@PathVariable Long idUnidade) {
        return ResponseEntity.ok(cardapioService.getCardapioByUnidade(idUnidade));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Criar cardápio", description = "Cadastra um novo cardápio para uma unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cardápio criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Unidade já possui cardápio ativo"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<CardapioResponseDTO> criarCardapio(@RequestBody @Valid CardapioRequestDTO request) {
        var cardapio = cardapioService.createCardapio(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cardapio.id()).toUri();
        return ResponseEntity.created(uri).body(cardapio);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Atualizar cardápio", description = "Atualiza os dados de um cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardápio atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<CardapioResponseDTO> atualizarCardapio(@PathVariable Long id, @RequestBody @Valid CardapioRequestDTO request) {
        return ResponseEntity.ok(cardapioService.updateCardapio(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Desativar cardápio", description = "Desativa um cardápio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cardápio desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cardápio não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cardápio já inativo"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        cardapioService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
