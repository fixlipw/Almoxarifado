package com.ufc.almoxarifado.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String CLAIM_ROLES = "roles";

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    @SuppressWarnings("unchecked")
    public AbstractAuthenticationToken convert(Jwt source) {
        Set<String> tokenRoles = new LinkedHashSet<>();

        Map<String, Object> resourceAccess = source.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.get(resourceId) instanceof Map<?, ?> clientMap) {
            Object rolesObj = ((Map<String, Object>) clientMap).get(CLAIM_ROLES);
            if (rolesObj instanceof Collection<?> clientRoles) {
                clientRoles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(tokenRoles::add);
            }
        }

        addRealmRoles(source, tokenRoles);
        addAllClientRoles(source, tokenRoles);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        tokenRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .forEach(authorities::add);

        return new JwtAuthenticationToken(source, authorities);
    }

    private void addRealmRoles(Jwt source, Set<String> tokenRoles) {
        Map<String, Object> realmAccess = source.getClaim("realm_access");
        if (realmAccess == null) {
            return;
        }

        Object rolesObj = realmAccess.get(CLAIM_ROLES);
        if (rolesObj instanceof Collection<?> roles) {
            roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(tokenRoles::add);
        }
    }

    @SuppressWarnings("unchecked")
    private void addAllClientRoles(Jwt source, Set<String> tokenRoles) {
        Map<String, Object> resourceAccess = source.getClaim("resource_access");
        if (resourceAccess == null) {
            return;
        }

        for (Object entryObj : resourceAccess.values()) {
            if (!(entryObj instanceof Map<?, ?> clientMap)) {
                continue;
            }
            Object rolesObj = ((Map<String, Object>) clientMap).get(CLAIM_ROLES);
            if (rolesObj instanceof Collection<?> roles) {
                roles.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .forEach(tokenRoles::add);
            }
        }
    }

}