package com.ufc.almoxarifado.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.ufc.almoxarifado.exception.ReportGenerationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GenericReportGenerator implements ReportGenerator {

    private static final DateTimeFormatter REPORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Configuration freemarkerConfiguration;

    @Override
    public boolean supports(String key) {
        return !"estoque".equalsIgnoreCase(key);
    }

    @Override
    public byte[] generate(String format, Map<String, String> params) {
        if (!"pdf".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Formato não suportado: " + format);
        }

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("nome", params != null && params.containsKey("nome") ? params.get("nome") : "Usuário");
            model.put("tipoRelatorio", params != null && params.containsKey("tipo") ? params.get("tipo") : "Geral");
            model.put("periodo", params != null && params.containsKey("periodo") ? params.get("periodo") : "Todos");
            model.put("dataGeracao", REPORT_DATE_FORMAT.format(LocalDateTime.now()));
            model.put("linkSistema", params != null && params.containsKey("linkSistema") ? params.get("linkSistema") : "#");

            Template template = freemarkerConfiguration.getTemplate("relatorio.ftlh");
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
        } catch (Exception e) {
            throw new ReportGenerationException("Erro ao gerar relatório: " + e.getMessage(), e);
        }
    }
}
