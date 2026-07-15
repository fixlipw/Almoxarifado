package com.ufc.almoxarifado.service;

import com.ufc.almoxarifado.dashboard.DashboardResponse;
import com.ufc.almoxarifado.dashboard.DashboardService;
import com.ufc.almoxarifado.estoque.EstoqueResponse;
import com.ufc.almoxarifado.estoque.EstoqueService;
import com.ufc.almoxarifado.estoque.TipoEstoque;
import com.ufc.almoxarifado.pedido.ItemPedidoResponse;
import com.ufc.almoxarifado.pedido.PedidoResponse;
import com.ufc.almoxarifado.pedido.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTests {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private EstoqueService estoqueService;


    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(pedidoService, estoqueService);
    }

    @Test
    void studentDashboardScopesOrdersAndDoesNotExposeStock() {
        Long userId = 42L;
        PedidoResponse active = pedido("ACTIVE", LocalDateTime.now().minusHours(2), 3);
        PedidoResponse delayed = pedido("DELAYED", LocalDateTime.now().minusDays(2), 1);

        when(pedidoService.countPending(userId)).thenReturn(0L);
        when(pedidoService.countActive(userId)).thenReturn(2L);
        when(pedidoService.countDelayed(any(LocalDateTime.class), eq(userId))).thenReturn(1L);
        when(pedidoService.countReturned(userId)).thenReturn(0L);
        when(pedidoService.sumBorrowedItems(userId)).thenReturn(4L);

        when(pedidoService.findActive(userId)).thenReturn(List.of(active, delayed));
        when(pedidoService.findPending(userId)).thenReturn(List.of());
        when(pedidoService.findDelayed(userId)).thenReturn(List.of(delayed));

        DashboardResponse response = dashboardService.getDashboard(userId, false);

        assertThat(response.gerencial()).isFalse();
        assertThat(response.pedidosAtivos()).isEqualTo(2);
        assertThat(response.pedidosAtrasados()).isEqualTo(1);
        assertThat(response.itensEmprestados()).isEqualTo(4);
        assertThat(response.totalTiposEstoque()).isZero();
        assertThat(response.estoqueBaixo()).isEmpty();
        assertThat(response.pedidosRecentes()).extracting("codigoPedido").containsExactly("ACTIVE", "DELAYED");
        verifyNoInteractions(estoqueService);
    }

    @Test
    void managerialDashboardReturnsGlobalLowStockAndPrioritizesDelayedStatus() {
        PedidoResponse delayed = pedido("DELAYED", LocalDateTime.now().minusDays(1), 2);
        EstoqueResponse critical = stock("Cabo HDMI", 20, 2, true);
        EstoqueResponse low = stock("Adaptador", 20, 5, true);
        EstoqueResponse healthy = stock("Mouse", 20, 15, true);
        EstoqueResponse disabled = stock("Projetor", 10, 0, false);

        when(pedidoService.countPending(null)).thenReturn(1L);
        when(pedidoService.countActive(null)).thenReturn(1L);
        when(pedidoService.countDelayed(any(LocalDateTime.class), eq(null))).thenReturn(1L);
        when(pedidoService.countReturned(null)).thenReturn(0L);
        when(pedidoService.sumBorrowedItems(null)).thenReturn(2L);

        when(pedidoService.findActive(null)).thenReturn(List.of(delayed));
        when(pedidoService.findPending(null)).thenReturn(List.of(delayed));
        when(pedidoService.findDelayed(null)).thenReturn(List.of(delayed));
        when(estoqueService.findAll()).thenReturn(List.of(healthy, low, critical, disabled));

        DashboardResponse response = dashboardService.getDashboard(null, true);

        assertThat(response.gerencial()).isTrue();
        assertThat(response.totalTiposEstoque()).isEqualTo(4);
        assertThat(response.itensEstoqueBaixo()).isEqualTo(2);
        assertThat(response.estoqueBaixo()).extracting("nome").containsExactly("Cabo HDMI", "Adaptador");
        assertThat(response.pedidosRecentes()).hasSize(1);
        assertThat(response.pedidosRecentes().getFirst().status()).isEqualTo("ATRASADO");
    }

    private PedidoResponse pedido(String code, LocalDateTime date, int quantity) {
        UUID pedidoId = UUID.randomUUID();
        ItemPedidoResponse item = new ItemPedidoResponse(
                UUID.randomUUID(),
                pedidoId,
                UUID.randomUUID(),
                "Item",
                quantity
        );
        return new PedidoResponse(
                pedidoId,
                code,
                null,
                null,
                null,
                null,
                List.of(item),
                date,
                date,
                null,
                date,
                true,
                false,
                false,
                null
        );
    }

    private EstoqueResponse stock(String name, int total, int available, boolean active) {
        return new EstoqueResponse(
                UUID.randomUUID(),
                name,
                total,
                available,
                TipoEstoque.COMPONENTE,
                active
        );
    }
}
