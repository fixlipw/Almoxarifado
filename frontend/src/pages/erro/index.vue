<script lang="ts" setup>
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft, Home, LockKeyhole, LogIn, RefreshCw, SearchX, ServerCrash, ShieldX, WifiOff} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {Card, CardContent} from '@/components/ui/card'

type ErrorCode = '401' | '403' | '404' | '500' | 'offline'

const route = useRoute()
const router = useRouter()

const errorCode = computed<ErrorCode>(() => {
  const code = String(route.meta.errorCode ?? '500')
  return ['401', '403', '404', '500', 'offline'].includes(code) ? code as ErrorCode : '500'
})

const errors = {
  '401': {
    eyebrow: 'Sessão encerrada',
    title: 'Entre novamente para continuar',
    description: 'Sua sessão não existe ou expirou. Faça uma nova autenticação para acessar o sistema.',
    icon: LockKeyhole,
    action: 'Entrar novamente',
    actionIcon: LogIn
  },
  '403': {
    eyebrow: 'Acesso negado',
    title: 'Você não tem permissão para acessar esta página',
    description: 'Se você acredita que deveria ter acesso, entre em contato com um administrador do sistema.',
    icon: ShieldX,
    action: 'Ir para o dashboard',
    actionIcon: Home
  },
  '404': {
    eyebrow: 'Erro 404',
    title: 'Página não encontrada',
    description: 'O endereço informado não existe, foi movido ou não está mais disponível.',
    icon: SearchX,
    action: 'Ir para o início',
    actionIcon: Home
  },
  '500': {
    eyebrow: 'Erro interno',
    title: 'Não foi possível concluir a operação',
    description: 'O sistema encontrou um erro inesperado. Tente novamente em alguns instantes.',
    icon: ServerCrash,
    action: 'Tentar novamente',
    actionIcon: RefreshCw
  },
  offline: {
    eyebrow: 'Sem conexão',
    title: 'Não foi possível conectar ao servidor',
    description: 'Verifique sua conexão com a internet e tente novamente.',
    icon: WifiOff,
    action: 'Tentar novamente',
    actionIcon: RefreshCw
  },
}

const content = computed(() => errors[errorCode.value])
const detail = computed(() => typeof route.query.message === 'string' ? route.query.message : '')

function primaryAction() {
  if (errorCode.value === '401' || errorCode.value === '404') return router.push('/')
  if (errorCode.value === '403') return router.push('/dashboard')
  globalThis.location.reload()
}

function goBack() {
  if (globalThis.history.length > 1) return router.back()
  router.push('/')
}
</script>

<template>
  <main class="relative grid min-h-screen place-items-center overflow-hidden bg-background p-4">
    <div
        class="absolute inset-0 bg-[radial-gradient(circle_at_top,color-mix(in_oklab,var(--primary)_18%,transparent),transparent_48%)]"/>
    <Card class="relative w-full max-w-lg overflow-hidden bg-card/90 shadow-lg backdrop-blur">
      <div class="h-1.5 bg-primary"/>
      <CardContent class="p-8 text-center sm:p-10">
        <span class="mx-auto grid size-16 place-items-center rounded-2xl bg-primary/10 text-primary">
          <component :is="content.icon" class="size-8"/>
        </span>
        <p class="mt-6 text-xs font-semibold uppercase tracking-[0.22em] text-primary">{{ content.eyebrow }}</p>
        <h1 class="mt-2 text-2xl font-bold tracking-tight sm:text-3xl">{{ content.title }}</h1>
        <p class="mx-auto mt-3 max-w-md text-sm leading-6 text-muted-foreground">{{ detail || content.description }}</p>
        <div class="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
          <Button @click="primaryAction">
            <component :is="content.actionIcon"/>
            {{ content.action }}
          </Button>
          <Button variant="outline" @click="goBack">
            <ArrowLeft/>
            Voltar
          </Button>
        </div>
      </CardContent>
    </Card>
  </main>
</template>
