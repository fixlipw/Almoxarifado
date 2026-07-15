package com.ufc.almoxarifado.usuario;

import com.ufc.almoxarifado.usuario.internal.AuthenticatedUsuarioSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticatedUsuarioSyncService authenticatedUsuarioSyncService;

    @GetMapping("/session-sync")
    public ResponseEntity<Void> sessionSync(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            authenticatedUsuarioSyncService.syncFromJwt(jwt);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}