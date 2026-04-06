/**
 * src/services/api.ts
 * Minimal fetch wrapper for typed API calls. Does not add runtime dependencies.
 */

export async function apiFetch<T = unknown> (endpoint: string, init?: RequestInit): Promise<T> {
  const base = (import.meta as any).env?.VITE_API_BASE ?? ''

  const headers = new Headers(init?.headers)
  const sessionRaw = localStorage.getItem('almoxarifado-auth-session')
  if (sessionRaw) {
    try {
      const session = JSON.parse(sessionRaw)
      if (session?.usuario?.id) {
        headers.set('X-User-Id', session.usuario.id)
      }
    } catch (e) {
      // Ignore parse error
    }
  }

  const res = await fetch(base + endpoint, {
    ...init,
    headers
  })

  if (!res.ok) {
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

export async function sigaaFetch<T = unknown> (endpoint: string, init?: RequestInit): Promise<T> {
  const base = (import.meta as any).env?.VITE_SIGAA_BASE ?? ''
  const res = await fetch(base + endpoint, init)

  if (!res.ok) {
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
