package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.UsuarioRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UsuarioUpdateRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.response.UsuarioResponseDTO;
import com.enterprise.raizesnordeste.service.UsuarioService;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints de gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(Pageable pageable) {
        var usuarios = usuarioService.getAll(pageable);
        return usuarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar usuário por id", description = "Retorna um usuário pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @GetMapping("/unidade/{idUnidade}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar usuários por unidade", description = "Retorna usuários vinculados a uma unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPorUnidade(@PathVariable Long idUnidade, Pageable pageable) {
        var usuarios = usuarioService.getAllByUnidade(idUnidade, pageable);
        return usuarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(usuarios);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário interno (funcionário, gerente)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody @Valid UsuarioRequestDTO request) {
        var usuario = usuarioService.createUsuario(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuario.id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioUpdateRequestDTO request) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request));
    }

    @PostMapping("/{id}/perfis/{idPerfil}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Vincular perfil", description = "Vincula um perfil de autoridade a um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil vinculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou perfil não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> vincularPerfil(@PathVariable Long id, @PathVariable Long idPerfil) {
        usuarioService.vincularPerfilAutoridade(id, idPerfil);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/perfis/{idPerfil}")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Desvincular perfil", description = "Remove um perfil de autoridade de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil desvinculado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou perfil não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> desvincularPerfil(@PathVariable Long id, @PathVariable Long idPerfil) {
        usuarioService.desvincularPerfil(id, idPerfil);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Alterar status", description = "Ativa ou desativa um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Void> alterarStatus(@PathVariable Long id) {
        usuarioService.alterarStatus(id);
        return ResponseEntity.noContent().build();
    }
}