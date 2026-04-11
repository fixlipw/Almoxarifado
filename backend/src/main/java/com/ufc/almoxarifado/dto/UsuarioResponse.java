package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.RoleAcesso;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String usuario,
        String matricula,
        String nome,
        String sobrenome,
        String curso,
        String fotoPerfil,
        RoleAcesso acesso,
        Boolean isAtivada,
        Boolean isBloqueado
) {
}

