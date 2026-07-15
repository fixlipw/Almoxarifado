import {apiFetch} from '@/services/api'
import type {EstoqueRequest, EstoqueResponse, PageResponse} from '@/types/dtos'

export async function getEstoques(params?: {
    page?: number;
    size?: number;
    search?: string;
}): Promise<PageResponse<EstoqueResponse>> {
    const queryParams = new URLSearchParams()

    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())
    if (params?.search) queryParams.append('search', params.search)

    const queryString = queryParams.toString()
    const url = `/estoques${queryString ? `?${queryString}` : ''}`

    return await apiFetch<PageResponse<EstoqueResponse>>(url)
}

export async function getEstoqueById(id: string): Promise<EstoqueResponse | null> {
    return await apiFetch<EstoqueResponse>(`/estoques/${id}`)
}

export async function createEstoque(payload: EstoqueRequest): Promise<EstoqueResponse> {
    return await apiFetch<EstoqueResponse>('/estoques', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function updateEstoque(id: string, payload: EstoqueRequest): Promise<EstoqueResponse> {
    return await apiFetch<EstoqueResponse>(`/estoques/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function deleteEstoque(id: string): Promise<void> {
    await apiFetch<void>(`/estoques/${id}`, {method: 'DELETE'})
}
