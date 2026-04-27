import {apiFetch} from '@/services/api'
import type {PedidoRequest, PedidoResponse} from '@/types/dtos'

export async function getPedidos(): Promise<PedidoResponse[]> {
    return await apiFetch<PedidoResponse[]>('/pedidos')
}

export async function getPedidoById(id: string): Promise<PedidoResponse | null> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}`)
}

export async function createPedido(payload: PedidoRequest): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>('/pedidos', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function updatePedido(id: string, payload: PedidoRequest): Promise<PedidoResponse> {
    return await apiFetch<PedidoResponse>(`/pedidos/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
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
    const queryParams = new URLSearchParams()
    if (params?.userId) queryParams.append('userId', params.userId)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const queryString = queryParams.toString()
    const url = `/pedidos/pending${queryString ? `?${queryString}` : ''}`

    return await apiFetch<PedidoResponse[]>(url)
}

export async function getPedidosAtivos(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    const queryParams = new URLSearchParams()
    if (params?.userId) queryParams.append('userId', params.userId)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const queryString = queryParams.toString()
    const url = `/pedidos/active${queryString ? `?${queryString}` : ''}`

    return await apiFetch<PedidoResponse[]>(url)
}

export async function getPedidosFinalizados(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    const queryParams = new URLSearchParams()
    if (params?.userId) queryParams.append('userId', params.userId)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const queryString = queryParams.toString()
    const url = `/pedidos/returned${queryString ? `?${queryString}` : ''}`

    return await apiFetch<PedidoResponse[]>(url)
}

export async function getPedidosAtrasados(params?: {
    userId?: string;
    page?: number;
    size?: number;
}): Promise<PedidoResponse[]> {
    const queryParams = new URLSearchParams()
    if (params?.userId) queryParams.append('userId', params.userId)
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())

    const queryString = queryParams.toString()
    const url = `/pedidos/delayed${queryString ? `?${queryString}` : ''}`

    return await apiFetch<PedidoResponse[]>(url)
}