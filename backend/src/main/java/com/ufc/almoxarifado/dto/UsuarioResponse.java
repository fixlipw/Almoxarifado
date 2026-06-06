package com.ufc.almoxarifado.dto;

import com.ufc.almoxarifado.entity.Curso;
import com.ufc.almoxarifado.entity.Papel;

public record UsuarioResponse(
        Long id,
        String username,
        String email,
        String nome,
        String nomeSocial,
        String cpf,
        Curso curso,
        Papel papel,
        String fotoPerfil,
        Boolean status,
        Boolean bloqueado,
        String matricula
) {
}

