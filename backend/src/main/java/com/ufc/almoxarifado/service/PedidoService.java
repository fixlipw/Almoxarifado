package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dto.*;
import com.ufc.almoxarifado.entity.Estoque;
import com.ufc.almoxarifado.entity.ItemPedido;
import com.ufc.almoxarifado.entity.Pedido;
import com.ufc.almoxarifado.entity.Usuario;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.repository.EstoqueRepository;
import com.ufc.almoxarifado.repository.PedidoRepository;
import com.ufc.almoxarifado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoResponse create(PedidoRequest request, UUID solicitanteId) {
        Pedido entity = new Pedido();
        entity.setCodigoPedido(request.codigoPedido());
        applyRequest(entity, request, solicitanteId);
        return toResponse(pedidoRepository.save(entity));
    }

    public List<PedidoResponse> findAll(UUID userId) {
        if (userId != null) {
            return pedidoRepository.findBySolicitanteId(userId).stream().map(this::toResponse).toList();
        }
        return pedidoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> findApproved(UUID userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findApprovedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findApproved();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> findPending(UUID userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findPendingByUserId(userId);
        } else {
            pedidos = pedidoRepository.findPending();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> findActive(UUID userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findActiveByUserId(userId);
        } else {
            pedidos = pedidoRepository.findActive();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> findRejected(UUID userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findRejectedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findRejected();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> findDelayed(UUID userId) {
        return findActive(userId).stream()
                .filter(p -> {
                    if (p.dataAprovacao() == null) {
                        return false;
                    }
                    LocalDateTime prazo = p.dataAprovacao().toLocalDate().atTime(18, 0);
                    return LocalDateTime.now().isAfter(prazo);
                })
                .toList();
    }

    public PedidoResponse approvePedido(UUID id, UUID aprovadorId) {
        Pedido entity = getEntity(id);
        entity.setAprovado(true);
        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(aprovadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    public PedidoResponse rejectPedido(UUID id, UUID finalizadorId) {
        Pedido entity = getEntity(id);
        entity.setAprovado(false);
        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(finalizadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    public PedidoResponse returnPedido(UUID id, UUID finalizadorId) {
        Pedido entity = getEntity(id);
        entity.setFinalizado(true);
        entity.setDataFinalizado(LocalDateTime.now());
        entity.setFinalizador(getOptionalUser(finalizadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    public PedidoResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public PedidoResponse update(UUID id, PedidoRequest request) {
        Pedido entity = getEntity(id);
        applyRequest(entity, request, entity.getSolicitante() != null ? entity.getSolicitante().getId() : null);
        return toResponse(pedidoRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado para id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public Pedido getEntity(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado para id: " + id));
    }

    private void applyRequest(Pedido entity, PedidoRequest request, UUID solicitanteId) {
        if (solicitanteId != null) {
            Usuario solicitante = usuarioRepository.findById(solicitanteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para id: " + solicitanteId));
            entity.setSolicitante(solicitante);
        }

        entity.setFeedback(request.feedback());

        if (entity.getDataSolicitacao() == null) {
            if (request.dataSolicitacao() != null) {
                entity.setDataSolicitacao(request.dataSolicitacao());
            } else {
                entity.setDataSolicitacao(LocalDateTime.now());
            }
        }
        entity.setDataAprovacao(request.dataAprovacao());
        entity.setDataFinalizado(request.dataFinalizado());
        entity.setDataAtualizacao(request.dataAtualizacao());

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

        if (request.itens() != null) {
            entity.getItens().clear();
            for (ItemPedidoRequest itemReq : request.itens()) {
                Estoque estoque = estoqueRepository.findById(itemReq.estoqueId())
                        .orElseThrow(() -> new ResourceNotFoundException("Item de estoque não encontrado para id: " + itemReq.estoqueId()));
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPedido(entity);
                itemPedido.setEstoque(estoque);
                itemPedido.setQuantidadeItem(itemReq.quantidadeItem());
                entity.getItens().add(itemPedido);
            }
        }
    }

    private Usuario getOptionalUser(UUID id) {
        if (id == null) {
            return null;
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para id: " + id));
    }

    private PedidoResponse toResponse(Pedido entity) {
        List<ItemPedidoResponse> itens = entity.getItens().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        entity.getId(),
                        item.getEstoque().getId(),
                        item.getQuantidadeItem()
                ))
                .collect(Collectors.toList());

        if (entity.getFinalizador() != null) {
            return new PedidoResponse(
                    entity.getId(),
                    entity.getCodigoPedido(),
                    entity.getFeedback(),
                    entity.getSolicitante() != null ? toUsuarioResponse(entity.getSolicitante()) : null,
                    entity.getAprovador() != null ? toUsuarioResponse(entity.getAprovador()) : null,
                    toUsuarioResponse(entity.getFinalizador()),
                    itens,
                    entity.getDataSolicitacao(),
                    entity.getDataAprovacao(),
                    entity.getDataFinalizado(),
                    entity.getDataAtualizacao(),
                    entity.getAprovado(),
                    entity.getFinalizado(),
                    entity.getEmprestimoEspecial(),
                    entity.getHash()
            );
        }
        if (entity.getAprovador() != null) {
            return new PedidoResponse(
                    entity.getId(),
                    entity.getCodigoPedido(),
                    entity.getFeedback(),
                    entity.getSolicitante() != null ? toUsuarioResponse(entity.getSolicitante()) : null,
                    toUsuarioResponse(entity.getAprovador()),
                    null,
                    itens,
                    entity.getDataSolicitacao(),
                    entity.getDataAprovacao(),
                    entity.getDataFinalizado(),
                    entity.getDataAtualizacao(),
                    entity.getAprovado(),
                    entity.getFinalizado(),
                    entity.getEmprestimoEspecial(),
                    entity.getHash()
            );
        }
        if (entity.getSolicitante() != null) {
            return new PedidoResponse(
                    entity.getId(),
                    entity.getCodigoPedido(),
                    entity.getFeedback(),
                    toUsuarioResponse(entity.getSolicitante()),
                    null,
                    null,
                    itens,
                    entity.getDataSolicitacao(),
                    entity.getDataAprovacao(),
                    entity.getDataFinalizado(),
                    entity.getDataAtualizacao(),
                    entity.getAprovado(),
                    entity.getFinalizado(),
                    entity.getEmprestimoEspecial(),
                    entity.getHash()
            );
        }
        return new PedidoResponse(
                entity.getId(),
                entity.getCodigoPedido(),
                entity.getFeedback(),
                null,
                null,
                null,
                itens,
                entity.getDataSolicitacao(),
                entity.getDataAprovacao(),
                entity.getDataFinalizado(),
                entity.getDataAtualizacao(),
                entity.getAprovado(),
                entity.getFinalizado(),
                entity.getEmprestimoEspecial(),
                entity.getHash()
        );
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getCurso(),
                usuario.getFotoPerfil(),
                usuario.getAcesso(),
                usuario.getIsAtivada(),
                usuario.getIsBloqueado()
        );
    }
}
