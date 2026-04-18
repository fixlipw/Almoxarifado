import { apiFetch } from '@/services/api'
import type { EstoqueRequest, EstoqueResponse } from '@/types/dtos'

export async function getEstoques (): Promise<EstoqueResponse[]> {
  return await apiFetch<EstoqueResponse[]>('/estoques')
}

export async function getEstoqueById (id: string): Promise<EstoqueResponse | null> {
  return await apiFetch<EstoqueResponse>(`/estoques/${id}`)
}

export async function createEstoque (payload: EstoqueRequest): Promise<EstoqueResponse> {
  return await apiFetch<EstoqueResponse>('/estoques', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function updateEstoque (id: string, payload: EstoqueRequest): Promise<EstoqueResponse> {
  return await apiFetch<EstoqueResponse>(`/estoques/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function deleteEstoque (id: string): Promise<void> {
  await apiFetch<void>(`/estoques/${id}`, { method: 'DELETE' })
}
