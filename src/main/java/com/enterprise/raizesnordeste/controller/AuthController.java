package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.request.LoginRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.RefreshTokenDTO;
import com.enterprise.raizesnordeste.domain.dto.request.RegistroRequestDTO;
import com.enterprise.raizesnordeste.domain.dto.request.UpdateSenhaDTO;
import com.enterprise.raizesnordeste.domain.dto.response.AuthResponseDTO;
import com.enterprise.raizesnordeste.domain.dto.response.RegistroResponseDTO;
import com.enterprise.raizesnordeste.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e gerenciamento de acesso")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar novo cliente", description = "Cria um novo usuário cliente com aceite de consentimento LGPD")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    public ResponseEntity<RegistroResponseDTO> registro(@RequestBody @Valid RegistroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registro(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica o usuário e retorna o JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Email ou senha inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário inativo")
    })
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request, HttpServletRequest httpRequest) {
        var ip = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(authService.login(request, ip));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Gera um novo token JWT a partir do refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody @Valid RefreshTokenDTO request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PatchMapping("/alterar-senha")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid UpdateSenhaDTO request) {
        authService.alterarSenha(request.senhaAtual(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }
}