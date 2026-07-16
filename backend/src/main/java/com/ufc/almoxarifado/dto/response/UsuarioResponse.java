package com.ufc.almoxarifado.dto.response;

import com.ufc.almoxarifado.model.Curso;
import com.ufc.almoxarifado.model.Papel;

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
