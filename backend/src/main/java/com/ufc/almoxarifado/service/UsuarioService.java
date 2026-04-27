package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.UsuarioRequest;
import com.ufc.almoxarifado.dto.UsuarioResponse;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse create(UsuarioRequest request) {
        Usuario entity = new Usuario();
        applyRequest(entity, request);
        return toResponse(usuarioRepository.save(entity));
    }

    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public UsuarioResponse findByMatricula(String matricula) {
        Usuario entity = usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado para matricula: " + matricula));
        return toResponse(entity);
    }

    public UsuarioResponse findByEmail(String email) {
        Usuario entity = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado para email: " + email));
        return toResponse(entity);
    }

    public UsuarioResponse update(UUID id, UsuarioRequest request) {
        Usuario entity = getEntity(id);
        applyRequest(entity, request);
        return toResponse(usuarioRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário nao encontrado para id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario getEntity(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário nao encontrado para id: " + id));
    }

    private void applyRequest(Usuario entity, UsuarioRequest request) {
        entity.setMatricula(request.matricula());
        entity.setSenha(request.senha());
        entity.setNome(request.nome());
        entity.setSobrenome(request.sobrenome());
        entity.setCurso(request.curso());
        entity.setFotoPerfil(request.fotoPerfil());
        entity.setAcesso(request.acesso());
        if (request.isAtivada() != null) {
            entity.setIsAtivada(request.isAtivada());
        }
        if (request.isBloqueado() != null) {
            entity.setIsBloqueado(request.isBloqueado());
        }
    }

    private UsuarioResponse toResponse(Usuario entity) {
        return new UsuarioResponse(
                entity.getId(),
                entity.getUsuario(),
                entity.getMatricula(),
                entity.getEmail(),
                entity.getNome(),
                entity.getSobrenome(),
                entity.getCurso(),
                entity.getFotoPerfil(),
                entity.getAcesso(),
                entity.getIsAtivada(),
                entity.getIsBloqueado()
        );
    }
}
