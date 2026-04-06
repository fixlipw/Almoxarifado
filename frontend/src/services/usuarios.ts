import { supabase } from '@/plugins/supabase'
import type { Usuario, UUID } from '@/types/entities'

export async function getUsuarios (): Promise<Usuario[]> {
  const { data, error } = await supabase.from('usuarios').select('*')
  if (error) throw error
  return data as Usuario[]
}

export async function getUsuarioById (id: UUID): Promise<Usuario | null> {
  const { data, error } = await supabase.from('usuarios').select('*').eq('id', id).maybeSingle()
  if (error) throw error
  return data as Usuario | null
}

export async function getUsuarioByUsername (username: string): Promise<Usuario | null> {
  const { data, error } = await supabase.from('usuarios').select('*').eq('usuario', username).maybeSingle()
  if (error) throw error
  return data as Usuario | null
}

export async function getUsuarioByLoginOrEmail (loginOrEmail: string): Promise<Usuario | null> {
  const valorInformado = loginOrEmail.trim()

  if (!valorInformado) {
    return null
  }

  const usuarioPorLogin = await getUsuarioByUsername(valorInformado)
  if (usuarioPorLogin) {
    return usuarioPorLogin
  }

  const email = valorInformado.includes('@') ? valorInformado.toLowerCase() : valorInformado
  return getUsuarioByEmail(email)
}

export async function getUsuarioByMatricula (matricula: string): Promise<Usuario | null> {
  const { data, error } = await supabase.from('usuarios').select('*').eq('matricula', matricula).maybeSingle()
  if (error) throw error
  return data as Usuario | null
}

export async function getUsuarioByEmail (email: string): Promise<Usuario | null> {
  const { data, error } = await supabase.from('usuarios').select('*').eq('email', email).maybeSingle()
  if (error) throw error
  return data as Usuario | null
}

export async function createUsuario (payload: Partial<Usuario>): Promise<Usuario> {
  const { data, error } = await supabase.from('usuarios').insert(payload).select().single()
  if (error) throw error
  return data as Usuario
}

export async function updateUsuario (id: UUID, payload: Partial<Usuario>): Promise<Usuario> {
  const { data, error } = await supabase.from('usuarios').update(payload).eq('id', id).select().single()
  if (error) throw error
  return data as Usuario
}

export async function deleteUsuario (id: UUID): Promise<void> {
  const { error } = await supabase.from('usuarios').delete().eq('id', id)
  if (error) throw error
}
