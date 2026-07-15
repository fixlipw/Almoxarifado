package com.ufc.almoxarifado.historico;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoBloqueiosResponse(
        UUID id,
        Long usuarioId,
        Long administradorBloqueioId,
        Long administradorDesbloqueioId,
        String motivoDesbloqueio,
        String motivoBloqueio,
        LocalDateTime dataBloqueio,
        LocalDateTime dataDesbloqueio
) {
}

