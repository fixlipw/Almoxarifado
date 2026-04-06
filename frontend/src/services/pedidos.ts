import {supabase} from '@/plugins/supabase'
import type {Pedido, UUID} from '@/types/entities'
import {getLocalISOString} from "@/utils";

export async function getPedidos (): Promise<Pedido[]> {
  const { data, error } = await supabase.from('pedidos').select('*')
  if (error) throw error
  return data as Pedido[]
}

export async function getPedidoById (id: UUID): Promise<Pedido | null> {
  const { data, error } = await supabase.from('pedidos').select('*').eq('id', id).maybeSingle()
  if (error) throw error
  return data as Pedido | null
}

export async function createPedido (payload: Partial<Pedido>): Promise<Pedido> {
  const { data, error } = await supabase.from('pedidos').insert(payload).select().single()
  if (error) throw error
  return data as Pedido
}

export async function updatePedido (id: UUID, payload: Partial<Pedido>): Promise<Pedido> {
  const { data, error } = await supabase.from('pedidos').update(payload).eq('id', id).select().single()
  if (error) throw error
  return data as Pedido
}

export async function deletePedido (id: UUID): Promise<void> {
  await supabase.from('itens_pedido').delete().eq('pedido_id', id)
  const { error } = await supabase.from('pedidos').delete().eq('id', id)
  if (error) throw error
}

export async function approvePedido (id: UUID, aprovadorId: string): Promise<Pedido> {
  const { data, error } = await supabase
    .from('pedidos')
    .update({
      aprovado: true,
      data_aprovacao: getLocalISOString,
      aprovador_id: aprovadorId
    })
    .eq('id', id)
    .select()
    .single()
  if (error) throw error
  return data as Pedido
}

export async function rejectPedido (id: UUID, aprovadorId: string): Promise<Pedido> {
  const { data, error } = await supabase
    .from('pedidos')
    .update({
      aprovado: false,
      data_aprovacao: getLocalISOString,
      aprovador_id: aprovadorId
    })
    .eq('id', id)
    .select()
    .single()
  if (error) throw error
  return data as Pedido
}

export async function returnPedido (id: UUID, finalizadorId: string): Promise<Pedido> {
  const { data, error } = await supabase
    .from('pedidos')
    .update({
      finalizado: true,
      data_finalizado: getLocalISOString,
      finalizador_id: finalizadorId
    })
    .eq('id', id)
    .select()
    .single()
  if (error) throw error
  return data as Pedido
}

export async function getPedidosPendentes (userId?: string): Promise<any[]> {
  let query = supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .is('aprovado', null)

  if (userId) {
    query = query.eq('solicitante_id', userId)
  }

  const { data, error } = await query

  if (error) throw error
  return data
}

export async function getPedidosAtivos (userId?: string): Promise<any[]> {
  let query = supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .eq('aprovado', true)
    .is('finalizado', null)

  if (userId) {
    query = query.eq('solicitante_id', userId)
  }

  const { data, error } = await query

  if (error) throw error
  return data
}

export async function getPedidosFinalizados (userId?: string): Promise<any[]> {
  let query = supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .eq('finalizado', true)

  if (userId) {
    query = query.eq('solicitante_id', userId)
  }

  const { data, error } = await query

  if (error) throw error
  return data
}

export async function getPedidosAtrasados (userId?: string): Promise<any[]> {
  const pedidosAtivos = await getPedidosAtivos(userId);
  return pedidosAtivos.filter(pedido => {
    const aprovadoAtRaw = (pedido as Pedido).data_aprovacao
    if (!aprovadoAtRaw) return false
    const aprovadoAt = new Date(aprovadoAtRaw)
    const prazo = new Date(aprovadoAt)
    prazo.setHours(18, 0, 0, 0)
    return Date.now() > prazo.getTime()
  })
}