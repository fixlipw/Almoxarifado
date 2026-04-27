import type {PedidoDetalhesProps, PedidoVisualProps} from "@/components/pedido/types.ts";
import {formatDate} from "@/utils/index.ts";
import type {ItemPedidoResponse, PedidoResponse} from "@/types/dtos.ts";

export function mapearParaPedidoVisual(apiPedido: PedidoResponse): PedidoVisualProps {
    let status = 'Pendente'
    let statusColor = 'warning'

    if (apiPedido.finalizado) {
        status = 'Finalizado'
        statusColor = 'grey'
    } else if (apiPedido.aprovado) {
        status = 'Ativo'
        statusColor = 'success'
    }

    const solicitante = apiPedido.solicitante

    return {
        id: apiPedido.id,
        codigo: apiPedido.codigoPedido || '',
        nome: solicitante?.nome ? (solicitante.sobrenome && !solicitante.nome.includes(solicitante.sobrenome) ? `${solicitante.nome} ${solicitante.sobrenome}` : solicitante.nome).trim() : 'Desconhecido',
        matricula: solicitante && solicitante.matricula ? solicitante.matricula : 'Desconhecida',
        email: solicitante && solicitante.email ? solicitante.email : 'Desconhecido',
        curso: solicitante && solicitante.curso ? solicitante.curso : 'Desconhecido',
        tipo: solicitante && solicitante.acesso ? solicitante.acesso : 'Desconhecido',
        solicitadoEm: apiPedido.dataSolicitacao ? formatDate(apiPedido.dataSolicitacao) : '',
        aprovadoEm: apiPedido.dataAprovacao ? formatDate(apiPedido.dataAprovacao) : null,
        finalizadoEm: apiPedido.dataFinalizado ? formatDate(apiPedido.dataFinalizado) : null,
        dataAtualizacao: apiPedido.dataAtualizacao ? formatDate(apiPedido.dataAtualizacao) : (apiPedido.dataSolicitacao ? formatDate(apiPedido.dataSolicitacao) : ''),
        status,
        statusColor,
        observacoes: apiPedido.feedback || '',
        items: (apiPedido.itens || []).map((i: ItemPedidoResponse) => ({
            id: i.id,
            estoqueId: i.estoqueId,
            name: i.nomeItem || 'Item Desconhecido',
            quantity: i.quantidadeItem,
        }))
    }
}

export function mapearPedidoDetalhes(emprestimo: PedidoVisualProps): PedidoDetalhesProps {
    return {
        codigo: emprestimo.codigo,
        status: emprestimo.status,
        solicitante: {
            nome: emprestimo.nome,
            email: emprestimo.email,
            matricula: emprestimo.matricula,
            curso: emprestimo.curso,
            tipo: emprestimo.tipo,
        },
        itens: emprestimo.items.map(i => ({
            id: i.id,
            estoqueId: i.id.toString(),
            nomeItem: i.name,
            quantidadeItem: i.quantity,
        })),
        dataSolicitacao: emprestimo.solicitadoEm,
        dataAprovacao: emprestimo.aprovadoEm ?? undefined,
        dataDevolucao: emprestimo.finalizadoEm ?? undefined,
        dataAtualizacao: emprestimo.dataAtualizacao,
        observacoes: emprestimo.observacoes,
    }
}
