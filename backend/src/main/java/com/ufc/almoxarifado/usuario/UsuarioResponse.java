package com.ufc.almoxarifado.usuario;

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

