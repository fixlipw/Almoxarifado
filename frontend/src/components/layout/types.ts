export type NavSection = 'dashboard' | 'inventario' | 'emprestimos'

export interface NavItem {
  label: string
  value: NavSection
  icon: string
}
