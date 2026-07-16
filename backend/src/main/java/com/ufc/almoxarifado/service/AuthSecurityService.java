package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.model.Usuario;

import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.UsuarioRepository;
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
    private final AuthenticatedUsuarioSyncService authenticatedUsuarioSyncService;

    public Long getCurrentUserId(Authentication authentication) {
        return resolveUsuario(authentication).getId();
    }

    public Usuario getUsuarioLogado() {
        return resolveUsuario(SecurityContextHolder.getContext().getAuthentication());
    }

    private Usuario resolveUsuario(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        authenticatedUsuarioSyncService.syncFromJwt(jwt);

        String email = trimToNull(jwt.getClaimAsString("email"));
        if (email != null) {
            Optional<Usuario> usuarioPorEmail = usuarioRepository.findByEmailIgnoreCase(email);
            if (usuarioPorEmail.isPresent()) {
                return usuarioPorEmail.get();
            }
        }

        String username = trimToNull(jwt.getClaimAsString("preferred_username"));
        if (username != null) {
            Optional<Usuario> usuarioPorUsername = usuarioRepository.findByUsernameIgnoreCase(username);
            if (usuarioPorUsername.isPresent()) {
                return usuarioPorUsername.get();
            }
        }

        if (email == null && username == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Token do usuário não possui e-mail nem username.");
        }

        throw new ResourceNotFoundException("Não foi possível sincronizar o usuário autenticado no sistema.");
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
