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

export interface AppLoadSpinnerProps {
  label?: string
  color?: string
  size?: number | string
  width?: number | string
  minHeight?: number | string
}

export interface AppAlertProps {
  title?: string
  description?: string
  icon?: string
  tone?: 'success' | 'info' | 'warning' | 'error' | 'default'
  closable?: boolean
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
  loading?: boolean
}

export interface TabItem {
  label: string
  value: string | number
  badge?: number
}
