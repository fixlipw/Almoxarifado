package com.ufc.almoxarifado.dto;

import java.util.UUID;

public record ItemPedidoResponse(
        UUID id,
        UUID pedidoId,
        UUID estoqueId,
        Integer quantidadeItem
) {
}

