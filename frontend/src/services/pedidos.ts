import {apiFetch} from '@/services/api'
import type {AtualizarPedidoRequest, CriarPedidoRequest, PageResponse, PedidoResponse} from '@/types/dtos'

type PedidoListResponse = PedidoResponse[] | PageResponse<PedidoResponse>

function normalizePedidos(response: PedidoListResponse): PedidoResponse[] {
    return Array.isArray(response) ? response : response.content
}

async function getPedidosByStatus(endpoint: string, params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    const queryParams = new URLSearchParams()
    if (params?.userId) queryParams.append('userId', params.userId)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const queryString = queryParams.toString()
    const response = await apiFetch<PedidoListResponse>(`${endpoint}${queryString ? `?${queryString}` : ''}`)
    return normalizePedidos(response)
}

export async function getPedidos(): Promise<PedidoResponse[]> {
    return await apiFetch<PedidoResponse[]>('/pedidos')
}

export async function getPedidoById(id: string): Promise<PedidoResponse | null> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}`)
}

export async function createPedido(payload: CriarPedidoRequest): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>('/pedidos', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function updatePedido(id: string, payload: AtualizarPedidoRequest): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function updateEmprestimoEspecial(id: string, emprestimoEspecial: boolean): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}/emprestimo-especial`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({emprestimoEspecial})
    })
}

export async function deletePedido(id: string): Promise<void> {
    await apiFetch<void>(`/pedidos/${id}`, {method: 'DELETE'})
}

export async function approvePedido(id: string): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}/approve`, {method: 'PUT'})
}

export async function rejectPedido(id: string): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}/reject`, {method: 'PUT'})
}

export async function returnPedido(id: string): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}/return`, {method: 'PUT'})
}

export async function getPedidosPendentes(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    return getPedidosByStatus('/pedidos/pending', params)
}

export async function getPedidosAtivos(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    return getPedidosByStatus('/pedidos/active', params)
}

export async function getPedidosFinalizados(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    return getPedidosByStatus('/pedidos/returned', params)
}

export async function getPedidosAtrasados(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    return getPedidosByStatus('/pedidos/delayed', params)
}
