import {getAccessToken} from '@/services/auth'

export interface ApiErrorBody {
    timestamp?: string
    status?: number
    error?: string
    message?: string
    details?: string[]
    path?: string
}

export class ApiError extends Error {
    constructor(
        public readonly status: number,
        message: string,
        public readonly body?: ApiErrorBody,
    ) {
        super(message)
        this.name = 'ApiError'
    }
}

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

function notifyApiError(error: ApiError | TypeError): void {
    globalThis.dispatchEvent(new CustomEvent('app:api-error', {detail: error}))
}

async function createApiError(res: Response): Promise<ApiError> {
    const contentType = res.headers.get('content-type') || ''
    const body = contentType.includes('application/json')
        ? await res.json().catch(() => undefined) as ApiErrorBody | undefined
        : undefined

    return new ApiError(res.status, body?.message || `A requisição falhou com status ${res.status}.`, body)
}

async function request(endpoint: string, init?: RequestInit): Promise<Response> {
    const base = (import.meta as any).env?.VITE_API_BASE ?? ''
    const headers = await buildAuthHeaders(init)

    try {
        const response = await fetch(base + endpoint, {...init, headers})
        if (response.ok) return response

        if (response.status === 401 || response.status === 403) invalidateAccessTokenCache()

        const error = await createApiError(response)
        notifyApiError(error)
        throw error
    } catch (error) {
        if (error instanceof ApiError) throw error
        if (error instanceof TypeError) notifyApiError(error)
        throw error
    }
}

export async function apiFetch<T = unknown>(endpoint: string, init?: RequestInit): Promise<T> {
    const res = await request(endpoint, init)

    const contentType = res.headers.get('content-type') || ''
    if (contentType.includes('application/json')) {
        return await res.json() as Promise<T>
    }

    // @ts-ignore
    return (await res.text()) as T
}

export async function apiFetchBlob(endpoint: string, init?: RequestInit): Promise<Blob> {
    const res = await request(endpoint, init)
    return await res.blob()
}
