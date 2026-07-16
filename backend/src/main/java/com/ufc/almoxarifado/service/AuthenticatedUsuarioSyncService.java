package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.repository.UsuarioRepository;

import com.ufc.almoxarifado.model.Papel;
import com.ufc.almoxarifado.model.Usuario;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticatedUsuarioSyncService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    @SuppressWarnings("unchecked")
    public void syncFromJwt(Jwt jwt) {
        String email = normalizeEmail(jwt.getClaimAsString("email"));
        if (email == null) return;

        String username = normalizeUsername(jwt.getClaimAsString("preferred_username"));
        if (username == null) username = email;

        // Extrai as roles do token JWT
        Set<String> tokenRoles = new LinkedHashSet<>();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof Collection<?> roles) {
                roles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(tokenRoles::add);
            }
        }
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            for (Object entryObj : resourceAccess.values()) {
                if (entryObj instanceof Map<?, ?> clientMap) {
                    Object rolesObj = ((Map<String, Object>) clientMap).get("roles");
                    if (rolesObj instanceof Collection<?> roles) {
                        roles.stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .forEach(tokenRoles::add);
                    }
                }
            }
        }

        List<String> roles = new ArrayList<>(tokenRoles);

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        boolean matchedByEmail = usuario != null;

        if (usuario == null) {
            usuario = usuarioRepository.findByUsernameIgnoreCase(username).orElse(null);
        }

        if (usuario == null) {
            String availableUsername = resolveAvailableUsername(username);
            createUsuarioFromJwt(jwt, email, availableUsername, roles);
            return;
        }

        boolean changed = updateUsuario(usuario, jwt, email, username, roles, matchedByEmail);

        if (changed) {
            usuarioRepository.save(usuario);
            log.info("Usuário sincronizado a partir do token do Keycloak: email={}", email);
        }
    }

    private boolean updateUsuario(
            Usuario usuario,
            Jwt jwt,
            String email,
            String username,
            List<String> roles,
            boolean matchedByEmail
    ) {
        boolean changed = false;

        changed |= updateNome(usuario, jwt);
        changed |= updateEmail(usuario, email, matchedByEmail);
        changed |= updateUsername(usuario, username, email);
        changed |= updatePapel(usuario, roles);
        changed |= activateIfInactive(usuario);

        return changed;
    }

    private boolean updateNome(Usuario usuario, Jwt jwt) {
        String nome = extractNome(jwt);
        if (nome != null && !nome.equals(usuario.getNome())) {
            usuario.setNome(nome);
            return true;
        }
        return false;
    }

    private boolean updateEmail(Usuario usuario, String email, boolean matchedByEmail) {
        if (matchedByEmail && !email.equals(usuario.getEmail())) {
            usuario.setEmail(email);
            return true;
        }
        return false;
    }

    private boolean updateUsername(Usuario usuario, String username, String email) {
        Usuario owner = usuarioRepository.findByUsernameIgnoreCase(username).orElse(null);

        boolean canUse = owner == null || owner.getId().equals(usuario.getId());

        if (!canUse) {
            log.warn(
                    "Username do Keycloak já pertence a outro usuário local. Mantendo username atual. email={}, keycloakUsername={}",
                    email,
                    username
            );
            return false;
        }

        if (!username.equalsIgnoreCase(usuario.getUsername())) {
            usuario.setUsername(username);
            return true;
        }

        return false;
    }

    private boolean updatePapel(Usuario usuario, List<String> roles) {
        Papel novoPapel = resolvePapel(roles);
        if (novoPapel != usuario.getPapel()) {
            usuario.setPapel(novoPapel);
            return true;
        }
        return false;
    }

    private boolean activateIfInactive(Usuario usuario) {
        if (Boolean.FALSE.equals(usuario.getStatus())) {
            usuario.setStatus(true);
            return true;
        }
        return false;
    }

    private void createUsuarioFromJwt(Jwt jwt, String email, String username, List<String> roles) {
        String nome = extractNome(jwt);
        if (nome == null) {
            nome = email;
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setUsername(username);
        novoUsuario.setEmail(email);
        novoUsuario.setPapel(resolvePapel(roles));
        novoUsuario.setStatus(true);
        novoUsuario.setBloqueado(false);
        novoUsuario.setCpf(resolveCpf(jwt));

        usuarioRepository.save(novoUsuario);
        log.info("Usuário criado localmente a partir do Keycloak com papel ALUNO: email={}", email);
    }

    private Papel resolvePapel(List<String> roles) {
        if (roles.stream().anyMatch("administrador"::equalsIgnoreCase)) {
            return Papel.ADMIN;
        }
        if (roles.stream().anyMatch("bolsista"::equalsIgnoreCase)) {
            return Papel.BOLSISTA;
        }
        return Papel.ALUNO;
    }

    private String extractNome(Jwt jwt) {
        String fullName = trimToNull(jwt.getClaimAsString("name"));
        if (fullName != null) return fullName;

        String givenName = trimToNull(jwt.getClaimAsString("given_name"));
        String familyName = trimToNull(jwt.getClaimAsString("family_name"));

        if (givenName == null && familyName == null) return null;
        if (givenName == null) return familyName;
        if (familyName == null) return givenName;

        return givenName + " " + familyName;
    }

    private String normalizeEmail(String email) {
        String normalized = trimToNull(email);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private String normalizeUsername(String username) {
        String normalized = trimToNull(username);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private String resolveAvailableUsername(String baseUsername) {
        String candidate = baseUsername;
        int suffix = 1;

        while (true) {
            Usuario existing = usuarioRepository.findByUsernameIgnoreCase(candidate).orElse(null);
            if (existing == null) {
                return candidate;
            }
            candidate = baseUsername + "." + suffix++;
        }
    }

    private String resolveCpf(Jwt jwt) {
        String cpfClaim = trimToNull(jwt.getClaimAsString("cpf"));
        if (cpfClaim != null) {
            String normalized = cpfClaim.replaceAll("\\D", "");
            if (normalized.length() == 11) {
                return normalized;
            }
        }

        String candidate = generateFallbackCpf(jwt);
        int suffix = 1;
        while (usuarioRepository.existsByCpf(candidate)) {
            candidate = generateFallbackCpfWithSuffix(jwt, suffix++);
        }
        return candidate;
    }

    private String generateFallbackCpf(Jwt jwt) {
        String subject = trimToNull(jwt.getSubject());
        if (subject == null) {
            subject = String.valueOf(System.nanoTime());
        }

        byte[] bytes = subject.getBytes(StandardCharsets.UTF_8);
        long hash = 0;

        for (byte b : bytes) {
            hash = (hash * 31 + (b & 0xff)) & 0x7fffffffffffffffL;
        }

        long elevenDigits = hash % 100_000_000_000L;
        return String.format("%011d", elevenDigits);
    }

    private String generateFallbackCpfWithSuffix(Jwt jwt, int suffix) {
        String subject = trimToNull(jwt.getSubject());
        if (subject == null) {
            subject = String.valueOf(System.nanoTime());
        }
        subject = subject + "_" + suffix;

        byte[] bytes = subject.getBytes(StandardCharsets.UTF_8);
        long hash = 0;

        for (byte b : bytes) {
            hash = (hash * 31 + (b & 0xff)) & 0x7fffffffffffffffL;
        }

        long elevenDigits = hash % 100_000_000_000L;
        return String.format("%011d", elevenDigits);
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
