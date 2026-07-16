package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.request.UsuarioRequest;
import com.ufc.almoxarifado.dto.response.UsuarioResponse;
import com.ufc.almoxarifado.dto.request.UsuarioStatusRequest;
import com.ufc.almoxarifado.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @PathVariable Long id) {
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

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioStatusRequest request
    ) {
        return ResponseEntity.ok(usuarioService.updateStatus(id, request.ativo()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
