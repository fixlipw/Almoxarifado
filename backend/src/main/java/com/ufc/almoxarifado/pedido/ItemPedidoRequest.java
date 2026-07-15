package com.ufc.almoxarifado.pedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ItemPedidoRequest(
        @NotNull(message = "Item é obrigatório") UUID estoqueId,
        @NotNull(message = "Quantidade do item é obrigatória")
        @Positive(message = "Quantidade do item deve ser maior que zero") Integer quantidadeItem
) {
}

