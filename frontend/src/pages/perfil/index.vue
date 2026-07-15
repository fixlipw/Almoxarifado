<script lang="ts" setup>
import {computed} from 'vue'
import {BadgeCheck, BookOpen, Fingerprint, Mail, Settings, ShieldCheck, UserRound} from 'lucide-vue-next'
import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card'
import {Separator} from '@/components/ui/separator'
import {useAuthStore} from '@/stores/auth'

const auth = useAuthStore()
const initials = computed(() => auth.userName.split(/\s+/).filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase() || 'U')
const roleLabel = computed(() => auth.userRole === 'ADMIN' ? 'Administrador' : 'Usuário')
</script>

<template>
  <div class="space-y-7">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        <p class="text-sm font-medium text-primary">Minha conta</p>
        <h1 class="text-3xl font-bold tracking-tight">Perfil</h1>
        <p class="mt-1 text-sm text-muted-foreground">Consulte seus dados e informações de acesso.</p>
      </div>
      <Button as-child variant="outline">
        <RouterLink to="/configuracoes">
          <Settings class="size-4"/>
          Configurações
        </RouterLink>
      </Button>
    </div>

    <div class="grid gap-5 lg:grid-cols-[minmax(0,1.4fr)_minmax(18rem,.6fr)]">
      <Card>
        <CardContent class="p-6 sm:p-8">
          <div class="flex flex-col gap-5 sm:flex-row sm:items-center">
            <div
                class="grid size-24 shrink-0 place-items-center rounded-3xl bg-primary text-2xl font-bold text-primary-foreground shadow-sm">
              {{ initials }}
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex flex-wrap items-center gap-2">
                <h2 class="truncate text-2xl font-bold">{{ auth.userName || 'Usuário institucional' }}</h2>
                <Badge variant="secondary">
                  <BadgeCheck class="mr-1 size-3"/>
                  Conta verificada
                </Badge>
              </div>
              <p class="mt-1 text-sm text-muted-foreground">Conta vinculada à autenticação institucional da UFC.</p>
              <div class="mt-4 flex flex-wrap gap-2">
                <Badge variant="outline">
                  <ShieldCheck class="mr-1 size-3"/>
                  {{ roleLabel }}
                </Badge>
                <Badge variant="outline">Campus Quixadá</Badge>
              </div>
            </div>
          </div>

          <Separator class="my-7"/>

          <div class="grid gap-6 sm:grid-cols-2">
            <div class="flex gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><UserRound
                  class="size-5"/></span>
              <div><p class="text-xs font-medium uppercase tracking-wide text-muted-foreground">Nome completo</p>
                <p class="mt-1 font-medium">{{ auth.userName || 'Não informado' }}</p></div>
            </div>
            <div class="flex gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><Fingerprint
                  class="size-5"/></span>
              <div><p class="text-xs font-medium uppercase tracking-wide text-muted-foreground">Identificador</p>
                <p class="mt-1 break-all font-mono text-sm">{{ auth.userId || 'Não informado' }}</p></div>
            </div>
            <div class="flex gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><Mail
                  class="size-5"/></span>
              <div><p class="text-xs font-medium uppercase tracking-wide text-muted-foreground">E-mail</p>
                <p class="mt-1 text-sm text-muted-foreground">Gerenciado pela conta institucional</p></div>
            </div>
            <div class="flex gap-3">
              <span class="grid size-10 shrink-0 place-items-center rounded-xl bg-primary/10 text-primary"><BookOpen
                  class="size-5"/></span>
              <div><p class="text-xs font-medium uppercase tracking-wide text-muted-foreground">Vínculo</p>
                <p class="mt-1 font-medium">Universidade Federal do Ceará</p></div>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Segurança da conta</CardTitle>
          <CardDescription>Seu acesso é protegido pelo provedor institucional.</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <div class="rounded-xl border bg-background p-4">
            <div class="flex items-center gap-3">
              <ShieldCheck class="size-5 text-emerald-600"/>
              <p class="font-medium">Sessão autenticada</p></div>
            <p class="mt-2 text-sm text-muted-foreground">Senhas e recuperação de conta são gerenciadas fora do
              Almoxarifado.</p>
          </div>
          <Button as-child class="w-full" variant="outline">
            <RouterLink to="/configuracoes">Personalizar aparência</RouterLink>
          </Button>
        </CardContent>
      </Card>
    </div>
  </div>
</template>
