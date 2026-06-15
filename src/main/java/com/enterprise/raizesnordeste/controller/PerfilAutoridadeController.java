package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.PerfilAutoridadeRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.PerfilAutoridadeResponseDTO;
import com.enterprise.raizesnordeste.service.PerfilAutoridadeService;
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
@RequestMapping("/perfis")
@RequiredArgsConstructor
@Tag(name = "Perfis de Autoridade", description = "Endpoints de gerenciamento de perfis de autoridade")
public class PerfilAutoridadeController {

    private final PerfilAutoridadeService perfilAutoridadeService;

    @GetMapping
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Listar perfis", description = "Retorna todos os perfis de autoridade paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Lista vazia"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<PerfilAutoridadeResponseDTO>> listar(Pageable pageable) {
        var perfis = perfilAutoridadeService.getAll(pageable);
        return perfis.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(perfis);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Buscar perfil por id", description = "Retorna um perfil pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PerfilAutoridadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(perfilAutoridadeService.getPerfilById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Criar perfil", description = "Cadastra um novo perfil de autoridade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Perfil já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PerfilAutoridadeResponseDTO> criarPerfil(@RequestBody @Valid PerfilAutoridadeRequestDTO request) {
        var perfil = perfilAutoridadeService.createPerfil(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(perfil.id()).toUri();
        return ResponseEntity.created(uri).body(perfil);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Atualizar perfil", description = "Atualiza os dados de um perfil de autoridade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "409", description = "Nome já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<PerfilAutoridadeResponseDTO> atualizarPerfil(@PathVariable Long id, @RequestBody @Valid PerfilAutoridadeRequestDTO request) {
        return ResponseEntity.ok(perfilAutoridadeService.updatePerfil(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Alterar status", description = "Ativa ou desativa um perfil de autoridade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id) {
        perfilAutoridadeService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}
