import {supabase} from '@/plugins/supabase'
import type {Pedido, UUID} from '@/types/entities'

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

export async function getPedidosPendentes (): Promise<any[]> {
  const { data, error } = await supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .is('aprovado', null)

  if (error) throw error
  return data
}

export async function getPedidosAtivos (): Promise<any[]> {
  const { data, error } = await supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .eq('aprovado', true)
    .is('finalizado', null)

  if (error) throw error
  return data
}

export async function getPedidosFinalizados (): Promise<any[]> {
  const { data, error } = await supabase
    .from('pedidos')
    .select(`
      *,
      solicitante:usuarios!solicitante_id (*),
      itens:itens_pedido!pedido_id ( *, estoque:estoque!estoque_id (*) )
    `)
    .eq('finalizado', true)

  if (error) throw error
  return data
}

export async function getPedidosAtrasados (): Promise<any[]> {
  const pedidosAtivos = await getPedidosAtivos();
  return pedidosAtivos.filter(pedido => {
    const aprovadoAtRaw = (pedido as Pedido).data_aprovacao
    if (!aprovadoAtRaw) return false
    const aprovadoAt = new Date(aprovadoAtRaw)
    const prazo = new Date(aprovadoAt)
    prazo.setHours(18, 0, 0, 0)
    return Date.now() > prazo.getTime()
  })
}