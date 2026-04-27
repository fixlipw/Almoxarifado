<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import InstitutionFooter from '@/components/common/InstitutionFooter.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import { useThemePreference } from '@/composables/useThemePreference'
import { loadAuthSession } from '@/services/auth'
import { loginApi } from '@/services/auth'
import { useNotificationStore } from '@/stores/notifications'
import { useAuthStore } from '@/stores/auth'
import type { SigaaCredentials } from '@/types/auth'
import type { LoginRequest } from '@/types/dtos'
import { getLocalISOString } from '@/utils'
import {useDisplay} from "vuetify";

const router = useRouter()
const route = useRoute()
const notifications = useNotificationStore()
const authStore = useAuthStore()
const { currentTheme, initializeTheme } = useThemePreference()

const { smAndUp } = useDisplay()

const formRef = ref()
const isLoading = ref(false)
const showPassword = ref(false)
const credentials = ref<SigaaCredentials>({
  login: '',
  senha: '',
})

const heroTheme = computed(() => (
  currentTheme.value === 'dark'
    ? {
        shellClass: 'auth-shell auth-shell--dark',
        cardClass: 'auth-card auth-card--dark',
        titleClass: 'auth-title auth-title--dark',
        subtitleClass: 'auth-subtitle auth-subtitle--dark',
        pillClass: 'auth-pill auth-pill--dark',
        featureClass: 'auth-feature auth-feature--dark',
      }
    : {
        shellClass: 'auth-shell auth-shell--light',
        cardClass: 'auth-card auth-card--light',
        titleClass: 'auth-title auth-title--light',
        subtitleClass: 'auth-subtitle auth-subtitle--light',
        pillClass: 'auth-pill auth-pill--light',
        featureClass: 'auth-feature auth-feature--light',
      }
))

const isCadastroConcluido = computed(() => route.query.cadastro === 'cadastro_ok')

const loginRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe seu usuário ou email.',
]

const passwordRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe sua senha.',
]

function applyQueryPrefill() {
  if (typeof route.query.login === 'string' && !credentials.value.login) {
    credentials.value.login = route.query.login
  }
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Não foi possível realizar o acesso.'
}

async function handleLogin() {
  const validation = await formRef.value?.validate?.()

  if (validation && !validation.valid) {
    return
  }

  isLoading.value = true

  try {
    const login = credentials.value.login.trim()
    const senha = credentials.value.senha
    const payload: LoginRequest = { login, senha }
    const response = await loginApi(payload)
    authStore.login({
      usuario: response.usuario,
      token: response.token,
      authenticatedAt: getLocalISOString(),
    })
    notifications.success(`Bem-vindo, ${response.usuario.nome || response.usuario.usuario}!`)
    await router.push('/dashboard')
  } catch (error) {
    notifications.error(getErrorMessage(error))
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  initializeTheme()
  applyQueryPrefill()

  const session = loadAuthSession()
  if (session) {
    void router.replace('/dashboard')
    return
  }

  if (isCadastroConcluido.value) {
    notifications.success('Cadastro concluído com sucesso. Faça o login para continuar.')
  }
})
</script>

