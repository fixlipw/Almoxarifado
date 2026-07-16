package com.ufc.almoxarifado.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.ufc.almoxarifado.exception.ReportGenerationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EstoqueReportGenerator implements ReportGenerator {

    private static final DateTimeFormatter REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String BRASAO_PATH = "static/images/brasao.png";

    private final EstoqueService estoqueService;
    private final Configuration freemarkerConfiguration;

    @Override
    public boolean supports(String key) {
        return "estoque".equalsIgnoreCase(key);
    }

    @Override
    public byte[] generate(String format, Map<String, String> params) {
        if (!"pdf".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Formato não suportado: " + format);
        }

        List<Map<String, Object>> itens = estoqueService.findAll().stream()
                .map(item -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("nome", item.nome());
                    row.put("quantidade", item.quantidade());
                    row.put("quantidadeDisponivel", item.quantidadeDisponivel());
                    row.put("tipo", item.tipo() != null ? item.tipo().name() : "");
                    row.put("status", Boolean.TRUE.equals(item.isAtivado()) ? "Ativo" : "Inativo");
                    return row;
                })
                .toList();

        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Relatório de Estoque");
        model.put("geradoEm", REPORT_DATE_FORMAT.format(LocalDateTime.now()));
        model.put("totalItens", itens.size());
        model.put("itens", itens);
        model.put("brasaoUfc", loadBrasaoDataUri());

        try {
            Template template = freemarkerConfiguration.getTemplate("relatorios/estoque.ftlh");
            String html;
            try (StringWriter writer = new StringWriter()) {
                template.process(model, writer);
                html = writer.toString();
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html, null);
                builder.toStream(outputStream);
                builder.run();
                return outputStream.toByteArray();
            }
        } catch (IOException | TemplateException e) {
            throw new ReportGenerationException("Erro ao gerar relatório de estoque", e);
        }
    }

    private String loadBrasaoDataUri() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(BRASAO_PATH);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (inputStream == null) {
                throw new IllegalStateException("Imagem do brasão não encontrada em " + BRASAO_PATH);
            }

            inputStream.transferTo(outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            throw new ReportGenerationException("Erro ao carregar imagem do brasão", e);
        }
    }
}
