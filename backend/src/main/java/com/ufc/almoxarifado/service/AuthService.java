package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.config.TokenService;
import com.ufc.almoxarifado.dto.*;
import com.ufc.almoxarifado.entity.RoleAcesso;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import com.ufc.almoxarifado.rest.SigaaRestClient;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import com.ufc.almoxarifado.config.EmailService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SigaaRestClient sigaaRestClient;
    private final TokenService tokenService;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    public SigaaResponse validarSigaa(@Valid SigaaRequest request) {
        return sigaaRestClient.validarCredenciais(request);
    }

    @Transactional
    public void register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByUsuario(request.login()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe.");
        }

        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado.");
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
        usuario.setIsAtivada(false);

        notificarAtivacao(usuario);
        usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuarioOpt = usuarioRepository.findByUsuario(request.login()).orElse(null);
        if (usuarioOpt != null && !usuarioOpt.getIsAtivada()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não ativado. Verifique seu email para concluir a ativação.");
        }

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.senha());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Usuario usuario = (Usuario) auth.getPrincipal();

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.");
        }

        String token = tokenService.generateToken(usuario);

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getMatricula(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getCurso(),
                usuario.getFotoPerfil(),
                usuario.getAcesso(),
                usuario.getIsAtivada(),
                usuario.getIsBloqueado()
        );
        return new LoginResponse(token, usuarioResponse);
    }

    public void ativarConta(String token) {
        String login = tokenService.validateToken(token, "activation");
        if (login == null || login.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token de ativação inválido ou expirado.");
        }

        Usuario usuario = usuarioRepository.findByUsuario(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        if (usuario.getIsAtivada()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta conta já está ativada.");
        }

        usuario.setIsAtivada(true);
        usuarioRepository.save(usuario);
    }

    private void notificarAtivacao(Usuario usuario) {
        String token = tokenService.generateToken(usuario, "activation");
        String link = "http://almoxarifadoec.quixada.ufc.br:8090/api/auth/ativar?token=" + token;

        emailService.sendEmail(
                usuario.getEmail(),
                "Ativação de Conta - Almoxarifado da UFC",
                "ativacao.ftlh",
                Map.of(
                        "nome", usuario.getNome(),
                        "linkAtivacao", link
                )
        );
    }
}