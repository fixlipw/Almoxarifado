package com.ufc.almoxarifado.estoque;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EstoqueRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotNull(message = "Quantidade é obrigatória")
        @PositiveOrZero(message = "Quantidade não pode ser negativa") Integer quantidade,
        @NotNull(message = "Quantidade disponível é obrigatória")
        @PositiveOrZero(message = "Quantidade disponível não pode ser negativa") Integer quantidadeDisponivel,
        @NotNull(message = "Tipo é obrigatório") TipoEstoque tipo,
        Boolean isAtivado
) {
}
