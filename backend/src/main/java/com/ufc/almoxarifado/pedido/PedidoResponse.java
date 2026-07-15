package com.ufc.almoxarifado.pedido;

import com.ufc.almoxarifado.usuario.UsuarioResponse;

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
        StatusPedido status,
        Boolean emprestimoEspecial,
        String hash
) {
}
