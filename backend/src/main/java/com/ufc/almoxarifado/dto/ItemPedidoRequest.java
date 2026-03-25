package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ItemPedidoRequest(
        @NotNull UUID pedidoId,
        @NotNull UUID estoqueId,
        @NotNull @Positive Integer quantidadeItem
) {
}

