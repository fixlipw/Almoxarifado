import { supabase } from '@/plugins/supabase'
import type { Bloqueio, UUID } from '@/types/entities'

export async function getBloqueios (): Promise<Bloqueio[]> {
  const { data, error } = await supabase.from('bloqueios').select('*')
  if (error) throw error
  return data as Bloqueio[]
}

export async function getBloqueioById (id: UUID): Promise<Bloqueio | null> {
  const { data, error } = await supabase.from('bloqueios').select('*').eq('id', id).maybeSingle()
  if (error) throw error
  return data as Bloqueio | null
}

export async function createBloqueio (payload: Partial<Bloqueio>): Promise<Bloqueio> {
  const { data, error } = await supabase.from('bloqueios').insert(payload).select().single()
  if (error) throw error
  return data as Bloqueio
}

export async function updateBloqueio (id: UUID, payload: Partial<Bloqueio>): Promise<Bloqueio> {
  const { data, error } = await supabase.from('bloqueios').update(payload).eq('id', id).select().single()
  if (error) throw error
  return data as Bloqueio
}

export async function deleteBloqueio (id: UUID): Promise<void> {
  const { error } = await supabase.from('bloqueios').delete().eq('id', id)
  if (error) throw error
}

