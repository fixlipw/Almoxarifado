package com.ufc.almoxarifado.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SigaaRequest(
        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 100, message = "Login deve ter entre 3 e 100 caracteres") String login,
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres") String senha
) {
}
