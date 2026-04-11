package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(min = 3, max = 100) String login,
        @NotBlank @Size(min = 6, max = 120) String senha
) {
}
