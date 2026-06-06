package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.domain.dto.response.FidelidadeResponseDTO;
import com.enterprise.raizesnordeste.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Endpoints de gerenciamento do programa de fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'MATRIZ')")
    @Operation(summary = "Buscar fidelidade do cliente", description = "Retorna os pontos e saldo do cliente no programa de fidelidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fidelidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<FidelidadeResponseDTO> buscarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(fidelidadeService.getPorCliente(idCliente));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Listar fidelidade", description = "Retorna todos os registros de fidelidade paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Page<FidelidadeResponseDTO>> listarFidelidades(Pageable pageable) {
        var fidelidade = fidelidadeService.getAll(pageable);
        return fidelidade.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(fidelidade);
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Ranking de fidelidade", description = "Retorna o top 10 clientes com mais pontos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<List<FidelidadeResponseDTO>> buscarRanking() {
        var ranking = fidelidadeService.buscarRanking();
        return ranking.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ranking);
    }
}