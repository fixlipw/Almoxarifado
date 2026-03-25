package com.ufc.almoxarifado.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String codigoPedido,
        String feedback,
        UUID solicitanteId,
        UUID aprovadorId,
        UUID finalizadorId,
        LocalDateTime dataPedido,
        LocalDateTime dataFinalizado,
        Boolean aprovado,
        Boolean finalizado,
        Boolean emprestimoEspecial,
        String hash
) {
}

