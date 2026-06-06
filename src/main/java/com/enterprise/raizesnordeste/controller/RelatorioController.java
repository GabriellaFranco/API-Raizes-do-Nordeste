package com.enterprise.raizesnordeste.controller;

import com.enterprise.raizesnordeste.service.ExportarRelatorioService;
import com.enterprise.raizesnordeste.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints de exportação de relatórios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ExportarRelatorioService relatorioExportService;

    @GetMapping("/vendas/unidade/{idUnidade}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Relatório de vendas por unidade", description = "Retorna as vendas de uma unidade por período")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Map<String, Object>> vendasPorUnidade(@PathVariable Long idUnidade,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        return ResponseEntity.ok(relatorioService.vendasPorUnidade(idUnidade, inicio, fim));
    }

    @GetMapping("/vendas/unidade/{idUnidade}/export")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Exportar relatório de vendas", description = "Exporta o relatório de vendas em PDF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<byte[]> exportarVendasPorUnidade(
            @PathVariable Long idUnidade,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        byte[] pdf = relatorioExportService.exportarVendasPorUnidade(idUnidade, inicio, fim);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=vendas_unidade_" + idUnidade + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/consolidado")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Relatório consolidado", description = "Retorna dados consolidados de todas as unidades por região")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<Map<String, Object>> consolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(relatorioService.consolidado(inicio, fim));
    }

    @GetMapping("/consolidado/export")
    @PreAuthorize("hasRole('MATRIZ')")
    @Operation(summary = "Exportar relatório consolidado", description = "Exporta o relatório consolidado em PDF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<byte[]> exportarConsolidado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        byte[] pdf = relatorioExportService.exportarConsolidado(inicio, fim);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=consolidado.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/produtos-mais-vendidos/unidade/{idUnidade}")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Produtos mais vendidos", description = "Retorna o ranking dos produtos mais vendidos por unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<List<Map<String, Object>>> produtosMaisVendidos(@PathVariable Long idUnidade, @RequestParam(defaultValue = "10") int limite) {
        var produtos = relatorioService.produtosMaisVendidos(idUnidade, limite);
        return produtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(produtos);
    }

    @GetMapping("/produtos-mais-vendidos/unidade/{idUnidade}/export")
    @PreAuthorize("hasAnyRole('GERENTE', 'MATRIZ')")
    @Operation(summary = "Exportar produtos mais vendidos", description = "Exporta o ranking de produtos mais vendidos em PDF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade não encontrada"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para chamar o endpoint")
    })
    public ResponseEntity<byte[]> exportarProdutosMaisVendidos(
            @PathVariable Long idUnidade,
            @RequestParam(defaultValue = "10") int limite) {
        byte[] pdf = relatorioExportService.exportarProdutosMaisVendidos(idUnidade, limite);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=produtos_mais_vendidos_" + idUnidade + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}