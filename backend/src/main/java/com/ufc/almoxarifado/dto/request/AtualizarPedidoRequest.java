package com.ufc.almoxarifado.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AtualizarPedidoRequest(
        @Size(max = 2000, message = "Feedback deve ter no máximo 2000 caracteres") String feedback,
        @NotEmpty(message = "O pedido deve possuir pelo menos um item")
        List<@NotNull(message = "Item do pedido não pode ser nulo") @Valid ItemPedidoRequest> itens
) {
}
