package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.request.AtualizarPedidoRequest;
import com.ufc.almoxarifado.dto.request.CriarPedidoRequest;
import com.ufc.almoxarifado.dto.request.EmprestimoEspecialRequest;
import com.ufc.almoxarifado.dto.response.PedidoResponse;
import com.ufc.almoxarifado.service.PedidoService;

import com.ufc.almoxarifado.service.AuthSecurityService;
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
            @Valid @RequestBody CriarPedidoRequest request,
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
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findApprovedPaginated(allowed, page, normalizedSize(size)));
        }
        return ResponseEntity.ok(pedidoService.findApproved(allowed));
    }

    @GetMapping("/pending")
    public ResponseEntity<Object> findPending(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findPendingPaginated(allowed, page, normalizedSize(size)));
        }
        return ResponseEntity.ok(pedidoService.findPending(allowed));
    }

    @GetMapping("/active")
    public ResponseEntity<Object> findActive(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findActivePaginated(allowed, page, normalizedSize(size)));
        }
        return ResponseEntity.ok(pedidoService.findActive(allowed));
    }

    @GetMapping("/returned")
    public ResponseEntity<Object> findReturned(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findReturnedPaginated(allowed, page, normalizedSize(size)));
        }
        return ResponseEntity.ok(pedidoService.findReturned(allowed));
    }

    @GetMapping("/rejected")
    public ResponseEntity<Object> findRejected(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findRejectedPaginated(allowed, page, normalizedSize(size)));
        }
        return ResponseEntity.ok(pedidoService.findRejected(allowed));
    }

    @GetMapping("/delayed")
    public ResponseEntity<Object> findDelayed(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication) {
        Long allowed = authSecurityService.getAllowedUserId(userId, authentication);
        if (page != null) {
            return ResponseEntity.ok(pedidoService.findDelayedPaginated(allowed, page, normalizedSize(size)));
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
            @Valid @RequestBody AtualizarPedidoRequest request,
            Authentication authentication) {
        Long current = authSecurityService.getCurrentUserId(authentication);
        boolean staffAccess = authSecurityService.hasAdminOrStaffAccess(authentication);
        return ResponseEntity.ok(pedidoService.update(id, request, current, staffAccess));
    }

    @PatchMapping("/{id}/emprestimo-especial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponse> updateEmprestimoEspecial(
            @PathVariable UUID id,
            @Valid @RequestBody EmprestimoEspecialRequest request) {
        return ResponseEntity.ok(pedidoService.updateEmprestimoEspecial(id, request.emprestimoEspecial()));
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

    private int normalizedSize(Integer size) {
        return size == null ? 10 : Math.clamp(size, 1, 100);
    }
}
