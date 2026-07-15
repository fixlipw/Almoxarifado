package com.ufc.almoxarifado.usuario;

import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.usuario.internal.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthSecurityService {

    private final UsuarioRepository usuarioRepository;

    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return usuarioRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para email: " + email))
                    .getId();
        }

        String username = jwt.getClaimAsString("preferred_username");
        if (username != null && !username.isBlank()) {
            return usuarioRepository.findByUsernameIgnoreCase(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para username: " + username))
                    .getId();
        }

        throw new ResourceNotFoundException("Usuário autenticado não possui email nem username no token.");
    }

    public Usuario getUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            Optional<Usuario> usuarioPorEmail = usuarioRepository.findByEmailIgnoreCase(email);
            if (usuarioPorEmail.isPresent()) {
                return usuarioPorEmail.get();
            }
        }

        String username = jwt.getClaimAsString("preferred_username");
        if (username != null && !username.isBlank()) {
            Optional<Usuario> usuarioPorUsername = usuarioRepository.findByUsernameIgnoreCase(username);
            if (usuarioPorUsername.isPresent()) {
                return usuarioPorUsername.get();
            }
        }

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Token do usuário não possui e-mail.");
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Usuário não sincronizado no sistema.");
    }


    public boolean hasAdminOrStaffAccess(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority() != null && (a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_BOLSISTA")));
    }

    public Long getAllowedUserId(Long requestedId, Authentication authentication) {
        if (hasAdminOrStaffAccess(authentication)) {
            return requestedId;
        }
        Long current = getCurrentUserId(authentication);
        if (requestedId != null && !requestedId.equals(current)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar pedidos de outros usuários.");
        }
        return current;
    }
}
