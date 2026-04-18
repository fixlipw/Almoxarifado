import { apiFetch } from '@/services/api'
import type { PedidoRequest, PedidoResponse } from '@/types/dtos'

export async function getPedidos (): Promise<PedidoResponse[]> {
  return await apiFetch<PedidoResponse[]>('/pedidos')
}

export async function getPedidoById (id: string): Promise<PedidoResponse | null> {
  return await apiFetch<PedidoResponse>(`/pedidos/${id}`)
}

export async function createPedido (payload: PedidoRequest): Promise<PedidoResponse> {
  return await apiFetch<PedidoResponse>('/pedidos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function updatePedido (id: string, payload: PedidoRequest): Promise<PedidoResponse> {
  return await apiFetch<PedidoResponse>(`/pedidos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function deletePedido (id: string): Promise<void> {
  await apiFetch<void>(`/pedidos/${id}`, { method: 'DELETE' })
}

export async function approvePedido (id: string): Promise<PedidoResponse> {
  return await apiFetch<PedidoResponse>(`/pedidos/${id}/approve`, { method: 'PUT' })
}

export async function rejectPedido (id: string): Promise<PedidoResponse> {
  return await apiFetch<PedidoResponse>(`/pedidos/${id}/reject`, { method: 'PUT' })
}

export async function returnPedido (id: string): Promise<PedidoResponse> {
  return await apiFetch<PedidoResponse>(`/pedidos/${id}/return`, { method: 'PUT' })
}

export async function getPedidosPendentes (userId?: string): Promise<PedidoResponse[]> {
  const query = userId ? `?userId=${userId}` : ''
  return await apiFetch<PedidoResponse[]>(`/pedidos/pending${query}`)
}

export async function getPedidosAtivos (userId?: string): Promise<PedidoResponse[]> {
  const query = userId ? `?userId=${userId}` : ''
  return await apiFetch<PedidoResponse[]>(`/pedidos/active${query}`)
}

export async function getPedidosFinalizados (userId?: string): Promise<PedidoResponse[]> {
  const query = userId ? `?userId=${userId}` : ''
  return await apiFetch<PedidoResponse[]>(`/pedidos/approved${query}`)
}

export async function getPedidosAtrasados (userId?: string): Promise<PedidoResponse[]> {
  const query = userId ? `?userId=${userId}` : ''
  return await apiFetch<PedidoResponse[]>(`/pedidos/delayed${query}`)
}