import type { AuthSession } from '@/types/auth'
import { apiFetch } from '@/services/api'
import type { LoginRequest, LoginResponse, RegisterRequest } from '@/types/dtos'

const SESSION_KEY = 'almoxarifado-auth-session'

export function saveAuthSession(session: AuthSession): void {
  localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function loadAuthSession(): AuthSession | null {
  const raw = localStorage.getItem(SESSION_KEY)

  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as AuthSession
  } catch {
    return null
  }
}

export function clearAuthSession(): void {
  localStorage.removeItem(SESSION_KEY)
}

export async function loginApi(payload: LoginRequest): Promise<LoginResponse> {
  return await apiFetch<LoginResponse>('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}

export async function registerApi(payload: RegisterRequest): Promise<void> {
  await apiFetch<void>('/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
}
