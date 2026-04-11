package com.ufc.almoxarifado.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SigaaCadeiraResponse(
        String codigo,
        String componente,
        @JsonProperty("carga_horaria") Integer cargaHoraria,
        String local,
        String dias,
        String horario
) {
}

