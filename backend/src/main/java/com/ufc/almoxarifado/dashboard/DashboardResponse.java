package com.ufc.almoxarifado.dashboard;

import com.ufc.almoxarifado.estoque.EstoqueResponse;

import java.util.List;

public record DashboardResponse(
        boolean gerencial,
        long pedidosPendentes,
        long pedidosAtivos,
        long pedidosAtrasados,
        long pedidosFinalizados,
        long itensEmprestados,
        long totalTiposEstoque,
        long itensEstoqueBaixo,
        List<DashboardPedidoResponse> pedidosRecentes,
        List<EstoqueResponse> estoqueBaixo
) {
}
