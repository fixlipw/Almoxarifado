package com.ufc.almoxarifado.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Login é obrigatório")
        @Size(min = 3, max = 100, message = "Login deve ter entre 3 e 100 caracteres") String login,
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido") String email,
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres") String senha,
        @NotBlank(message = "Matrícula é obrigatória")
        @Size(max = 30, message = "Matrícula deve ter no máximo 30 caracteres") String matricula,
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres") String nome,
        @Size(max = 150, message = "Sobrenome deve ter no máximo 150 caracteres") String sobrenome,
        @Size(max = 100, message = "Curso deve ter no máximo 100 caracteres") String curso,
        @Size(max = 255, message = "Foto de perfil deve ter no máximo 255 caracteres") String fotoPerfil
) {
}

