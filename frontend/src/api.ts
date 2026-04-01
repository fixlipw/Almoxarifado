import type { EstoqueResponse, ItemPedidoResponse, PedidoResponse, UsuarioResponse } from '@/types'

const API_URL = 'http://localhost:3000'

export const api = {
  async getEstoque (): Promise<EstoqueResponse[]> {
    const res = await fetch(`${API_URL}/estoque`)
    if (!res.ok) {
      throw new Error('Falha ao obter estoque')
    }
    return res.json()
  },

  async getPedidos (): Promise<PedidoResponse[]> {
    const res = await fetch(`${API_URL}/pedidos`)
    if (!res.ok) {
      throw new Error('Falha ao obter pedidos')
    }
    return res.json()
  },

  async getItensPedido (pedidoId?: string): Promise<ItemPedidoResponse[]> {
    const url = pedidoId ? `${API_URL}/itensPedido?pedidoId=${pedidoId}` : `${API_URL}/itensPedido`
    const res = await fetch(url)
    if (!res.ok) {
      throw new Error('Falha ao obter itens do pedido')
    }
    return res.json()
  },

  async deletePedido (id: string): Promise<void> {
    const res = await fetch(`${API_URL}/pedidos/${id}`, { method: 'DELETE' })
    if (!res.ok) {
      throw new Error('Falha ao deletar pedido')
    }
  },

  async updatePedido (id: string, data: Partial<PedidoResponse>): Promise<PedidoResponse> {
    const res = await fetch(`${API_URL}/pedidos/${id}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
    if (!res.ok) {
      throw new Error('Falha ao atualizar pedido')
    }
    return res.json()
  },

  async createPedido (data: any): Promise<PedidoResponse> {
    data.id = `p${Date.now()}`
    data.dataSolicitacao = new Date().toISOString()
    const res = await fetch(`${API_URL}/pedidos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
    if (!res.ok) {
      throw new Error('Falha ao criar pedido')
    }
    return res.json()
  },

  async createItemPedido (data: any): Promise<ItemPedidoResponse> {
    data.id = `ip${Date.now()}_${Math.floor(Math.random() * 1000)}`
    const res = await fetch(`${API_URL}/itensPedido`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })
    if (!res.ok) {
      throw new Error('Falha ao criar item de pedido')
    }
    return res.json()
  },

  async getUsuarios (): Promise<UsuarioResponse[]> {
    const res = await fetch(`${API_URL}/usuarios`)
    if (!res.ok) {
      throw new Error('Falha ao obter usuários')
    }
    return res.json()
  },

  async getUsuario (id: string): Promise<UsuarioResponse> {
    const res = await fetch(`${API_URL}/usuarios/${id}`)
    if (!res.ok) {
      throw new Error('Falha ao obter usuário')
    }
    return res.json()
  },
}
