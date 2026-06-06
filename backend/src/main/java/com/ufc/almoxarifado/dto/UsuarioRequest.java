package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.Curso;
import com.ufc.almoxarifado.entity.Papel;
import jakarta.validation.constraints.*;

public record UsuarioRequest(
        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 100, message = "Username deve ter entre 3 e 100 caracteres") String username,
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido") String email,
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres") String nome,
        @Size(max = 150, message = "Nome social deve ter no máximo 150 caracteres") String nomeSocial,
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "Formato do CPF inválido") String cpf,
        @NotNull(message = "Curso é obrigatório") Curso curso,
        @NotNull(message = "Papel é obrigatório") Papel papel,
        @Size(max = 255, message = "Foto de perfil deve ter no máximo 255 caracteres") String fotoPerfil,
        @NotNull(message = "Status é obrigatório") Boolean status,
        @NotNull(message = "Bloqueio é obrigatório") Boolean bloqueado,
        @Size(max = 30, message = "Matrícula deve ter no máximo 30 caracteres") String matricula
) {
}

