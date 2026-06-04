package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.UnidadeRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UnidadeResponseDTO;
import com.enterprise.raizesnordeste.service.UnidadeService;
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
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Endpoints de gerenciamento de unidades da rede")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar unidades", description = "Retorna todas as unidades paginadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<UnidadeResponseDTO>> listarUnidades(Pageable pageable) {
        var unidades = unidadeService.getAll(pageable);
        return unidades.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(unidades);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar unidade por id", description = "Retorna uma unidade pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UnidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.getUnidadeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Criar unidade", description = "Cadastra uma nova unidade na rede")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UnidadeResponseDTO> criarUnidade(@RequestBody @Valid UnidadeRequestDTO request) {
        var unidade = unidadeService.createUnidade(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(unidade.id()).toUri();
        return ResponseEntity.created(uri).body(unidade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Atualizar unidade", description = "Atualiza os dados de uma unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "409", description = "CNPJ já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UnidadeResponseDTO> atualizarUnidade(@PathVariable Long id, @RequestBody @Valid UnidadeRequestDTO request) {
        return ResponseEntity.ok(unidadeService.updateUnidade(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Desativar unidade", description = "Desativa uma unidade da rede")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unidade desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "400", description = "Unidade já inativa"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        unidadeService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
