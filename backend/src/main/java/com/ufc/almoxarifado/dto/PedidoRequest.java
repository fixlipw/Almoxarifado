package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoRequest(
        @NotBlank String codigoPedido,
        String feedback,
        @NotNull UUID solicitanteId,
        UUID aprovadorId,
        UUID finalizadorId,
        List<ItemPedidoRequest> itens,
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
