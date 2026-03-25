package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.TipoEstoque;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EstoqueRequest(
        @NotBlank String nome,
        @NotNull @PositiveOrZero Integer quantidade,
        @NotNull TipoEstoque tipo,
        Boolean isAtivado
) {
}
