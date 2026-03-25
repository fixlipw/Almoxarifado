package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.PedidoRequest;
import com.ufc.almoxarifado.dto.PedidoResponse;
import com.ufc.almoxarifado.entity.Pedido;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.PedidoRepository;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoResponse create(PedidoRequest request) {
        Pedido entity = new Pedido();
        entity.setCodigoPedido(request.codigoPedido());
        applyRequest(entity, request);
        return toResponse(pedidoRepository.save(entity));
    }

    public List<PedidoResponse> findAll() {
        return pedidoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PedidoResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public PedidoResponse update(UUID id, PedidoRequest request) {
        Pedido entity = getEntity(id);
        applyRequest(entity, request);
        return toResponse(pedidoRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido nao encontrado para id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public Pedido getEntity(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado para id: " + id));
    }

    private void applyRequest(Pedido entity, PedidoRequest request) {
        Usuario solicitante = usuarioRepository.findById(request.solicitanteId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para id: " + request.solicitanteId()));

        entity.setFeedback(request.feedback());
        entity.setSolicitante(solicitante);
        entity.setAprovador(getOptionalUser(request.aprovadorId()));
        entity.setFinalizador(getOptionalUser(request.finalizadorId()));

        if (entity.getDataPedido() == null) {
            entity.setDataPedido(request.dataPedido() != null ? request.dataPedido() : LocalDateTime.now());
        }
        entity.setDataFinalizado(request.dataFinalizado());
        if (request.aprovado() != null) {
            entity.setAprovado(request.aprovado());
        }
        if (request.finalizado() != null) {
            entity.setFinalizado(request.finalizado());
        }
        if (request.emprestimoEspecial() != null) {
            entity.setEmprestimoEspecial(request.emprestimoEspecial());
        }
        entity.setHash(request.hash());
    }

    private Usuario getOptionalUser(UUID id) {
        return id == null ? null : usuarioRepository.findById(id)
                                   .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para id: " + id));
    }

    private PedidoResponse toResponse(Pedido entity) {
        return new PedidoResponse(
                entity.getId(),
                entity.getCodigoPedido(),
                entity.getFeedback(),
                entity.getSolicitante() != null ? entity.getSolicitante().getId() : null,
                entity.getAprovador() != null ? entity.getAprovador().getId() : null,
                entity.getFinalizador() != null ? entity.getFinalizador().getId() : null,
                entity.getDataPedido(),
                entity.getDataFinalizado(),
                entity.getAprovado(),
                entity.getFinalizado(),
                entity.getEmprestimoEspecial(),
                entity.getHash()
        );
    }
}

