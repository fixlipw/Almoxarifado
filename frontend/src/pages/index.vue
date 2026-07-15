<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {Boxes, ClipboardCheck, Loader2, LogIn, Moon, Package, ShieldCheck, Sun, UserPlus} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {useAuthStore} from '@/stores/auth'
import {useNotificationStore} from '@/stores/notifications'
import {Card, CardHeader, CardTitle} from "@/components/ui/card";

const auth = useAuthStore()
const notifications = useNotificationStore()
const route = useRoute()
const router = useRouter()
const action = ref<'login' | 'register' | null>(null)
const dark = ref(localStorage.getItem('theme') === 'dark')
const authenticated = computed(() => auth.isAuthenticated)
document.documentElement.classList.toggle('dark', dark.value)

function toggleTheme() {
  dark.value = !dark.value
  document.documentElement.classList.toggle('dark', dark.value)
  localStorage.setItem('theme', dark.value ? 'dark' : 'light')
}

async function login() {
  action.value = 'login'
  try {
    await auth.login(`${globalThis.location.origin}/dashboard`)
  } catch (error) {
    notifications.error(error instanceof Error ? error.message : 'Não foi possível iniciar o acesso.')
    action.value = null
  }
}

async function register() {
  action.value = 'register'
  try {
    await auth.register(`${globalThis.location.origin}/?cadastro=ok`)
  } catch (error) {
    notifications.error(error instanceof Error ? error.message : 'Não foi possível iniciar o cadastro.')
    action.value = null
  }
}

onMounted(() => {
  if (route.query.cadastro === 'ok') {
    notifications.success('Cadastro concluído. Entre com sua conta institucional.')
    router.replace('/')
  }
})

const features = [
  {icon: Boxes, title: 'Pedidos', text: 'Acompanhe solicitações de materiais de forma clara e organizada.'},
  {icon: ClipboardCheck, title: 'Controle', text: 'Registre movimentações, empréstimos e devoluções com precisão.'},
  {icon: ShieldCheck, title: 'Notificações', text: 'Receba alertas sobre pendências e itens que exigem atenção.'},
  {icon: Package, title: '100% web', text: 'Acesse o sistema em qualquer dispositivo, sem instalar nada.'},
  {icon: Package, title: 'Tudo online', text: 'Centralize processos do almoxarifado em uma interface única.'},
]
</script>

<template>
  <div
      class="relative min-h-screen overflow-hidden bg-[linear-gradient(135deg,color-mix(in_oklab,var(--background)_50%,transparent),color-mix(in_oklab,var(--primary)_18%,var(--background))),url('/ufc_panorama.jpeg')] bg-cover bg-center">
    <div class="absolute inset-0 bg-background/5 backdrop-blur-[0px]"/>
    <header class="relative z-10 mx-auto flex h-20 max-w-7xl items-center px-5">
      <div class="flex items-center gap-3">
        <img alt="Brasão da UFC" class="h-11 w-auto" src="/brasao.png">
        <div><strong class="block text-sm">Almoxarifado UFC</strong><span class="text-xs text-muted-foreground">Campus Quixadá</span>
        </div>
      </div>
      <div class="ml-auto flex gap-2">
        <Button :aria-label="dark ? 'Tema claro' : 'Tema escuro'" size="icon" variant="ghost" @click="toggleTheme">
          <component :is="dark ? Sun : Moon"/>
        </Button>
      </div>
    </header>

    <main class="relative z-10 mx-auto flex max-w-7xl flex-col items-center px-5 py-16 text-center sm:py-24">
      <h1 class="max-w-4xl text-4xl font-bold tracking-tight sm:text-6xl">Bem-vindo(a)</h1>
      <p class="mt-6 max-w-2xl text-base leading-7 text-muted-foreground sm:text-lg">Sistema de Gerenciamento de Estoque
        e Empréstimos da UFC - Quixadá.</p>
      <Button v-if="authenticated" as-child class="mt-8" size="lg">
        <RouterLink to="/dashboard">
          <Package/>
          Acessar almoxarifado
        </RouterLink>
      </Button>
      <div v-else class="mt-8 flex flex-col gap-3 sm:flex-row">
        <Button :disabled="action !== null" size="lg" @click="login">
          <Loader2 v-if="action === 'login'" class="animate-spin"/>
          <LogIn v-else/>
          Entrar no sistema
        </Button>
        <Button :disabled="action !== null" size="lg" variant="outline" @click="register">
          <Loader2 v-if="action === 'register'" class="animate-spin"/>
          <UserPlus v-else/>
          Realizar primeiro acesso
        </Button>
      </div>

      <div class="mt-16 grid w-full gap-4 md:grid-cols-5">
        <Card
            v-for="feature in features"
            :key="feature.title"
            class="group border border-white/10 bg-background/60 text-left shadow-xl shadow-black/10 backdrop-blur transition-transform duration-200 hover:-translate-y-1 hover:bg-background/70"
        >
          <CardHeader class="space-y-3">
            <span
                class="grid size-11 place-items-center rounded-xl bg-primary/10 text-primary ring-1 ring-primary/15 transition-colors group-hover:bg-primary/15">
              <component :is="feature.icon" class="size-5"/>
            </span>
            <div class="space-y-1">
              <CardTitle class="text-base">{{ feature.title }}</CardTitle>
              <p class="text-sm leading-6 text-muted-foreground">{{ feature.text }}</p>
            </div>
          </CardHeader>
        </Card>
      </div>
    </main>
  </div>
</template>
