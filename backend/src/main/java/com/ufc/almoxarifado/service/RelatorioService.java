package com.ufc.almoxarifado.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final List<ReportGenerator> reportGenerators;

    public byte[] generateReport(String key, String format, Map<String, String> params) {
        return reportGenerators.stream()
                .filter(generator -> generator.supports(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de relatório não suportado: " + key))
                .generate(format, params);
    }
}
