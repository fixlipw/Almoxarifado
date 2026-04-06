
export type UUID = string

export type UserRole = 'ALUNO' | 'BOLSISTA' | 'ADMIN'

export interface Usuario {
  id: UUID
  is_ativada?: boolean | null
  is_bloqueado?: boolean | null
  acesso: UserRole
  curso?: string | null
  email: string
  foto_perfil?: string | null
  matricula?: string | null
  nome?: string | null
  senha: string
  sobrenome?: string | null
  usuario: string
}

export interface Pedido {
  id: UUID
  aprovado?: boolean | null
  emprestimo_especial?: boolean | null
  finalizado?: boolean | null
  data_aprovacao?: string | null
  data_atualizacao?: string | null
  data_finalizado?: string | null
  data_solicitacao: string
  aprovador_id?: UUID | null
  finalizador_id?: UUID | null
  solicitante_id: UUID
  codigo_pedido: string
  feedback?: string | null
  hash?: string | null
}

export interface ItensPedido {
  id: UUID
  quantidade_item: number
  estoque_id: UUID
  pedido_id: UUID
}

export type EstoqueTipo = 'EQUIPAMENTO' | 'COMPONENTE'

export interface Estoque {
  id: UUID
  is_ativado?: boolean | null
  quantidade: number
  nome: string
  tipo: EstoqueTipo
}

export interface Bloqueio {
  id: UUID
  data_bloqueio: string
  data_desbloqueio?: string | null
  admin_block_id: UUID
  admin_unblock_id?: UUID | null
  usuario_id: UUID
  motivo_bloqueio: string
  motivo_desbloqueio?: string | null
}

