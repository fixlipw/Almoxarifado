package com.ufc.almoxarifado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 100) String login,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 120) String senha,
        @NotBlank String matricula,
        @NotBlank String nome,
        String sobrenome,
        String curso,
        String fotoPerfil
) {
}

