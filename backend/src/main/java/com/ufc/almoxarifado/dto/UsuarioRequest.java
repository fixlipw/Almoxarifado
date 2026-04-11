package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.RoleAcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        String matricula,
        @NotBlank String senha,
        String nome,
        String sobrenome,
        String curso,
        String fotoPerfil,
        @NotNull RoleAcesso acesso,
        Boolean isAtivada,
        Boolean isBloqueado
) {
}

