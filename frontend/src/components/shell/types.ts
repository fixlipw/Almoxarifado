export type NavSection = 'dashboard' | 'estoque' | 'pedidos' | 'usuarios' | 'relatorios'

export interface NavItem {
  label: string
  value: NavSection
  icon: string
}
