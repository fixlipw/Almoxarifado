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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoResponse create(PedidoRequest request, Long solicitanteId) {
        Pedido entity = new Pedido();
        entity.setCodigoPedido(request.codigoPedido());
        applyRequest(entity, request, solicitanteId);
        return toResponse(pedidoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findAll(Long userId) {
        if (userId != null) {
            return pedidoRepository.findBySolicitanteId(userId).stream().map(this::toResponse).toList();
        }
        return pedidoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findApproved(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findApprovedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findApproved();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findApprovedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findApprovedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findApprovedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findPending(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findPendingByUserId(userId);
        } else {
            pedidos = pedidoRepository.findPending();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findPendingPaginated(Long allowedUserId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (allowedUserId != null) {
            pedidos = pedidoRepository.findPendingByUserIdPaginated(allowedUserId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findPendingPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findActive(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findActiveByUserId(userId);
        } else {
            pedidos = pedidoRepository.findActive();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findActivePaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findActiveByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findActivePaginated(PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findReturned(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findReturnedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findReturned();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findReturnedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findReturnedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findReturnedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findRejected(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findRejectedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findRejected();
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findRejectedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findRejectedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findRejectedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findDelayed(Long userId) {
        return findActive(userId).stream()
                .filter(p -> {
                    if (p.dataAprovacao() == null) {
                        return false;
                    }
                    LocalDateTime prazo = p.dataAprovacao().toLocalDate().atTime(18, 0);
                    return LocalDateTime.now().isAfter(prazo);
                }).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findDelayedPaginated(Long userId, Integer page, Integer size) {
        LocalDateTime threshold = LocalDateTime.now().toLocalDate().atTime(18, 0);
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findDelayedByUserIdPaginated(userId, threshold, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findDelayedPaginated(threshold, PageRequest.of(page, size));
        }
        return pedidos.map(this::toResponse);
    }

    @Transactional
    public PedidoResponse approvePedido(UUID id, Long aprovadorId) {
        Pedido entity = getEntity(id);
        entity.setAprovado(true);

        for (ItemPedido item : entity.getItens()) {
            Estoque estoque = item.getEstoque();
            if (estoque.getQuantidadeDisponivel() < item.getQuantidadeItem()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o item: " + estoque.getNome());
            }
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() - item.getQuantidadeItem());
            estoqueRepository.save(estoque);
        }

        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(aprovadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    @Transactional
    public PedidoResponse rejectPedido(UUID id, Long finalizadorId) {
        Pedido entity = getEntity(id);
        entity.setAprovado(false);
        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(finalizadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    @Transactional
    public PedidoResponse returnPedido(UUID id, Long finalizadorId) {
        Pedido entity = getEntity(id);
        entity.setFinalizado(true);

        for (ItemPedido item : entity.getItens()) {
            Estoque estoque = item.getEstoque();
            estoque.setQuantidadeDisponivel(estoque.getQuantidadeDisponivel() + item.getQuantidadeItem());
            estoqueRepository.save(estoque);
        }

        entity.setDataFinalizado(LocalDateTime.now());
        entity.setFinalizador(getOptionalUser(finalizadorId));
        return toResponse(pedidoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PedidoResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    public PedidoResponse update(UUID id, PedidoRequest request) {
        Pedido entity = getEntity(id);
        applyRequest(entity, request, entity.getSolicitante() != null ? entity.getSolicitante().getId() : null);
        return toResponse(pedidoRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado para id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    private Pedido getEntity(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado para id: " + id));
    }

    private void applyRequest(Pedido entity, PedidoRequest request, Long solicitanteId) {
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

    private Usuario getOptionalUser(Long id) {
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
                        item.getEstoque().getNome(),
                        item.getQuantidadeItem()
                ))
                .toList();

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
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getNomeSocial(),
                usuario.getCpf(),
                usuario.getCurso(),
                usuario.getPapel(),
                usuario.getFotoPerfil(),
                usuario.getStatus(),
                usuario.getBloqueado(),
                usuario.getMatricula()
        );
    }
}
