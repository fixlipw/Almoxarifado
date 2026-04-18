package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoBloqueiosRequest(
        @NotNull UUID usuarioId,
        String motivoDesbloqueio,
        @NotBlank String motivoBloqueio,
        LocalDateTime dataBloqueio,
        LocalDateTime dataDesbloqueio
) {
}
