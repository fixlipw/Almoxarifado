package com.ufc.almoxarifado.pedido;

import jakarta.validation.constraints.NotNull;

public record EmprestimoEspecialRequest(
        @NotNull(message = "A indicação de empréstimo especial é obrigatória") Boolean emprestimoEspecial
) {
}
