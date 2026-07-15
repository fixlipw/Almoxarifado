export type TipoEstoque = 'EQUIPAMENTO' | 'COMPONENTE' | string
export type RoleAcesso = 'ALUNO' | 'BOLSISTA' | 'ADMIN' | string

export interface PageResponse<T> {
    content: T[]
    empty: boolean
    first: boolean
    last: boolean
    number: number
    numberOfElements: number
    pageable: {
        offset: number
        pageNumber: number
        pageSize: number
        paged: boolean
        sort: {
            empty: boolean
            sorted: boolean
            unsorted: boolean
        }
        unpaged: boolean
    }
    size: number
    sort: {
        empty: boolean
        sorted: boolean
        unsorted: boolean
    }
    totalElements: number
    totalPages: number
}

export interface EstoqueRequest {
    nome: string
    quantidade: number
    quantidadeDisponivel?: number
    tipo: TipoEstoque
    isAtivado?: boolean
}

export interface EstoqueResponse {
    id: string
    nome: string
    quantidade: number
    quantidadeDisponivel: number
    tipo: TipoEstoque
    isAtivado?: boolean
}

export interface DashboardPedidoResponse {
    id: string
    codigoPedido: string
    solicitante: string
    tiposItens: number
    data?: string
    status: 'ATRASADO' | 'PENDENTE' | 'ATIVO'
}

export interface DashboardResponse {
    gerencial: boolean
    pedidosPendentes: number
    pedidosAtivos: number
    pedidosAtrasados: number
    pedidosFinalizados: number
    itensEmprestados: number
    totalTiposEstoque: number
    itensEstoqueBaixo: number
    pedidosRecentes: DashboardPedidoResponse[]
    estoqueBaixo: EstoqueResponse[]
}

export interface HistoricoBloqueiosRequest {
    usuarioId: string
    motivoDesbloqueio?: string
    motivoBloqueio: string
    dataBloqueio?: string
    dataDesbloqueio?: string
}

export interface HistoricoBloqueiosResponse {
    id: string
    usuarioId: string
    administradorBloqueioId?: string
    administradorDesbloqueioId?: string
    motivoDesbloqueio?: string
    motivoBloqueio: string
    dataBloqueio?: string
    dataDesbloqueio?: string
}

export interface ItemPedidoRequest {
    estoqueId: string
    quantidadeItem: number
}

export interface ItemPedidoResponse {
    id: string
    pedidoId: string
    estoqueId: string
    nomeItem: string
    quantidadeItem: number
}

export interface UsuarioRequest {
    matricula?: string
    senha: string
    nome?: string
    sobrenome?: string
    curso?: string
    fotoPerfil?: string
    acesso: RoleAcesso
    isAtivada?: boolean
    isBloqueado?: boolean
}

export interface UsuarioResponse {
    id: string
    usuario?: string,
    email?: string
    matricula?: string
    nome?: string
    sobrenome?: string
    curso?: string
    fotoPerfil?: string
    acesso: RoleAcesso
    isAtivada?: boolean
    isBloqueado?: boolean
}

export interface CriarPedidoRequest {
    codigoPedido: string
    feedback?: string
    itens: ItemPedidoRequest[]
}

export interface AtualizarPedidoRequest {
    feedback?: string
    itens: ItemPedidoRequest[]
}

export type StatusPedido = 'PENDENTE' | 'APROVADO' | 'REJEITADO' | 'FINALIZADO' | 'CANCELADO'

export interface PedidoResponse {
    id: string
    codigoPedido: string
    feedback?: string
    solicitante?: UsuarioResponse
    aprovador?: UsuarioResponse
    finalizador?: UsuarioResponse
    itens: ItemPedidoResponse[]
    dataSolicitacao?: string
    dataAprovacao?: string
    dataFinalizado?: string
    dataAtualizacao?: string
    status: StatusPedido
    emprestimoEspecial?: boolean
    hash?: string
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
