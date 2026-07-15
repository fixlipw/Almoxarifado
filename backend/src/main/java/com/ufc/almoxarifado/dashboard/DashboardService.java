package com.ufc.almoxarifado.dashboard;

import com.ufc.almoxarifado.estoque.EstoqueResponse;
import com.ufc.almoxarifado.estoque.EstoqueService;
import com.ufc.almoxarifado.pedido.PedidoResponse;
import com.ufc.almoxarifado.pedido.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int RECENT_REQUESTS_LIMIT = 5;
    private static final int LOW_STOCK_LIMIT = 5;
    private static final int LOW_STOCK_PERCENTAGE = 25;

    private final PedidoService pedidoService;
    private final EstoqueService estoqueService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId, boolean managerial) {
        LocalDateTime threshold = LocalDateTime.now().toLocalDate().atTime(18, 0);

        long countPending = pedidoService.countPending(userId);
        long countActive = pedidoService.countActive(userId);
        long countDelayed = pedidoService.countDelayed(threshold, userId);
        long countReturned = pedidoService.countReturned(userId);
        long borrowedItems = pedidoService.sumBorrowedItems(userId);

        List<PedidoResponse> recentDelayed = pedidoService.findDelayed(userId);
        List<PedidoResponse> recentPending = pedidoService.findPending(userId);
        List<PedidoResponse> recentActive = pedidoService.findActive(userId);

        List<EstoqueResponse> stock = managerial ? estoqueService.findAll() : List.of();
        List<EstoqueResponse> allLowStock = stock.stream()
                .filter(item -> !Boolean.FALSE.equals(item.isAtivado()))
                .filter(item -> availabilityPercentage(item) <= LOW_STOCK_PERCENTAGE)
                .sorted(Comparator.comparingInt(this::availabilityPercentage)
                        .thenComparing(EstoqueResponse::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<EstoqueResponse> lowStock = allLowStock.stream()
                .limit(LOW_STOCK_LIMIT)
                .toList();

        return new DashboardResponse(
                managerial,
                (int) countPending,
                (int) countActive,
                (int) countDelayed,
                (int) countReturned,
                borrowedItems,
                stock.size(),
                allLowStock.size(),
                recentRequests(recentDelayed, recentPending, recentActive),
                lowStock
        );
    }

    private List<DashboardPedidoResponse> recentRequests(
            List<PedidoResponse> delayed,
            List<PedidoResponse> pending,
            List<PedidoResponse> active
    ) {
        Map<Object, DashboardPedidoResponse> unique = new LinkedHashMap<>();
        addRequests(unique, delayed, "ATRASADO");
        addRequests(unique, pending, "PENDENTE");
        addRequests(unique, active, "ATIVO");

        return unique.values().stream()
                .sorted(Comparator.comparing(
                        DashboardPedidoResponse::data,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(RECENT_REQUESTS_LIMIT)
                .toList();
    }

    private void addRequests(Map<Object, DashboardPedidoResponse> target, List<PedidoResponse> requests, String status) {
        for (PedidoResponse request : requests) {
            target.putIfAbsent(request.id(), new DashboardPedidoResponse(
                    request.id(),
                    request.codigoPedido(),
                    requesterName(request),
                    request.itens().size(),
                    latestDate(request),
                    status
            ));
        }
    }

    private String requesterName(PedidoResponse request) {
        if (request.solicitante() == null) {
            return "Solicitante";
        }
        if (request.solicitante().nome() != null && !request.solicitante().nome().isBlank()) {
            return request.solicitante().nome();
        }
        if (request.solicitante().email() != null && !request.solicitante().email().isBlank()) {
            return request.solicitante().email();
        }
        return "Solicitante";
    }

    private LocalDateTime latestDate(PedidoResponse request) {
        return request.dataAtualizacao() != null ? request.dataAtualizacao() : request.dataSolicitacao();
    }

    private int availabilityPercentage(EstoqueResponse item) {
        if (item.quantidade() == null || item.quantidade() <= 0 || item.quantidadeDisponivel() == null) {
            return 0;
        }
        double percentage = item.quantidadeDisponivel() * 100.0 / item.quantidade();
        return (int) Math.max(0, Math.min(100, Math.round(percentage)));
    }
}
