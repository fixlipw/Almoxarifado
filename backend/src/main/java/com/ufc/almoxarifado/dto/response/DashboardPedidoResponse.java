package com.ufc.almoxarifado.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardPedidoResponse(
        UUID id,
        String codigoPedido,
        String solicitante,
        int tiposItens,
        LocalDateTime data,
        String status
) {
}
