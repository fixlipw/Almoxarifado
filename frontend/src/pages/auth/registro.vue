<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import InstitutionFooter from '@/components/common/InstitutionFooter.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import { useThemePreference } from '@/composables/useThemePreference'
import { createUsuario, getUsuarioByEmail, getUsuarioByMatricula, getUsuarioByUsername } from '@/services/usuarios'
import { validarCredenciaisSigaa } from '@/services/sigaa'
import { useNotificationStore } from '@/stores/notifications'
import type { RegistroComplementarForm, SigaaAluno, SigaaCredentials } from '@/types/auth'

const router = useRouter()
const route = useRoute()
const notifications = useNotificationStore()
const { currentTheme, initializeTheme } = useThemePreference()

const sigaaFormRef = ref()
const cadastroFormRef = ref()
const isValidating = ref(false)
const isSaving = ref(false)
const currentStep = ref<1 | 2>(1)
const showSigaaPassword = ref(false)
const showLocalPassword = ref(false)
const sigaaProfile = ref<SigaaAluno | null>(null)
const sigaaCredentials = ref<SigaaCredentials>({
  login: '',
  senha: '',
})
const cadastroComplementar = ref<RegistroComplementarForm>({
  usuario: '',
  email: '',
  senha: '',
  confirmarSenha: '',
  sobrenome: '',
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

const stepLabel = computed(() => currentStep.value === 1 ? 'Etapa 1 de 2' : 'Etapa 2 de 2')
const progressValue = computed(() => currentStep.value === 1 ? 50 : 100)

const loginRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe o login SIGAA.',
]

const sigaaPasswordRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe a senha do SIGAA.',
]

const usuarioRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe um nome de usuário.',
  (value: string) => value.trim().length >= 3 || 'O nome de usuário deve ter pelo menos 3 caracteres.',
]

const emailRules = [
  (value: string) => Boolean(value?.trim()) || 'Informe um email de contato.',
  (value: string) => /\S+@\S+\.\S+/.test(value) || 'Informe um email válido.',
]

const senhaRules = [
  (value: string) => Boolean(value?.trim()) || 'Crie uma senha para o sistema.',
  (value: string) => value.length >= 6 || 'A senha deve ter pelo menos 6 caracteres.',
]

const confirmarSenhaRules = [
  (value: string) => Boolean(value?.trim()) || 'Confirme a senha.',
  (value: string) => value === cadastroComplementar.value.senha || 'As senhas não conferem.',
]

function applyQueryPrefill() {
  if (typeof route.query.login === 'string' && !sigaaCredentials.value.login) {
    sigaaCredentials.value.login = route.query.login
  }
}

function getErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : 'Não foi possível concluir o cadastro.'
}

function extrairSobrenome(nome: string) {
  const nomes = nome.trim().split(/\s+/).filter(Boolean)

  if (nomes.length <= 1) {
    return ''
  }

  return nomes.slice(1).join(' ')
}

async function validarSigaa() {
  const validation = await sigaaFormRef.value?.validate?.()

  if (validation && !validation.valid) {
    return
  }

  isValidating.value = true

  try {
    const aluno = await validarCredenciaisSigaa(sigaaCredentials.value)
    const usuarioExistente = await getUsuarioByMatricula(aluno.matricula)

    if (usuarioExistente) {
      notifications.info('Seu cadastro já está concluído. Faça o login para continuar.')
      await router.push({
        path: '/auth/login',
        query: {
          cadastro: 'ok',
          login: aluno.login,
        },
      })
      return
    }

    sigaaProfile.value = aluno
    cadastroComplementar.value.usuario = aluno.login
    cadastroComplementar.value.sobrenome = extrairSobrenome(aluno.nome)
    currentStep.value = 2
    notifications.success('Credenciais validadas. Complete os dados para finalizar o cadastro.')
  } catch (error) {
    notifications.error(getErrorMessage(error))
  } finally {
    isValidating.value = false
  }
}

