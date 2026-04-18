package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.UsuarioRequest;
import com.ufc.almoxarifado.dto.UsuarioResponse;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.service.UsuarioService;
import com.ufc.almoxarifado.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<UsuarioResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario loggedUser) {

        if (!id.equals(loggedUser.getId()) && !SecurityUtils.hasAdminOrStaffAccess(loggedUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar os dados deste usuário.");
        }

        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/matricula/{matricula}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<UsuarioResponse> findByMatricula(
            @PathVariable String matricula) {
        return ResponseEntity.ok(usuarioService.findByMatricula(matricula));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<UsuarioResponse> findByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(usuarioService.findByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequest request,
            @AuthenticationPrincipal Usuario loggedUser) {

        if (!id.equals(loggedUser.getId()) && !SecurityUtils.hasAdminOrStaffAccess(loggedUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar os dados deste usuário.");
        }

        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
