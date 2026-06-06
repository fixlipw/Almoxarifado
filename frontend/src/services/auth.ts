import Keycloak, {
  type KeycloakConfig,
  type KeycloakLoginOptions,
  type KeycloakLogoutOptions,
  type KeycloakRegisterOptions,
  type KeycloakTokenParsed,
} from 'keycloak-js'

const env = import.meta.env as Record<string, string | undefined>

const keycloakConfig: KeycloakConfig = {
  url: String(env.VITE_KEYCLOAK_URL ?? ''),
  realm: String(env.VITE_KEYCLOAK_REALM ?? ''),
  clientId: String(env.VITE_KEYCLOAK_CLIENT_ID ?? ''),
}

const keycloak = new Keycloak(keycloakConfig)
const callbackParams = ['state', 'session_state', 'iss', 'code', 'error', 'error_description']

function getFallbackRedirectUri() {
  return String(env.VITE_KEYCLOAK_REDIRECT_URI ?? globalThis.window.location.origin)
}

function clearAuthCallbackParamsFromUrl() {
  const url = new URL(globalThis.window.location.href)
  let changed = false

  for (const param of callbackParams) {
    if (url.searchParams.has(param)) {
      url.searchParams.delete(param)
      changed = true
    }
  }

  if (!changed) {
    return
  }

  globalThis.window.history.replaceState(globalThis.window.history.state, '', `${url.pathname}${url.search}${url.hash}`)
}

function normalizeRoles(tokenParsed?: KeycloakTokenParsed) {
  const roles = [
    ...(tokenParsed?.realm_access?.roles ?? []),
    ...Object.values(tokenParsed?.resource_access ?? {}).flatMap(resource => resource?.roles ?? []),
  ]

  return roles.map(role => role.toLowerCase())
}

function resolveUserRole(tokenParsed?: KeycloakTokenParsed): string {
  const roles = normalizeRoles(tokenParsed)

  if (roles.some(role => ['admin', 'administrador', 'ADMIN'].includes(role))) {
    return 'ADMIN'
  }

  if (roles.some(role => ['bolsista', 'BOLSISTA'].includes(role))) {
    return 'BOLSISTA'
  }

  if (roles.some(role => ['aluno', 'ALUNO'].includes(role))) {
    return 'ALUNO'
  }

  return ''
}

export async function initAuth(): Promise<boolean> {
  const authenticated = await keycloak.init({
    onLoad: 'check-sso',
    checkLoginIframe: false,
    responseMode: 'query',
    pkceMethod: 'S256',
    silentCheckSsoFallback: true,
  })

  clearAuthCallbackParamsFromUrl()
  return authenticated
}

export async function getAccessToken(minValidity = 30): Promise<string | null> {
  if (!keycloak.authenticated) {
    return null
  }

  try {
    await keycloak.updateToken(minValidity)
  } catch {
    return null
  }

  return keycloak.token ?? null
}

export function getToken(): string | null {
  return keycloak.token ?? null
}

export function isAuthenticated(): boolean {
  return Boolean(keycloak.authenticated)
}

export function getUserName(): string {
  return keycloak.tokenParsed?.name
      ?? keycloak.tokenParsed?.preferred_username
      ?? keycloak.tokenParsed?.email
      ?? ''
}

export function getUserRole(): string {
  return resolveUserRole(keycloak.tokenParsed)
}

export function getUserId(): string {
  return keycloak.tokenParsed?.sub ?? ''
}

export async function login(redirectUri?: string, options?: Omit<KeycloakLoginOptions, 'redirectUri'>): Promise<void> {
  await keycloak.login({
    ...options,
    redirectUri: redirectUri ?? getFallbackRedirectUri(),
  })
}

export async function register(redirectUri?: string, options?: Omit<KeycloakRegisterOptions, 'redirectUri'>): Promise<void> {
  await keycloak.register({
    ...options,
    redirectUri: redirectUri ?? getFallbackRedirectUri(),
  })
}

export async function logout(redirectUri?: string, options?: Omit<KeycloakLogoutOptions, 'redirectUri'>): Promise<void> {
  await keycloak.logout({
    ...options,
    redirectUri: redirectUri ?? getFallbackRedirectUri(),
  })
}

export async function updateToken(minValidity = 30): Promise<boolean> {
  if (!keycloak.authenticated) {
    return false
  }

  return await keycloak.updateToken(minValidity)
}
