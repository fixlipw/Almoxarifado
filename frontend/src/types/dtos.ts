export type TipoEstoque = 'EQUIPAMENTO' | 'COMPONENTE' | string
export type RoleAcesso = 'ALUNO' | 'BOLSISTA' | 'ADMIN' | string

export interface EstoqueRequest {
  nome: string
  quantidade: number
  tipo: TipoEstoque
  isAtivado?: boolean | null
}

export interface EstoqueResponse {
  id: string
  nome: string
  quantidade: number
  tipo: TipoEstoque
  isAtivado?: boolean | null
}

export interface HistoricoBloqueiosRequest {
  usuarioId: string
  motivoDesbloqueio?: string | null
  motivoBloqueio: string
  dataBloqueio?: string | null
  dataDesbloqueio?: string | null
}

export interface HistoricoBloqueiosResponse {
  id: string
  usuarioId: string
  administradorBloqueioId?: string | null
  administradorDesbloqueioId?: string | null
  motivoDesbloqueio?: string | null
  motivoBloqueio: string
  dataBloqueio?: string | null
  dataDesbloqueio?: string | null
}

export interface ItemPedidoRequest {
  estoqueId: string
  quantidadeItem: number
}

export interface ItemPedidoResponse {
  id: string
  pedidoId: string
  estoqueId: string
  quantidadeItem: number
}

export interface UsuarioRequest {
  matricula?: string | null
  senha?: string | null
  nome?: string | null
  sobrenome?: string | null
  curso?: string | null
  fotoPerfil?: string | null
  acesso?: RoleAcesso | null
  isAtivada?: boolean | null
  isBloqueado?: boolean | null
}

export interface UsuarioResponse {
  id: string
  usuario?: string | null
  matricula?: string | null
  nome?: string | null
  sobrenome?: string | null
  curso?: string | null
  fotoPerfil?: string | null
  acesso: RoleAcesso
  isAtivada?: boolean | null
  isBloqueado?: boolean | null
}

export interface PedidoRequest {
  codigoPedido: string
  feedback?: string | null
  itens?: ItemPedidoRequest[] | null
  dataSolicitacao?: string | null
  dataAprovacao?: string | null
  dataFinalizado?: string | null
  dataAtualizacao?: string | null
  aprovado?: boolean | null
  finalizado?: boolean | null
  emprestimoEspecial?: boolean | null
  hash?: string | null
}

export interface PedidoResponse {
  id: string
  codigoPedido: string
  feedback?: string | null
  solicitante?: UsuarioResponse | null
  aprovador?: UsuarioResponse | null
  finalizador?: UsuarioResponse | null
  itens?: ItemPedidoResponse[] | null
  dataSolicitacao?: string | null
  dataAprovacao?: string | null
  dataFinalizado?: string | null
  dataAtualizacao?: string | null
  aprovado?: boolean | null
  finalizado?: boolean | null
  emprestimoEspecial?: boolean | null
  hash?: string | null
}

export interface LoginRequest {
  login: string
  senha: string
}

export interface LoginResponse {
  token: string
  usuario: UsuarioResponse
}

export interface RegisterRequest {
  login: string
  email: string
  senha: string
  matricula: string
  nome: string
  sobrenome?: string | null
  curso?: string | null
  fotoPerfil?: string | null
}
