package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.PedidoRequest;
import com.ufc.almoxarifado.dto.PedidoResponse;
import com.ufc.almoxarifado.service.AuthSecurityService;
import com.ufc.almoxarifado.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final AuthSecurityService authSecurityService;

    @PostMapping
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication) {
        Long currentUserId = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(request, currentUserId));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll(
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        return ResponseEntity.ok(pedidoService.findAll(allowed));
    }

    @GetMapping("/approved")
    public ResponseEntity<Object> findApproved(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findApprovedPaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findApproved(allowed));
    }

    @GetMapping("/pending")
    public ResponseEntity<Object> findPending(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findPendingPaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findPending(allowed));
    }

    @GetMapping("/active")
    public ResponseEntity<Object> findActive(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findActivePaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findActive(allowed));
    }

    @GetMapping("/returned")
    public ResponseEntity<Object> findReturned(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findReturnedPaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findReturned(allowed));
    }

    @GetMapping("/rejected")
    public ResponseEntity<Object> findRejected(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findRejectedPaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findRejected(allowed));
    }

    @GetMapping("/delayed")
    public ResponseEntity<Object> findDelayed(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findDelayedPaginated(allowed, page, size));
        }
        return ResponseEntity.ok(pedidoService.findDelayed(allowed));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(
            @PathVariable UUID id,
            Authentication authentication) {

        PedidoResponse response = pedidoService.findById(id);
        Long currentUserId = authSecurityService.getCurrentUserId(authentication);
        boolean isAdmin = authSecurityService.hasAdminOrStaffAccess(authentication);
        if (!isAdmin && (response.solicitante() == null || !response.solicitante().id().equals(currentUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar este pedido.");
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.update(id, request));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<PedidoResponse> approvePedido(
            @PathVariable UUID id,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.ok(pedidoService.approvePedido(id, current));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<PedidoResponse> rejectPedido(
            @PathVariable UUID id,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.ok(pedidoService.rejectPedido(id, current));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<PedidoResponse> returnPedido(
            @PathVariable UUID id,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        return ResponseEntity.ok(pedidoService.returnPedido(id, current));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
