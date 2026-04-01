export interface EstoqueResponse {
  id: string
  nome: string
  quantidade: number
  tipo: 'COMPONENTE' | 'EQUIPAMENTO'
  isAtivado: boolean
}

export interface UsuarioResponse {
  id: string
  email: string
  matricula: string
  nome: string
  sobrenome: string
  curso: string
  fotoPerfil: string | null
  acesso: 'ALUNO' | 'PROFESSOR' | 'ADMIN'
  isAtivada: boolean
  isBloqueado: boolean
}

export interface PedidoResponse {
  id: string
  codigoPedido: string
  feedback: string
  solicitanteId: string
  aprovadorId: string | null
  finalizadorId: string | null
  dataSolicitacao: string
  dataAprovacao: string | null
  dataFinalizado: string | null
  dataAtualizacao: string
  aprovado: boolean | null
  finalizado: boolean
  emprestimoEspecial: boolean
  hash: string
}

export interface ItemPedidoResponse {
  id: string
  pedidoId: string
  estoqueId: string
  quantidadeItem: number
}

export interface EmprestimoItem {
  id: string | number
  name: string
  quantity: number
  icon?: string
}

export interface EmprestimoDetalhes {
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

export interface EmprestimoCardProps {
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
  items?: EmprestimoItem[]
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

export interface ItemCardProps {
  title: string
  category?: string
  description?: string
  available: number
  total: number
  status?: string
  statusColor?: string
  icon?: string
  iconColor?: string
  buttonText?: string
}

export interface EmprestimoVisual {
  id: string
  items: EmprestimoItem[]
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

export interface EstoqueVisual {
  id: string
  title: string
  category: string
  available: number
  total: number
  icon: string
  iconColor: string
}

export interface QuantityDialogItem {
  title: string
  category: string
  available: number
  icon?: string
  iconColor?: string
}

export interface QuantityDialogProps {
  modelValue: boolean
  item?: QuantityDialogItem
}

// ----------------------------------------------------
// Interfaces UI
// ----------------------------------------------------

export interface TabItem {
  label: string
  value: string | number
  badge?: number
}

export interface AppSelectProps {
  modelValue: string | null
  items: Array<{ label: string, value: string }>
  itemTitle?: string
  itemValue?: string
  placeholder?: string
  clearable?: boolean
  hideDetails?: boolean
  disabled?: boolean
}

export interface AppSearchBarProps {
  modelValue: string
  placeholder?: string
  clearable?: boolean
  hideDetails?: boolean
  disabled?: boolean
}

export interface AppProgressBarProps {
  value: number
  total: number
  color?: string
  height?: number | string
  showText?: boolean
  textFormat?: (value: number, total: number) => string
  label?: string
  valueText?: string
  bgColor?: string
}

export interface AppCardProps {
  title?: string
  subtitle?: string
  color?: string
  cardClass?: string
  contentClass?: string
  headerClass?: string
  elevation?: number
  variant?: 'elevated' | 'flat' | 'tonal' | 'outlined' | 'text' | 'plain'
}

export interface AppAlertProps {
  title?: string
  description: string
  icon?: string
  tone?: 'success' | 'info' | 'warning' | 'error' | 'default'
  closable?: boolean
}

export interface CartDialogProps {
  modelValue: boolean
}

export interface ConfirmDialogProps {
  modelValue: boolean
  title?: string
  message?: string
  confirmText?: string
  cancelText?: string
  confirmColor?: string
  isLoading?: boolean
}

export interface EmprestimoDetalhesDialogProps {
  modelValue: boolean
  emprestimo: EmprestimoDetalhes
}
