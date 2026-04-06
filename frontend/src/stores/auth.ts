import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AuthSession } from '@/types/auth'
import { saveAuthSession, loadAuthSession, clearAuthSession } from '@/services/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
    const session = ref<AuthSession | null>(loadAuthSession())

    const isAuthenticated = computed(() => session.value !== null)
    const userRole = computed(() => session.value?.usuario?.acesso || '')
    const userName = computed(() => {
        if (session.value?.sigaa?.nome) {
            return session.value.sigaa.nome.split(' ')[0]
        }
        return session.value?.usuario?.nome?.split(' ')[0] || ''
    })

    function login(newSession: AuthSession) {
        session.value = newSession
        saveAuthSession(newSession)
    }

    function logout(redirect = true) {
        session.value = null
        clearAuthSession()
        if (redirect) {
            router.push('/auth/login')
        }
    }

    return {
        session,
        isAuthenticated,
        userRole,
        userName,
        login,
        logout
    }
})
