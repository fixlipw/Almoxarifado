package com.ufc.almoxarifado.service;

import java.util.Map;

public interface ReportGenerator {
    boolean supports(String key);

    byte[] generate(String format, Map<String, String> params);
}
