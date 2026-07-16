package com.ufc.almoxarifado.dto.request;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusRequest(
        @NotNull(message = "Status é obrigatório") Boolean ativo
) {
}
