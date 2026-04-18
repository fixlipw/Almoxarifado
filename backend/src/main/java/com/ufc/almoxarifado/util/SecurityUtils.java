package com.ufc.almoxarifado.util;

import com.ufc.almoxarifado.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getAllowedUserId(UUID requestedId, Usuario loggedUser) {
        boolean isAdminOrStaff = loggedUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority() != null && (
                        a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_BOLSISTA")));

        if (isAdminOrStaff) {
            return requestedId;
        }

        if (requestedId != null && !requestedId.equals(loggedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar pedidos de outros usuários.");
        }

        return loggedUser.getId();
    }

    public static boolean hasAdminOrStaffAccess(Usuario loggedUser) {
        return loggedUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority() != null && (
                        a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_BOLSISTA")));
    }
}

