package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.service.RelatorioService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<ByteArrayResource> getReport(
            @PathVariable @NotBlank String key,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) String nome
    ) {
        Map<String, String> params = new HashMap<>();
        if (periodo != null) params.put("periodo", periodo);
        if (nome != null) params.put("nome", nome);

        byte[] reportContent = relatorioService.generateReport(key, format, params);
        ByteArrayResource resource = new ByteArrayResource(reportContent);

        boolean isExcel = "excel".equalsIgnoreCase(format);
        MediaType mediaType = isExcel 
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_PDF;
        
        String extension = isExcel ? ".xlsx" : ".pdf";
        String filename = "relatorio-" + key + extension;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(resource);
    }
}

