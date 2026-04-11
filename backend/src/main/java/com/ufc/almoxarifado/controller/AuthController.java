package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.*;
import com.ufc.almoxarifado.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/validar-sigaa")
    public ResponseEntity<SigaaResponse> validarSigaa(@RequestBody SigaaRequest request) {
        return ResponseEntity.ok(authService.validarSigaa(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<com.ufc.almoxarifado.dto.UsuarioResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
