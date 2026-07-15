package com.ufc.almoxarifado.usuario;

import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario entity) {
        if (entity == null) {
            return null;
        }
        return new UsuarioResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getNome(),
                entity.getNomeSocial(),
                entity.getCpf(),
                entity.getCurso(),
                entity.getPapel(),
                entity.getFotoPerfil(),
                entity.getStatus(),
                entity.getBloqueado(),
                entity.getMatricula()
        );
    }

    public void applyRequest(Usuario entity, UsuarioRequest request) {
        if (request == null || entity == null) {
            return;
        }
        if (request.matricula() != null) {
            entity.setMatricula(request.matricula().trim());
        }
        if (request.username() != null) {
            entity.setUsername(normalizeUsername(request.username()));
        }
        if (request.email() != null) {
            entity.setEmail(normalizeEmail(request.email()));
        }
        if (request.nome() != null) {
            entity.setNome(request.nome().trim());
        }
        if (request.nomeSocial() != null) {
            entity.setNomeSocial(request.nomeSocial().trim());
        }
        if (request.cpf() != null) {
            entity.setCpf(normalizeCpf(request.cpf()));
        }
        if (request.curso() != null) {
            entity.setCurso(request.curso());
        }
        if (request.papel() != null) {
            entity.setPapel(request.papel());
        }
        if (request.fotoPerfil() != null) {
            entity.setFotoPerfil(request.fotoPerfil());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        if (request.bloqueado() != null) {
            entity.setBloqueado(request.bloqueado());
        }
    }

    public String normalizeUsername(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    public String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }

    public String normalizeCpf(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
