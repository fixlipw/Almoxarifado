import {defineStore} from 'pinia'
import {computed, ref} from 'vue'
import {
    getToken,
    getUserId,
    getUserName,
    getUserRole,
    initAuth,
    isAuthenticated as isAuthenticatedService,
    login as loginWithKeycloak,
    logout as logoutWithKeycloak,
    register as registerWithKeycloak,
    updateToken,
} from '@/services/auth'

export const useAuthStore = defineStore('auth', () => {
    const token = ref<string | null>(getToken())
    const userId = ref<string>(getUserId())
    const userName = ref<string>(getUserName())
    const userRole = ref<string>(getUserRole())
    const authenticated = ref<boolean>(isAuthenticatedService())
    const ready = ref(false)

    const isAuthenticated = computed(() => authenticated.value)

    function syncAuthState() {
        token.value = getToken()
        userId.value = getUserId()
        userName.value = getUserName()
        userRole.value = getUserRole()
        authenticated.value = isAuthenticatedService()
    }

    async function init() {
        await initAuth()
        syncAuthState()
        ready.value = true
    }

    async function login(redirectUri?: string) {
        await loginWithKeycloak(redirectUri)
    }

    async function register(redirectUri?: string) {
        await registerWithKeycloak(redirectUri)
    }

    async function refreshToken(minValidity = 30) {
        const refreshed = await updateToken(minValidity)
        syncAuthState()
        return refreshed
    }

    async function logout(redirectUri?: string) {
        await logoutWithKeycloak(redirectUri)
        authenticated.value = false
        token.value = null
        userId.value = ''
        userName.value = ''
        userRole.value = ''
    }

    return {
        ready,
        token,
        userId,
        userName,
        userRole,
        isAuthenticated,
        syncAuthState,
        init,
        login,
        register,
        refreshToken,
        logout
    }
})
