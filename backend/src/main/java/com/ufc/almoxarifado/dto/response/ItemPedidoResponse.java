package com.ufc.almoxarifado.dto.response;

import java.util.UUID;

public record ItemPedidoResponse(
        UUID id,
        UUID pedidoId,
        UUID estoqueId,
        String nomeItem,
        Integer quantidadeItem
) {
}
