export type NavSection = 'dashboard' | 'estoque' | 'pedidos'

export interface NavItem {
  label: string
  value: NavSection
  icon: string
}
