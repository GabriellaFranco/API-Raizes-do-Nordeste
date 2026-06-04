package com.enterprise.raizesnordeste.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportarRelatorioService {

    private final RelatorioService relatorioService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] exportarVendasPorUnidade(Long idUnidade, LocalDateTime inicio, LocalDateTime fim) {
        var dados = relatorioService.vendasPorUnidade(idUnidade, inicio, fim);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            var writer = new PdfWriter(baos);
            var pdf = new PdfDocument(writer);
            var document = new Document(pdf);

            adicionarTitulo(document, "Relatório de Vendas por Unidade");
            adicionarSubtitulo(document, "Período: " + inicio.format(FORMATTER) + " até " + fim.format(FORMATTER));

            var table = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();

            adicionarCabecalhoTabela(table, "Métrica", "Valor");
            adicionarLinhaTabela(table, "Total de Pedidos", String.valueOf(dados.get("totalPedidos")));
            adicionarLinhaTabela(table, "Total de Vendas", "R$ " + dados.get("totalVendas"));

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de vendas: " + e.getMessage());
        }
    }

    public byte[] exportarConsolidado(LocalDateTime inicio, LocalDateTime fim) {
        var dados = relatorioService.consolidado(inicio, fim);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            var writer = new PdfWriter(baos);
            var pdf = new PdfDocument(writer);
            var document = new Document(pdf);

            adicionarTitulo(document, "Relatório Consolidado por Região");
            adicionarSubtitulo(document, "Período: " + inicio.format(FORMATTER) + " até " + fim.format(FORMATTER));

            var table = new Table(UnitValue.createPercentArray(new float[]{34, 33, 33}))
                    .useAllAvailableWidth();

            adicionarCabecalhoTabela(table, "Região", "Total Pedidos", "Total Vendas");

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> dadosPorRegiao =
                    (Map<String, Map<String, Object>>) dados.get("dadosPorRegiao");

            dadosPorRegiao.forEach((regiao, info) -> {
                table.addCell(new Cell().add(new Paragraph(regiao)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(info.get("totalPedidos")))));
                table.addCell(new Cell().add(new Paragraph("R$ " + info.get("totalVendas"))));
            });

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório consolidado: " + e.getMessage());
        }
    }

    public byte[] exportarProdutosMaisVendidos(Long idUnidade, int limite) {
        var dados = relatorioService.produtosMaisVendidos(idUnidade, limite);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            var writer = new PdfWriter(baos);
            var pdf = new PdfDocument(writer);
            var document = new Document(pdf);

            adicionarTitulo(document, "Produtos Mais Vendidos");
            adicionarSubtitulo(document, "Top " + limite + " produtos");

            var table = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}))
                    .useAllAvailableWidth();

            adicionarCabecalhoTabela(table, "Posição", "Produto", "Quantidade Vendida");

            for (int i = 0; i < dados.size(); i++) {
                Map<String, Object> item = dados.get(i);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.get("item")))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.get("quantidadeVendida")))));
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório de produtos: " + e.getMessage());
        }
    }

    private void adicionarTitulo(Document document, String titulo) {
        document.add(new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));
    }

    private void adicionarSubtitulo(Document document, String subtitulo) {
        document.add(new Paragraph(subtitulo)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));
    }

    private void adicionarCabecalhoTabela(Table table, String... colunas) {
        for (String coluna : colunas) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(coluna).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void adicionarLinhaTabela(Table table, String... valores) {
        for (String valor : valores) {
            table.addCell(new Cell()
                    .add(new Paragraph(valor))
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }
}