<template>
  <v-sheet :class="heroTheme.shellClass" class="auth-page position-relative d-flex flex-column">
    <v-toolbar
      aria-label="Cabeçalho da página inicial com informações institucionais e acesso ao sistema"
      class="position-relative pt-4"
      color="transparent"
      flat
      role="banner"
      style="z-index: 1; overflow: visible;"
    >
      <v-container class="d-flex justify-space-between align-center py-0">
        <div class="d-flex align-center">
          <v-img
            alt="Brasão da Universidade Federal do Ceará"
            class="mr-4"
            contain
            height="46"
            src="/brasao.png"
            width="40"
          />
          <div v-if="smAndUp" class="d-flex flex-column">
            <span class="text-title-medium text-sm-h6 font-weight-bold text-white lh-1">Almoxarifado UFC</span>
            <span class="text-body-small text-sm-body-2 text-grey-lighten-1">Campus Quixadá</span>
          </div>
        </div>

        <AppButton :loading="isLoading" color="warning" variant="flat" to="/">
          Página Inicial
        </AppButton>
      </v-container>
    </v-toolbar>

    <v-container class="flex-grow-1 d-flex align-center py-10 position-relative" style="z-index: 1;">
      <v-row align="center" class="w-100" justify="space-between">
        <v-col class="text-center text-lg-left mb-lg-0">
          <AppCard :cardClass="heroTheme.cardClass" contentClass="pa-6 pa-md-8">
            <template #header>
              <div>
                <div class="text-overline text-warning font-weight-bold mb-2">Entrar</div>
                <div class="text-headline-small font-weight-bold">Acesse sua conta</div>
                <div class="text-body-medium text-medium-emphasis">
                  Entre com seu usuário ou email e a senha cadastrada no sistema.
                </div>
              </div>
            </template>

            <AppAlert
              v-if="isCadastroConcluido"
              class="mt-5 mb-5"
              description="Agora você pode entrar com seu usuário ou email e a senha cadastrada."
              title="Cadastro concluído"
              tone="success"
            />

            <v-form ref="formRef" class="mt-4" @submit.prevent="handleLogin">
              <v-text-field
                v-model="credentials.login"
                :rules="loginRules"
                autocomplete="username"
                class="mb-4"
                density="comfortable"
                label="Usuário ou email"
                placeholder="Informe seu usuário ou email"
                prepend-inner-icon="mdi-account-outline"
                variant="outlined"
              />

              <v-text-field
                v-model="credentials.senha"
                :append-inner-icon="showPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                :rules="passwordRules"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                class="mb-3"
                density="comfortable"
                label="Senha"
                placeholder="Digite sua senha"
                prepend-inner-icon="mdi-lock-outline"
                variant="outlined"
                @click:append-inner="showPassword = !showPassword"
              />

              <div class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center ga-3 mt-2">
                <v-btn
                  class="text-none px-0"
                  color="warning"
                  size="small"
                  to="/auth/registro"
                  variant="text"
                >
                  Primeiro acesso? Registre-se
                </v-btn>

                <AppButton :loading="isLoading" color="warning" type="submit" variant="flat">
                  Entrar
                </AppButton>
              </div>
            </v-form>
          </AppCard>
        </v-col>
      </v-row>
    </v-container>

    <InstitutionFooter :theme="currentTheme" class="position-relative mt-auto" mode="hero" style="z-index: 1;"/>
  </v-sheet>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
}

.auth-page::after {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(circle at 12% 18%, rgba(245, 158, 11, 0.22), transparent 26%),
  radial-gradient(circle at 88% 15%, rgba(14, 165, 233, 0.18), transparent 30%);
}

.auth-shell--dark {
  background-image: linear-gradient(135deg, rgba(2, 6, 23, 0.92), rgba(15, 23, 42, 0.78)), url('/ufc_panorama.jpeg');
}

.auth-shell--light {
  background-image: linear-gradient(135deg, rgba(248, 250, 252, 0.94), rgba(226, 232, 240, 0.78)), url('/ufc_panorama.jpeg');
}

.auth-card {
  backdrop-filter: blur(14px);
  border-width: 1px;
}

.auth-card--dark {
  background: rgba(15, 23, 42, 0.24);
  border-color: rgba(255, 255, 255, 0.14);
}

.auth-card--light {
  background: rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.52);
  box-shadow: 0 20px 60px rgba(15, 23, 42, 0.12);
}

.auth-title {
  font-size: clamp(1.25rem, 3.5vw, 1.75rem);
  font-weight: 800;
  line-height: 1.3;
}

.auth-title--dark {
  color: #ffffff;
}

.auth-title--light {
  color: #0f172a;
}

.auth-subtitle {
  font-size: clamp(0.9rem, 1.25vw, 1rem);
  line-height: 1.6;
}

.auth-subtitle--dark {
  color: rgba(226, 232, 240, 0.94);
}

.auth-subtitle--light {
  color: rgba(15, 23, 42, 0.84);
}

.auth-pill {
  font-weight: 700;
  letter-spacing: 0.02em;
}

.auth-pill--dark {
  background: rgba(15, 23, 42, 0.45);
  color: #f8fafc;
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.auth-pill--light {
  background: rgba(255, 255, 255, 0.62);
  color: #0f172a;
  border: 1px solid rgba(255, 255, 255, 0.72);
}

.auth-feature {
  height: 100%;
  backdrop-filter: blur(12px);
  border-radius: 20px;
}

.auth-feature--dark {
  background: rgba(15, 23, 42, 0.28);
  color: rgba(248, 250, 252, 0.96);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.auth-feature--light {
  background: rgba(255, 255, 255, 0.44);
  color: rgba(15, 23, 42, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.65);
}
</style>

<route lang="yaml">
meta:
  layout: blank
</route>
