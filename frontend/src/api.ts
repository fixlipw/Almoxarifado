import type { EstoqueResponse, ItemPedidoResponse, PedidoResponse, UsuarioResponse } from '@/types'
import { supabase } from '@/lib/supabase'

function throwIfError (error: Error | null, message: string): void {
  if (error) {
    throw new Error(`${message}: ${error.message}`)
  }
}

export const api = {
  async getEstoque (): Promise<EstoqueResponse[]> {
    const { data, error } = await supabase
      .from('estoque')
      .select('*')
      .order('nome', { ascending: true })

    throwIfError(error, 'Falha ao obter estoque')
    return (data ?? []) as EstoqueResponse[]
  },

  async getPedidos (): Promise<PedidoResponse[]> {
    const { data, error } = await supabase
      .from('pedidos')
      .select('*')
      .order('dataSolicitacao', { ascending: false })

    throwIfError(error, 'Falha ao obter pedidos')
    return (data ?? []) as PedidoResponse[]
  },

  async getItensPedido (pedidoId?: string): Promise<ItemPedidoResponse[]> {
    let query = supabase.from('itensPedido').select('*')

    if (pedidoId) {
      query = query.eq('pedidoId', pedidoId)
    }

    const { data, error } = await query
    throwIfError(error, 'Falha ao obter itens do pedido')

    return (data ?? []) as ItemPedidoResponse[]
  },

  async deletePedido (id: string): Promise<void> {
    const { error } = await supabase
      .from('pedidos')
      .delete()
      .eq('id', id)

    throwIfError(error, 'Falha ao deletar pedido')
  },

  async deleteItemPedido (id: string): Promise<void> {
    const { error } = await supabase
      .from('itensPedido')
      .delete()
      .eq('id', id)

    throwIfError(error, 'Falha ao deletar item de pedido')
  },

  async updatePedido (id: string, data: Partial<PedidoResponse>): Promise<PedidoResponse> {
    const payload = {
      ...data,
      dataAtualizacao: new Date().toISOString(),
    }

    const { data: updatedPedido, error } = await supabase
      .from('pedidos')
      .update(payload)
      .eq('id', id)
      .select('*')
      .single()

    throwIfError(error, 'Falha ao atualizar pedido')
    return updatedPedido as PedidoResponse
  },

  async createPedido (data: Partial<PedidoResponse>): Promise<PedidoResponse> {
    const now = new Date().toISOString()

    const payload: PedidoResponse = {
      id: data.id ?? `p${Date.now()}`,
      codigoPedido: data.codigoPedido ?? `PED-${Date.now().toString().slice(-6)}`,
      feedback: data.feedback ?? '',
      solicitanteId: data.solicitanteId ?? '',
      aprovadorId: data.aprovadorId ?? null,
      finalizadorId: data.finalizadorId ?? null,
      dataSolicitacao: data.dataSolicitacao ?? now,
      dataAprovacao: data.dataAprovacao ?? null,
      dataFinalizado: data.dataFinalizado ?? null,
      dataAtualizacao: data.dataAtualizacao ?? now,
      aprovado: data.aprovado ?? null,
      finalizado: data.finalizado ?? false,
      emprestimoEspecial: data.emprestimoEspecial ?? false,
      hash: data.hash ?? crypto.randomUUID(),
    }

    const { data: createdPedido, error } = await supabase
      .from('pedidos')
      .insert(payload)
      .select('*')
      .single()

    throwIfError(error, 'Falha ao criar pedido')
    return createdPedido as PedidoResponse
  },

  async createItemPedido (data: Partial<ItemPedidoResponse>): Promise<ItemPedidoResponse> {
    const payload: ItemPedidoResponse = {
      id: data.id ?? `ip${Date.now()}_${Math.floor(Math.random() * 1000)}`,
      pedidoId: data.pedidoId ?? '',
      estoqueId: data.estoqueId ?? '',
      quantidadeItem: data.quantidadeItem ?? 1,
    }

    const { data: createdItemPedido, error } = await supabase
      .from('itensPedido')
      .insert(payload)
      .select('*')
      .single()

    throwIfError(error, 'Falha ao criar item de pedido')
    return createdItemPedido as ItemPedidoResponse
  },

  async getUsuarios (): Promise<UsuarioResponse[]> {
    const { data, error } = await supabase
      .from('usuarios')
      .select('*')
      .order('nome', { ascending: true })

    throwIfError(error, 'Falha ao obter usuários')
    return (data ?? []) as UsuarioResponse[]
  },

  async getUsuario (id: string): Promise<UsuarioResponse> {
    const { data, error } = await supabase
      .from('usuarios')
      .select('*')
      .eq('id', id)
      .single()

    throwIfError(error, 'Falha ao obter usuário')
    return data as UsuarioResponse
  },
}
