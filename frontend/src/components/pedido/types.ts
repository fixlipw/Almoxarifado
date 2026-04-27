export interface PedidoCardProps {
  title?: string
  subtitle?: string
  cardClass?: string
  contentClass?: string
  headerClass?: string
  color?: string
  status?: string
  statusColor?: string
  personIcon?: string
  codigo?: string
  items?: PedidoItem[]
  itemsLabel?: string
  requestedAt?: string
  requestedAtLabel?: string
  approvedAt?: string
  approvedAtLabel?: string
  returnedAt?: string
  returnedAtLabel?: string
  updatedAt?: string
  updatedAtLabel?: string
  notes?: string
  notesLabel?: string
  buttonText?: string
  showButton?: boolean
  buttonDisabled?: boolean
  emptyItemsText?: string
}

export interface PedidoDetalhesProps {
    codigo: string
    status: string
    solicitante: {
        nome: string
        email: string
        matricula: string
        curso: string
        tipo: string
    }
    itens: Array<{
        id: string | number
        estoqueId: string
        nomeItem?: string
        quantidadeItem: number
    }>
    dataSolicitacao: string
    dataAprovacao?: string
    dataDevolucao?: string
    dataAtualizacao?: string
    observacoes: string
}

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

export interface PedidoItem {
  id: string | number
  name: string
  quantity: number
  icon?: string
  estoqueId?: string
  available?: number
}