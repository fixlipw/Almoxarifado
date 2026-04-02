package com.ufc.almoxarifado.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String codigoPedido,
        String feedback,
        UsuarioResponse solicitante,
        UsuarioResponse aprovador,
        UsuarioResponse finalizador,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataAprovacao,
        LocalDateTime dataFinalizado,
        LocalDateTime dataAtualizacao,
        Boolean aprovado,
        Boolean finalizado,
        Boolean emprestimoEspecial,
        String hash
) {
}
