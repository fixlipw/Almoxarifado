package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.*;
import com.ufc.almoxarifado.service.AuthService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/validar-sigaa")
    @PermitAll
    public ResponseEntity<SigaaResponse> validarSigaa(
            @RequestBody SigaaRequest request) {
        return ResponseEntity.ok(authService.validarSigaa(request));
    }

    @PostMapping("/registro")
    @PermitAll
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Usuário registrado com sucesso. Verifique seu email para ativar a conta.");
    }

    @GetMapping("/ativar")
    @PermitAll
    public ResponseEntity<String> ativarConta(@RequestParam String token) {
        authService.ativarConta(token);
        return ResponseEntity.ok("Conta ativada com sucesso! Você já pode realizar o login.");
    }

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
