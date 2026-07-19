import {apiFetch} from '@/services/api'
import type {UsuarioRequest, UsuarioResponse} from '@/types/dtos'

export async function getUsuarios(): Promise<UsuarioResponse[]> {
    return await apiFetch<UsuarioResponse[]>('/usuarios')
}

export async function getUsuarioById(id: string): Promise<UsuarioResponse | null> {
    return await apiFetch<UsuarioResponse>(`/usuarios/${id}`)
}

export async function getUsuarioByUsername(username: string): Promise<UsuarioResponse | null> {
    return await apiFetch<UsuarioResponse>(`/usuarios/matricula/${username}`)
}

export async function getUsuarioByLoginOrEmail(loginOrEmail: string): Promise<UsuarioResponse | null> {
    const valorInformado = loginOrEmail.trim()
    if (!valorInformado) {
        return null
    }

    const email = valorInformado.includes('@') ? valorInformado.toLowerCase() : valorInformado
    if (email.includes('@')) {
        return getUsuarioByEmail(email)
    }
    return getUsuarioByUsername(valorInformado)
}

export async function getUsuarioByMatricula(matricula: string): Promise<UsuarioResponse | null> {
    return await apiFetch<UsuarioResponse>(`/usuarios/matricula/${matricula}`)
}

export async function getUsuarioByEmail(email: string): Promise<UsuarioResponse | null> {
    return await apiFetch<UsuarioResponse>(`/usuarios/email/${email}`)
}

export async function createUsuario(payload: UsuarioRequest): Promise<UsuarioResponse> {
    return await apiFetch<UsuarioResponse>('/usuarios', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function updateUsuario(id: string, payload: UsuarioRequest): Promise<UsuarioResponse> {
    return await apiFetch<UsuarioResponse>(`/usuarios/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
    })
}

export async function deleteUsuario(id: string): Promise<void> {
    await apiFetch<void>(`/usuarios/${id}`, {method: 'DELETE'})
}

export async function updateUsuarioStatus(id: string, ativo: boolean): Promise<UsuarioResponse> {
    return await apiFetch<UsuarioResponse>(`/usuarios/${id}/status`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ativo})
    })
}
