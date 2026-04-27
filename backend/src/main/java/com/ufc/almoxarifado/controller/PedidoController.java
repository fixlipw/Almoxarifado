package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.PedidoRequest;
import com.ufc.almoxarifado.dto.PedidoResponse;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.service.PedidoService;
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
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> create(
            @Valid @RequestBody PedidoRequest request,
            @AuthenticationPrincipal Usuario loggedUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(request, loggedUser.getId()));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll(
            @RequestParam(required = false) UUID userId,
            @AuthenticationPrincipal Usuario loggedUser) {
        return ResponseEntity.ok(pedidoService.findAll(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/approved")
    public ResponseEntity<?> findApproved(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findApprovedPaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findApproved(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> findPending(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findPendingPaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findPending(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/active")
    public ResponseEntity<?> findActive(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findActivePaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findActive(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/returned")
    public ResponseEntity<?> findReturned(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findReturnedPaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findReturned(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/rejected")
    public ResponseEntity<?> findRejected(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findRejectedPaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findRejected(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/delayed")
    public ResponseEntity<?> findDelayed(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @AuthenticationPrincipal Usuario loggedUser) {
        if (page != null && size != null) {
            return ResponseEntity.ok(pedidoService.findDelayedPaginated(SecurityUtils.getAllowedUserId(userId, loggedUser), page, size));
        }
        return ResponseEntity.ok(pedidoService.findDelayed(SecurityUtils.getAllowedUserId(userId, loggedUser)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario loggedUser) {

        PedidoResponse response = pedidoService.findById(id);

        if (!SecurityUtils.hasAdminOrStaffAccess(loggedUser) && (response.solicitante() == null || !response.solicitante().id().equals(loggedUser.getId()))) {
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
            @AuthenticationPrincipal Usuario loggedUser) {
        return ResponseEntity.ok(pedidoService.approvePedido(id, loggedUser.getId()));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<PedidoResponse> rejectPedido(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario loggedUser) {
        return ResponseEntity.ok(pedidoService.rejectPedido(id, loggedUser.getId()));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<PedidoResponse> returnPedido(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario loggedUser) {
        return ResponseEntity.ok(pedidoService.returnPedido(id, loggedUser.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
