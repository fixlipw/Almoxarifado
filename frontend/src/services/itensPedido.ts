import { supabase } from '@/plugins/supabase'
import type { ItensPedido, UUID } from '@/types/entities'

export async function getItensPedido (): Promise<ItensPedido[]> {
  const { data, error } = await supabase.from('itens_pedido').select('*')
  if (error) throw error
  return data as ItensPedido[]
}

export async function getItemPedidoById (id: UUID): Promise<ItensPedido | null> {
  const { data, error } = await supabase.from('itens_pedido').select('*').eq('id', id).maybeSingle()
  if (error) throw error
  return data as ItensPedido | null
}

export async function createItemPedido (payload: Partial<ItensPedido>): Promise<ItensPedido> {
  const { data, error } = await supabase.from('itens_pedido').insert(payload).select().single()
  if (error) throw error
  return data as ItensPedido
}

export async function updateItemPedido (id: UUID, payload: Partial<ItensPedido>): Promise<ItensPedido> {
  const { data, error } = await supabase.from('itens_pedido').update(payload).eq('id', id).select().single()
  if (error) throw error
  return data as ItensPedido
}

export async function deleteItemPedido (id: UUID): Promise<void> {
  const { error } = await supabase.from('itens_pedido').delete().eq('id', id)
  if (error) throw error
}


