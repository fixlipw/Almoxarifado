package com.ufc.almoxarifado.relatorio.internal;

import java.util.Map;

public interface ReportGenerator {
    boolean supports(String key);

    byte[] generate(String format, Map<String, String> params);
}
