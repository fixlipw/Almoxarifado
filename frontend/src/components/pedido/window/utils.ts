import type {PedidoVisualProps} from "@/components/pedido/window/types.ts";
import type { PedidoDetalhesProps } from "@/components/pedido/types.ts";
import { formatDate } from "@/utils/index.ts";

export function mapearParaPedidoVisual (apiPedido: any): PedidoVisualProps {
  let status = 'Pendente'
  let statusColor = 'warning'

  if (apiPedido.finalizado) {
    status = 'Finalizado'
    statusColor = 'grey'
  } else if (apiPedido.aprovado) {
    status = 'Ativo'
    statusColor = 'success'
  } else if (apiPedido.aprovado === false) {
    status = 'Rejeitado'
    statusColor = 'error'
  }

  const solicitante = apiPedido.solicitante || {}

  return {
    id: apiPedido.id,
    codigo: apiPedido.codigo_pedido,
    nome: solicitante.nome ? `${solicitante.nome} ${solicitante.sobrenome || ''}`.trim() : 'Desconhecido',
    matricula: solicitante.matricula || '',
    email: solicitante.email || '',
    curso: solicitante.curso || '',
    tipo: solicitante.acesso || '',
    solicitadoEm: apiPedido.data_solicitacao ? formatDate(apiPedido.data_solicitacao) : '',
    aprovadoEm: apiPedido.data_aprovacao ? formatDate(apiPedido.data_aprovacao) : null,
    finalizadoEm: apiPedido.data_finalizado ? formatDate(apiPedido.data_finalizado) : null,
    dataAtualizacao: apiPedido.data_atualizacao ? formatDate(apiPedido.data_atualizacao) : (apiPedido.data_solicitacao ? formatDate(apiPedido.data_solicitacao) : ''),
    status,
    statusColor,
    observacoes: apiPedido.feedback || '',
    items: (apiPedido.itens || []).map((i: any) => ({
      id: i.id,
      estoqueId: i.estoque_id,
      name: i.estoque?.nome || 'Item Desconhecido',
      quantity: i.quantidade_item,
      available: (i.estoque?.quantidade || 0) + i.quantidade_item
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