async function finalizarCadastro() {
  const validation = await cadastroFormRef.value?.validate?.()

  if (validation && !validation.valid) {
    return
  }

  if (!sigaaProfile.value) {
    notifications.error('Valide o SIGAA novamente antes de finalizar o cadastro.')
    currentStep.value = 1
    return
  }

  if (cadastroComplementar.value.senha !== cadastroComplementar.value.confirmarSenha) {
    notifications.error('As senhas precisam ser iguais para concluir o cadastro.')
    return
  }

  isSaving.value = true

  try {
    const usuario = cadastroComplementar.value.usuario.trim()
    const email = cadastroComplementar.value.email.trim().toLowerCase()
    const usuarioPorLogin = await getUsuarioByUsername(usuario)
    const usuarioPorEmail = await getUsuarioByEmail(email)

    if (usuarioPorLogin) {
      notifications.addNotification('Esse nome de usuário já está cadastrado. Escolha outro.', 'warning')
      return
    }

    if (usuarioPorEmail) {
      notifications.addNotification('Esse email já está cadastrado. Use outro email ou faça login.', 'warning')
      return
    }

    await createUsuario({
      id: crypto.randomUUID(),
      acesso: 'ALUNO',
      curso: sigaaProfile.value.curso,
      email,
      foto_perfil: sigaaProfile.value.foto,
      matricula: sigaaProfile.value.matricula,
      nome: sigaaProfile.value.nome,
      senha: cadastroComplementar.value.senha,
      sobrenome: cadastroComplementar.value.sobrenome.trim() || extrairSobrenome(sigaaProfile.value.nome),
      usuario,
      is_ativada: true,
      is_bloqueado: false,
    })

    notifications.success('Cadastro concluído com sucesso. Agora faça o login.')
    await router.push({
      path: '/auth/login',
      query: {
        cadastro: 'cadastro_ok',
        login: usuario,
      },
    })
  } catch (error) {
    notifications.error(getErrorMessage(error))
  } finally {
    isSaving.value = false
  }
}

function voltarParaValidacao() {
  currentStep.value = 1
}

