<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'

import InstitutionFooter from '@/components/common/InstitutionFooter.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import ParticleCanvas from '@/components/ui/ParticleCanvas.vue'
import {useThemePreference} from '@/composables/useThemePreference'
import {useNotificationStore} from '@/stores/notifications'
import {useAuthStore} from '@/stores/auth'
import {useDisplay} from 'vuetify'

const router = useRouter()
const route = useRoute()
const notifications = useNotificationStore()
const authStore = useAuthStore()
const {currentTheme, initializeTheme, toggleTheme} = useThemePreference()

const { smAndUp } = useDisplay()

const isLoading = ref(false)

const heroTheme = computed(() => (
  currentTheme.value === 'dark'
    ? {
        shellClass: 'auth-shell auth-shell--dark',
        cardClass: 'auth-card auth-card--dark',
        titleClass: 'auth-title auth-title--dark',
        subtitleClass: 'auth-subtitle auth-subtitle--dark',
        pillClass: 'auth-pill auth-pill--dark',
        featureClass: 'auth-feature auth-feature--dark',
        particleFill: 'rgba(255, 255, 255, 0.45)',
        particleStroke: '255, 255, 255',
        brandClass: 'text-white',
        themeButtonClass: 'text-white',
        themeIcon: 'mdi-weather-night',
      }
    : {
        shellClass: 'auth-shell auth-shell--light',
        cardClass: 'auth-card auth-card--light',
        titleClass: 'auth-title auth-title--light',
        subtitleClass: 'auth-subtitle auth-subtitle--light',
        pillClass: 'auth-pill auth-pill--light',
        featureClass: 'auth-feature auth-feature--light',
        particleFill: 'rgba(15, 23, 42, 0.28)',
        particleStroke: '15, 23, 42',
        brandClass: 'text-black',
        themeButtonClass: 'text-black',
        themeIcon: 'mdi-weather-sunny',
      }
))

const isCadastroConcluido = computed(() => route.query.cadastro === 'ok' || route.query.cadastro === 'cadastro_ok')

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Não foi possível realizar o acesso.'
}

async function handleLogin() {
  isLoading.value = true

  try {
    await authStore.login(`${globalThis.window.location.origin}/dashboard`)
  } catch (error) {
    notifications.error(getErrorMessage(error))
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  initializeTheme()
  if (authStore.isAuthenticated) {
    router.replace('/dashboard')
    return
  }

  if (isCadastroConcluido.value) {
    notifications.success('Cadastro concluído com sucesso. Entre com sua conta institucional para continuar.')
  }
})
</script>

<template>
  <v-sheet :class="heroTheme.shellClass" class="auth-page position-relative d-flex flex-column">
    <ParticleCanvas
        :particle-fill="heroTheme.particleFill"
        :particle-stroke="heroTheme.particleStroke"
    />

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
            <span :class="heroTheme.brandClass" class="text-title-medium text-sm-h6 font-weight-bold lh-1">Almoxarifado UFC</span>
            <span :class="currentTheme === 'dark' ? 'text-grey-lighten-1' : 'text-grey'"
                  class="text-body-small text-sm-body-2">Campus Quixadá</span>
          </div>
        </div>

        <v-spacer/>

        <v-btn
            :aria-label="currentTheme === 'dark' ? 'Mudar para tema claro' : 'Mudar para tema escuro'"
            :class="heroTheme.themeButtonClass + 'mr-4'"
            :icon="heroTheme.themeIcon"
            density="comfortable"
            variant="text"
            @click="toggleTheme"
        />

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
              description="Seu cadastro foi concluído. Agora basta entrar com a conta institucional."
              title="Cadastro concluído"
              tone="success"
            />

            <div class="mt-4 d-flex flex-column ga-4">
              <AppAlert
                  description="Ao continuar, você será redirecionado para a tela de login."
                  title="Login institucional"
                  tone="info"
              />

              <div class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center ga-3">
                <v-btn
                  class="text-none px-0"
                  color="warning"
                  size="small"
                  to="/auth/registro"
                  variant="text"
                >
                  Primeiro acesso? Registre-se
                </v-btn>

                <AppButton :loading="isLoading" color="warning" variant="flat" @click="handleLogin">
                  Entrar
                </AppButton>
              </div>
            </div>
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
