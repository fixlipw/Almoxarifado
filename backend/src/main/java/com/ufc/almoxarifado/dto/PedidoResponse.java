package com.ufc.almoxarifado.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String codigoPedido,
        String feedback,
        UsuarioResponse solicitante,
        UsuarioResponse aprovador,
        UsuarioResponse finalizador,
        List<ItemPedidoResponse> itens,
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
