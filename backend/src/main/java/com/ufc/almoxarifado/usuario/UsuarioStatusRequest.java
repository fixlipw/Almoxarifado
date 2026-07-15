package com.ufc.almoxarifado.usuario;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusRequest(
        @NotNull(message = "Status é obrigatório") Boolean ativo
) {
}