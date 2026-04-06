export interface ItemCardProps {
  id?: string
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
  isBlocked?: boolean
}

export interface QuantityDialogProps {
  modelValue: boolean
  item?: QuantityDialogItem
}

export interface QuantityDialogItem {
  title: string
  category: string
  available: number
  icon?: string
  iconColor?: string
}

export interface EstoqueFormValues {
  nome: string
  quantidade: number
  tipo: 'COMPONENTE' | 'EQUIPAMENTO'
  ativo: boolean
}

export interface EstoqueEditDialogItem extends EstoqueFormValues {
  id: string
  is_ativado?: boolean | null
}

export interface EstoqueEditDialogProps {
  modelValue: boolean
  item?: EstoqueEditDialogItem
  isLoading?: boolean
  mode?: 'create' | 'edit'
}
