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