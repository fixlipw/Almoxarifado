package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioStatusRequest(
        @NotNull(message = "Status é obrigatório") Boolean ativo
) {
}