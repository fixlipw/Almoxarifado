package com.ufc.almoxarifado.controller;

import com.ufc.almoxarifado.dto.EstoqueRequest;
import com.ufc.almoxarifado.dto.EstoqueResponse;
import com.ufc.almoxarifado.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private static final MediaType APPLICATION_PDF = MediaType.parseMediaType("application/pdf");

    private final EstoqueService estoqueService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<EstoqueResponse> create(
            @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<EstoqueResponse>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(estoqueService.findAllPaginated(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @GetMapping("/relatorio/{format}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<ByteArrayResource> getItemReport(@PathVariable String format) {
        byte[] reportContent = estoqueService.generateItemReport(format);
        ByteArrayResource resource = new ByteArrayResource(reportContent);

        return ResponseEntity.ok()
                .contentType(APPLICATION_PDF)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("almoxarifado-relatorio-estoque.pdf", StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<EstoqueResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody EstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BOLSISTA')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
