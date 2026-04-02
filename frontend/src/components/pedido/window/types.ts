import type {PedidoItem} from "@/components/pedido/types.ts";

export interface PedidoVisualProps {
    id: string
    items: PedidoItem[]
    nome: string
    matricula: string
    solicitadoEm: string
    aprovadoEm: string | null
    finalizadoEm?: string | null
    email: string
    curso: string
    tipo: string
    status: string
    statusColor?: string
    codigo: string
    observacoes: string
    dataAtualizacao: string
}