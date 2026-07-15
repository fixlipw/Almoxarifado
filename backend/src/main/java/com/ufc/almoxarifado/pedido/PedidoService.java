package com.ufc.almoxarifado.pedido;

import com.ufc.almoxarifado.estoque.Estoque;
import com.ufc.almoxarifado.estoque.EstoqueService;
import com.ufc.almoxarifado.exception.ResourceNotFoundException;
import com.ufc.almoxarifado.pedido.internal.ItemPedido;
import com.ufc.almoxarifado.pedido.internal.Pedido;
import com.ufc.almoxarifado.pedido.internal.PedidoMapper;
import com.ufc.almoxarifado.pedido.internal.PedidoRepository;
import com.ufc.almoxarifado.usuario.Usuario;
import com.ufc.almoxarifado.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstoqueService estoqueService;
    private final UsuarioService usuarioService;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponse create(CriarPedidoRequest request, Long solicitanteId) {
        Pedido entity = new Pedido();
        entity.setCodigoPedido(request.codigoPedido());
        entity.setSolicitante(usuarioService.getEntity(solicitanteId));
        entity.setDataSolicitacao(LocalDateTime.now());
        applyEditableFields(entity, request);
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findAll(Long userId) {
        if (userId != null) {
            return pedidoRepository.findBySolicitanteId(userId).stream().map(pedidoMapper::toResponse).toList();
        }
        return pedidoRepository.findAll().stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findApproved(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findApprovedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findApproved();
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findApprovedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findApprovedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findApprovedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findPending(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findPendingByUserId(userId);
        } else {
            pedidos = pedidoRepository.findPending();
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findPendingPaginated(Long allowedUserId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (allowedUserId != null) {
            pedidos = pedidoRepository.findPendingByUserIdPaginated(allowedUserId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findPendingPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findActive(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findActiveByUserId(userId);
        } else {
            pedidos = pedidoRepository.findActive();
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findActivePaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findActiveByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findActivePaginated(PageRequest.of(page, size));
        }
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findReturned(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findReturnedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findReturned();
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findReturnedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findReturnedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findReturnedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findRejected(Long userId) {
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findRejectedByUserId(userId);
        } else {
            pedidos = pedidoRepository.findRejected();
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponse> findRejectedPaginated(Long userId, Integer page, Integer size) {
        Page<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findRejectedByUserIdPaginated(userId, PageRequest.of(page, size));
        } else {
            pedidos = pedidoRepository.findRejectedPaginated(PageRequest.of(page, size));
        }
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> findDelayed(Long userId) {
        LocalDateTime threshold = LocalDateTime.now().toLocalDate().atTime(18, 0);
        List<Pedido> pedidos;
        if (userId != null) {
            pedidos = pedidoRepository.findDelayedByUserId(userId, threshold);
        } else {
            pedidos = pedidoRepository.findDelayed(threshold);
        }
        return pedidos.stream().map(pedidoMapper::toResponse).toList();
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
        return pedidos.map(pedidoMapper::toResponse);
    }

    @Transactional
    public PedidoResponse approvePedido(UUID id, Long aprovadorId) {
        Pedido entity = getEntity(id);
        if (entity.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Somente pedidos pendentes podem ser aprovados.");
        }
        entity.setStatus(StatusPedido.APROVADO);

        for (ItemPedido item : entity.getItens()) {
            estoqueService.deductStock(item.getEstoque().getId(), item.getQuantidadeItem());
        }

        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(aprovadorId));
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    @Transactional
    public PedidoResponse rejectPedido(UUID id, Long finalizadorId) {
        Pedido entity = getEntity(id);
        if (entity.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Somente pedidos pendentes podem ser rejeitados.");
        }
        entity.setStatus(StatusPedido.REJEITADO);
        entity.setDataAprovacao(LocalDateTime.now());
        entity.setAprovador(getOptionalUser(finalizadorId));
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    @Transactional
    public PedidoResponse returnPedido(UUID id, Long finalizadorId) {
        Pedido entity = getEntity(id);
        if (entity.getStatus() != StatusPedido.APROVADO) {
            throw new IllegalStateException("Somente pedidos aprovados e ainda não finalizados podem ser devolvidos.");
        }
        entity.setStatus(StatusPedido.FINALIZADO);

        for (ItemPedido item : entity.getItens()) {
            estoqueService.replenishStock(item.getEstoque().getId(), item.getQuantidadeItem());
        }

        entity.setDataFinalizado(LocalDateTime.now());
        entity.setFinalizador(getOptionalUser(finalizadorId));
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PedidoResponse findById(UUID id) {
        return pedidoMapper.toResponse(getEntity(id));
    }

    @Transactional
    public PedidoResponse update(UUID id, AtualizarPedidoRequest request, Long actorId, boolean staffAccess) {
        Pedido entity = getEntity(id);
        boolean owner = entity.getSolicitante() != null && entity.getSolicitante().getId().equals(actorId);
        if (!staffAccess && !owner) {
            throw new org.springframework.security.access.AccessDeniedException("Você não tem permissão para alterar este pedido.");
        }
        if (entity.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Somente pedidos pendentes podem ser alterados.");
        }
        applyEditableFields(entity, request);
        entity.setDataAtualizacao(LocalDateTime.now());
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    @Transactional
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

    private void applyEditableFields(Pedido entity, CriarPedidoRequest request) {
        entity.setFeedback(request.feedback());
        replaceItems(entity, request.itens());
    }

    private void applyEditableFields(Pedido entity, AtualizarPedidoRequest request) {
        entity.setFeedback(request.feedback());
        replaceItems(entity, request.itens());
    }

    private void replaceItems(Pedido entity, List<ItemPedidoRequest> itens) {
        entity.getItens().clear();
        for (ItemPedidoRequest itemReq : itens) {
            Estoque estoque = estoqueService.getEntity(itemReq.estoqueId());
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(entity);
            itemPedido.setEstoque(estoque);
            itemPedido.setQuantidadeItem(itemReq.quantidadeItem());
            entity.getItens().add(itemPedido);
        }
    }

    @Transactional
    public PedidoResponse updateEmprestimoEspecial(UUID id, boolean emprestimoEspecial) {
        Pedido entity = getEntity(id);
        if (entity.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalStateException("Somente pedidos pendentes podem ser classificados como empréstimo especial.");
        }
        entity.setEmprestimoEspecial(emprestimoEspecial);
        entity.setDataAtualizacao(LocalDateTime.now());
        return pedidoMapper.toResponse(pedidoRepository.save(entity));
    }

    private Usuario getOptionalUser(Long id) {
        if (id == null) {
            return null;
        }
        return usuarioService.getEntity(id);
    }

    @Transactional(readOnly = true)
    public long countActive(Long userId) {
        return pedidoRepository.countActive(userId);
    }

    @Transactional(readOnly = true)
    public long countPending(Long userId) {
        return pedidoRepository.countPending(userId);
    }

    @Transactional(readOnly = true)
    public long countDelayed(LocalDateTime threshold, Long userId) {
        return pedidoRepository.countDelayed(threshold, userId);
    }

    @Transactional(readOnly = true)
    public long countReturned(Long userId) {
        return pedidoRepository.countReturned(userId);
    }

    @Transactional(readOnly = true)
    public long sumBorrowedItems(Long userId) {
        return pedidoRepository.sumBorrowedItems(userId);
    }
}
