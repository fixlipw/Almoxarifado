package com.ufc.almoxarifado.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CriarPedidoRequest(
        @NotBlank(message = "Código do pedido é obrigatório")
        @Size(max = 50, message = "Código do pedido deve ter no máximo 50 caracteres") String codigoPedido,
        @Size(max = 2000, message = "Feedback deve ter no máximo 2000 caracteres") String feedback,
        @NotEmpty(message = "O pedido deve possuir pelo menos um item")
        List<@NotNull(message = "Item do pedido não pode ser nulo") @Valid ItemPedidoRequest> itens
) {
}