onMounted(() => {
  initializeTheme()
  applyQueryPrefill()
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
          <div class="d-flex flex-column">
            <span class="text-title-medium text-sm-h6 font-weight-bold text-white lh-1">Almoxarifado UFC</span>
            <span class="text-body-small text-sm-body-2 text-grey-lighten-1">Campus Quixadá</span>
          </div>
        </div>

        <v-btn
          aria-label="Acessar o Sistema do Almoxarifado"
          class="text-none font-weight-bold px-6 rounded-pill text-grey-darken-4"
          color="warning"
          elevation="4"
          size="large"
          to="/"
          variant="flat"
        >
          Página Inicial
        </v-btn>
      </v-container>
    </v-toolbar>

    <v-container class="flex-grow-1 d-flex align-center py-10 position-relative" style="z-index: 1;">
      <v-row align="center" class="w-100" justify="space-between">
        <v-col class="text-center text-lg-left mb-8 mb-lg-0" cols="12">
          <AppCard :cardClass="heroTheme.cardClass" contentClass="pa-6 pa-md-8">
            <template #header>
              <div class="d-flex flex-column ga-3 w-100">
                <div class="d-flex align-center justify-space-between flex-wrap ga-3">
                  <div>
                    <div class="text-overline text-warning font-weight-bold mb-2">Primeiro acesso</div>
                    <div class="text-headline-small font-weight-bold">
                      {{ currentStep === 1 ? 'Valide no SIGAA' : 'Complete seu cadastro' }}
                    </div>
                    <div class="text-body-medium text-medium-emphasis">
                      {{
                        currentStep === 1
                          ? 'Digite o login e a senha usados no SIGAA para recuperar seus dados acadêmicos.'
                          : 'Agora informe seu usuário, os dados de contato e a senha de acesso ao sistema.'
                      }}
                    </div>
                  </div>

                  <v-chip color="warning" variant="tonal">
                    {{ stepLabel }}
                  </v-chip>
                </div>

                <v-progress-linear :model-value="progressValue" bg-color="warning-lighten-4" color="warning" height="6" rounded/>
              </div>
            </template>

            <v-divider class="mt-5 mb-5" />

            <template v-if="currentStep === 1">
              <v-form ref="sigaaFormRef" class="mt-4" @submit.prevent="validarSigaa">
                <v-text-field
                  v-model="sigaaCredentials.login"
                  :rules="loginRules"
                  autocomplete="username"
                  class="mb-4"
                  density="comfortable"
                  label="Login SIGAA"
                  placeholder="Informe seu login"
                  prepend-inner-icon="mdi-account-outline"
                  variant="outlined"
                />

                <v-text-field
                  v-model="sigaaCredentials.senha"
                  :append-inner-icon="showSigaaPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                  :rules="sigaaPasswordRules"
                  :type="showSigaaPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  class="mb-3"
                  density="comfortable"
                  label="Senha do SIGAA"
                  placeholder="Digite sua senha"
                  prepend-inner-icon="mdi-lock-outline"
                  variant="outlined"
                  @click:append-inner="showSigaaPassword = !showSigaaPassword"
                />

                <div class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center ga-3 mt-2">
                  <v-btn
                    class="text-none px-0"
                    color="warning"
                    size="small"
                    to="/auth/login"
                    variant="text"
                  >
                    Já tenho cadastro
                  </v-btn>

                  <AppButton :loading="isValidating" color="warning" type="submit" variant="flat">
                    Validar no SIGAA
                  </AppButton>
                </div>
              </v-form>
            </template>

            <template v-else>
              <div v-if="sigaaProfile" class="auth-summary mb-5 pa-4 rounded-xl">
                <div class="d-flex flex-column flex-sm-row align-center ga-4">
                  <v-avatar class="auth-summary__avatar" size="92">
                    <v-img :alt="sigaaProfile.nome" :src="sigaaProfile.foto" cover/>
                  </v-avatar>

                  <div class="flex-grow-1 text-center text-sm-left">
                    <div class="text-title-large font-weight-bold mb-1">{{ sigaaProfile.nome }}</div>
                    <div class="text-body-medium text-medium-emphasis mb-3">
                      Matrícula {{ sigaaProfile.matricula }} • {{ sigaaProfile.nivel }} • {{ sigaaProfile.status }}
                    </div>
                  </div>
                </div>
              </div>

              <v-form ref="cadastroFormRef" class="mt-4" @submit.prevent="finalizarCadastro">
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="cadastroComplementar.usuario"
                      :rules="usuarioRules"
                      autocomplete="username"
                      density="comfortable"
                      label="Nome de usuário"
                      placeholder="Escolha como deseja entrar no sistema"
                      prepend-inner-icon="mdi-account-circle-outline"
                      variant="outlined"
                    />
                  </v-col>

                  <v-col cols="12">
                    <v-text-field
                      v-model="cadastroComplementar.email"
                      :rules="emailRules"
                      autocomplete="email"
                      density="comfortable"
                      label="Email de contato"
                      placeholder="seu.email@exemplo.com"
                      prepend-inner-icon="mdi-email-outline"
                      variant="outlined"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="cadastroComplementar.senha"
                      :append-inner-icon="showLocalPassword ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
                      :rules="senhaRules"
                      :type="showLocalPassword ? 'text' : 'password'"
                      autocomplete="new-password"
                      density="comfortable"
                      label="Senha de acesso"
                      placeholder="Crie sua senha"
                      prepend-inner-icon="mdi-lock-outline"
                      variant="outlined"
                      @click:append-inner="showLocalPassword = !showLocalPassword"
                    />
                  </v-col>

                  <v-col cols="12" md="6">
                    <v-text-field
                      v-model="cadastroComplementar.confirmarSenha"
                      :rules="confirmarSenhaRules"
                      :type="showLocalPassword ? 'text' : 'password'"
                      autocomplete="new-password"
                      density="comfortable"
                      label="Confirmar senha"
                      placeholder="Repita a senha"
                      prepend-inner-icon="mdi-lock-check-outline"
                      variant="outlined"
                    />
                  </v-col>
                </v-row>

                <AppAlert
                  class="mb-4"
                  description="O usuário, o email e a senha serão usados no cadastro local do sistema. Os dados acadêmicos foram validados diretamente no SIGAA."
                  title="Atenção aos dados de acesso"
                  tone="success"
                />

                <div class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center ga-3 mt-2">
                  <v-btn
                    class="text-none px-0"
                    color="warning"
                    size="small"
                    variant="text"
                    @click="voltarParaValidacao"
                  >
                    Voltar
                  </v-btn>

                  <AppButton :loading="isSaving" color="warning" type="submit" variant="flat">
                    Finalizar cadastro
                  </AppButton>
                </div>
              </v-form>
            </template>
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

.auth-summary {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.auth-summary__avatar {
  border: 2px solid rgba(245, 158, 11, 0.45);
}

@media (max-width: 959px) {
  .auth-summary {
    text-align: center;
  }
}
</style>

<route lang="yaml">
meta:
  layout: blank
</route>
