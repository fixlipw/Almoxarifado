import { apiFetch } from '@/services/api'
import type { HistoricoBloqueiosRequest, HistoricoBloqueiosResponse } from '@/types/dtos'

export async function getBloqueios (): Promise<HistoricoBloqueiosResponse[]> {
  return await apiFetch<HistoricoBloqueiosResponse[]>('/historico-bloqueios')
}

export async function getBloqueiosByUserId (userId: string): Promise<HistoricoBloqueiosResponse[]> {
  return await apiFetch<HistoricoBloqueiosResponse[]>(`/historico-bloqueios?userId=${userId}`)
}

export async function createBloqueio (payload: HistoricoBloqueiosRequest): Promise<HistoricoBloqueiosResponse> {
  return await apiFetch<HistoricoBloqueiosResponse>('/historico-bloqueios', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function updateBloqueio (id: string, payload: HistoricoBloqueiosRequest): Promise<HistoricoBloqueiosResponse> {
  return await apiFetch<HistoricoBloqueiosResponse>(`/historico-bloqueios/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function deleteBloqueio (id: string): Promise<void> {
  await apiFetch<void>(`/historico-bloqueios/${id}`, { method: 'DELETE' })
}
