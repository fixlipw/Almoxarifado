package com.ufc.almoxarifado.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoRequest(
        @NotBlank(message = "Código do pedido é obrigatório")
        @Size(min = 1, max = 50, message = "Código do pedido deve ter no máximo 50 caracteres") String codigoPedido,
        @Size(max = 2000, message = "Feedback deve ter no máximo 2000 caracteres") String feedback,
        List<@NotNull(message = "Item do pedido não pode ser nulo") @Valid ItemPedidoRequest> itens,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataAprovacao,
        LocalDateTime dataFinalizado,
        LocalDateTime dataAtualizacao,
        Boolean aprovado,
        Boolean finalizado,
        Boolean emprestimoEspecial,
        @Size(max = 255, message = "Hash deve ter no máximo 255 caracteres") String hash
) {
}
