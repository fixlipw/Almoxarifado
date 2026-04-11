package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.RegisterRequest;
import com.ufc.almoxarifado.dto.SigaaRequest;
import com.ufc.almoxarifado.dto.SigaaResponse;
import com.ufc.almoxarifado.dto.UsuarioResponse;
import com.ufc.almoxarifado.entity.RoleAcesso;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import com.ufc.almoxarifado.rest.SigaaRestClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SigaaRestClient sigaaRestClient;

    public SigaaResponse validarSigaa(@Valid SigaaRequest request) {
        return sigaaRestClient.validarCredenciais(request);
    }

    public UsuarioResponse register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByUsuario(request.login()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setSobrenome(request.sobrenome());
        usuario.setCurso(request.curso());
        usuario.setFotoPerfil(request.fotoPerfil());
        usuario.setMatricula(request.matricula());
        usuario.setEmail(request.email());
        usuario.setUsuario(request.login());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setAcesso(RoleAcesso.ALUNO);
        usuario.setIsAtivada(true);

        usuario = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getCurso(),
                usuario.getFotoPerfil(),
                usuario.getAcesso(),
                usuario.getIsAtivada(),
                usuario.getIsBloqueado()
        );
    }

    public UsuarioResponse login(com.ufc.almoxarifado.dto.LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsuario(request.login())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.");
        }

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getCurso(),
                usuario.getFotoPerfil(),
                usuario.getAcesso(),
                usuario.getIsAtivada(),
                usuario.getIsBloqueado()
        );
    }
}