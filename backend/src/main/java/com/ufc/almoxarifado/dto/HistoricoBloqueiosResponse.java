package com.ufc.almoxarifado.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoBloqueiosResponse(
        UUID id,
        UUID usuarioId,
        UUID administradorBloqueioId,
        UUID administradorDesbloqueioId,
        String motivoDesbloqueio,
        String motivoBloqueio,
        LocalDateTime dataBloqueio,
        LocalDateTime dataDesbloqueio
) {
}

