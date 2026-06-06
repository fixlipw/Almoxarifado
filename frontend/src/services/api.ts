import {getAccessToken} from '@/services/auth'

type CachedAccessToken = {
  token: string
  expiresAt: number
}

const TOKEN_SAFETY_WINDOW_MS = 30_000
const FALLBACK_CACHE_MS = 60_000

let cachedAccessToken: CachedAccessToken | null = null
let inFlightAccessTokenPromise: Promise<string | null> | null = null

function decodeJwtExp(token: string): number | null {
  const [, payload] = token.split('.')
  if (!payload) return null

  try {
    const normalized = payload.replaceAll('-', '+').replaceAll('_', '/')
    const padded = normalized.padEnd(normalized.length + (4 - (normalized.length % 4 || 4)) % 4, '=')
    const parsed = JSON.parse(globalThis.atob(padded)) as { exp?: unknown }
    return typeof parsed.exp === 'number' ? parsed.exp * 1000 : null
  } catch {
    return null
  }
}

function invalidateAccessTokenCache(): void {
  cachedAccessToken = null
}

async function getCachedAccessToken(): Promise<string | null> {
  const now = Date.now()

  if (cachedAccessToken && cachedAccessToken.expiresAt - TOKEN_SAFETY_WINDOW_MS > now) {
    return cachedAccessToken.token
  }

  if (inFlightAccessTokenPromise) {
    return inFlightAccessTokenPromise
  }

  inFlightAccessTokenPromise = getAccessToken()
      .then((token) => {
        if (!token) {
          cachedAccessToken = null
          return null
        }

        const expiresAt = decodeJwtExp(token) ?? (Date.now() + FALLBACK_CACHE_MS)
        cachedAccessToken = {token, expiresAt}
        return token
      })
      .finally(() => {
        inFlightAccessTokenPromise = null
      })

  return inFlightAccessTokenPromise
}

async function buildAuthHeaders(init?: RequestInit): Promise<Headers> {
  const headers = new Headers(init?.headers)
  const token = await getCachedAccessToken()

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  return headers
}

export async function apiFetch<T = unknown>(endpoint: string, init?: RequestInit): Promise<T> {
  const base = (import.meta as any).env?.VITE_API_BASE ?? ''

  const headers = await buildAuthHeaders(init)

  const res = await fetch(base + endpoint, {
    ...init,
    headers
  })

  if (!res.ok) {
    if (res.status === 401 || res.status === 403) {
      invalidateAccessTokenCache()
    }

    const text = await res.text().catch(() => '')
    throw new Error(`Request failed: ${res.status} ${res.statusText} ${text}`)
  }

  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return await res.json() as Promise<T>
  }

  // @ts-ignore
  return (await res.text()) as T
}

export async function apiFetchBlob(endpoint: string, init?: RequestInit): Promise<Blob> {
  const base = (import.meta as any).env?.VITE_API_BASE ?? ''

  const headers = await buildAuthHeaders(init)

  const res = await fetch(base + endpoint, {
    ...init,
    headers
  })

  if (!res.ok) {
    if (res.status === 401 || res.status === 403) {
      invalidateAccessTokenCache()
    }

    throw new Error(`Request failed: ${res.status}`)
  }

  return await res.blob()
}
