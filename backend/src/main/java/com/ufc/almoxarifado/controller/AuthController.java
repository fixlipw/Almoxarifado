package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/session-sync")
    public ResponseEntity<Void> sessionSync() {
        // Endpoint intencionalmente vazio; apenas dispara validação/sincronização no bootstrap do frontend.
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}