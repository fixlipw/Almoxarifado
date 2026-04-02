import { supabase } from '@/plugins/supabase'
import type { Estoque, UUID } from '@/types/entities'

export async function getEstoques (): Promise<Estoque[]> {
  const { data, error } = await supabase.from('estoque').select('*')
  if (error) throw error
  return data as Estoque[]
}

export async function getEstoqueById (id: UUID): Promise<Estoque | null> {
  const { data, error } = await supabase.from('estoque').select('*').eq('id', id).maybeSingle()
  if (error) throw error
  return data as Estoque | null
}

export async function createEstoque (payload: Partial<Estoque>): Promise<Estoque> {
  const { data, error } = await supabase.from('estoque').insert(payload).select().single()
  if (error) throw error
  return data as Estoque
}

export async function updateEstoque (id: UUID, payload: Partial<Estoque>): Promise<Estoque> {
  const { data, error } = await supabase.from('estoque').update(payload).eq('id', id).select().single()
  if (error) throw error
  return data as Estoque
}

export async function deleteEstoque (id: UUID): Promise<void> {
  const { error } = await supabase.from('estoque').delete().eq('id', id)
  if (error) throw error
}

