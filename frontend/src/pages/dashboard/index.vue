<script lang="ts" setup>
import {computed, onMounted, ref} from 'vue'
import {
  AlertTriangle,
  ArrowRight,
  BarChart3,
  Boxes,
  CheckCircle2,
  ClipboardCheck,
  ClipboardList,
  Clock3,
  PackageCheck,
  PackagePlus,
  RotateCcw,
  ShieldCheck,
  TriangleAlert
} from 'lucide-vue-next'
import {Alert, AlertDescription, AlertTitle} from '@/components/ui/alert'
import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card'
import {Progress} from '@/components/ui/progress'
import {Skeleton} from '@/components/ui/skeleton'
import {getDashboard} from '@/services/dashboard'
import type {DashboardPedidoResponse, DashboardResponse, EstoqueResponse} from '@/types/dtos'
import {useAuthStore} from '@/stores/auth'
import {useNotificationStore} from '@/stores/notifications'

const auth = useAuthStore()
const notifications = useNotificationStore()
const loading = ref(true)
const dashboard = ref<DashboardResponse | null>(null)

const canManage = computed(() => auth.userRole === 'ADMIN' || auth.userRole === 'BOLSISTA')
const firstName = computed(() => auth.userName?.split(' ')[0] || 'usuário')
const roleLabel = computed(() => {
  if (auth.userRole === 'ADMIN') return 'Administrador'
  if (auth.userRole === 'BOLSISTA') return 'Bolsista'
  return 'Aluno'
})
const lowStock = computed(() => dashboard.value?.estoqueBaixo ?? [])
const recentRequests = computed(() => dashboard.value?.pedidosRecentes ?? [])

const managerStats = computed(() => [
  {
    label: 'Aguardando análise',
    value: dashboard.value?.pedidosPendentes ?? 0,
    description: 'pedidos na fila',
    icon: ClipboardCheck,
    tone: 'bg-amber-500/10 text-amber-600',
    to: '/pedidos'
  },
  {
    label: 'Empréstimos ativos',
    value: dashboard.value?.pedidosAtivos ?? 0,
    description: 'em circulação',
    icon: RotateCcw,
    tone: 'bg-sky-500/10 text-sky-600',
    to: '/pedidos'
  },
  {
    label: 'Pedidos atrasados',
    value: dashboard.value?.pedidosAtrasados ?? 0,
    description: 'exigem acompanhamento',
    icon: AlertTriangle,
    tone: 'bg-destructive/10 text-destructive',
    to: '/pedidos'
  },
  {
    label: 'Estoque em atenção',
    value: dashboard.value?.itensEstoqueBaixo ?? 0,
    description: `de ${dashboard.value?.totalTiposEstoque ?? 0} tipos`,
    icon: Boxes,
    tone: 'bg-violet-500/10 text-violet-600',
    to: '/estoque'
  },
])
const studentStats = computed(() => [
  {
    label: 'Pedidos pendentes',
    value: dashboard.value?.pedidosPendentes ?? 0,
    description: 'aguardando análise',
    icon: Clock3,
    tone: 'bg-amber-500/10 text-amber-600',
    to: '/pedidos'
  },
  {
    label: 'Empréstimos ativos',
    value: dashboard.value?.pedidosAtivos ?? 0,
    description: `${dashboard.value?.itensEmprestados ?? 0} itens com você`,
    icon: ClipboardList,
    tone: 'bg-sky-500/10 text-sky-600',
    to: '/pedidos'
  },
  {
    label: 'Pedidos atrasados',
    value: dashboard.value?.pedidosAtrasados ?? 0,
    description: dashboard.value?.pedidosAtrasados ? 'regularize a devolução' : 'nenhuma pendência',
    icon: TriangleAlert,
    tone: 'bg-destructive/10 text-destructive',
    to: '/pedidos'
  },
  {
    label: 'Pedidos concluídos',
    value: dashboard.value?.pedidosFinalizados ?? 0,
    description: 'no seu histórico',
    icon: PackageCheck,
    tone: 'bg-emerald-500/10 text-emerald-600',
    to: '/pedidos'
  },
])
const stats = computed(() => canManage.value ? managerStats.value : studentStats.value)

onMounted(async () => {
  try {
    dashboard.value = await getDashboard()
  } catch {
    notifications.error('Não foi possível carregar todos os dados do dashboard.')
  } finally {
    loading.value = false
  }
})

function availabilityPercentage(item: EstoqueResponse) {
  if (item.quantidade <= 0) return 0
  return Math.min(100, Math.max(0, Math.round(item.quantidadeDisponivel / item.quantidade * 100)))
}

function formatDate(value?: string) {
  return value ? new Intl.DateTimeFormat('pt-BR', {day: '2-digit', month: 'short'}).format(new Date(value)) : 'Sem data'
}

function requestStatus(pedido: DashboardPedidoResponse) {
  if (pedido.status === 'ATRASADO') return {label: 'Atrasado', variant: 'destructive' as const}
  if (pedido.status === 'PENDENTE') return {label: 'Pendente', variant: 'secondary' as const}
  return {label: 'Ativo', variant: 'outline' as const}
}
</script>

<template>
  <div class="space-y-7">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        <div class="mb-2 flex items-center gap-2">
          <p class="text-sm font-medium text-primary">{{ canManage ? 'Painel operacional' : 'Minha área' }}</p>
          <Badge variant="outline">{{ roleLabel }}</Badge>
        </div>
        <h1 class="text-3xl font-bold tracking-tight">Olá, {{ firstName }}</h1>
        <p class="mt-1 text-sm text-muted-foreground">
          {{
            canManage ? 'Acompanhe as prioridades do almoxarifado e mantenha a operação em dia.' : 'Acompanhe seus pedidos, empréstimos e pendências em um só lugar.'
          }}
        </p>
      </div>
      <Button as-child>
        <RouterLink :to="canManage ? '/pedidos' : '/estoque'">
          <component :is="canManage ? ClipboardCheck : PackagePlus"/>
          {{ canManage ? 'Analisar pedidos' : 'Solicitar itens' }}
        </RouterLink>
      </Button>
    </div>

    <Alert v-if="!loading && dashboard?.pedidosAtrasados" variant="destructive">
      <TriangleAlert/>
      <AlertTitle>{{
          canManage ? `${dashboard.pedidosAtrasados} pedidos atrasados exigem atenção` : 'Você possui devolução em atraso'
        }}
      </AlertTitle>
      <AlertDescription>
        {{
          canManage ? 'Acompanhe os solicitantes e registre as devoluções pendentes.' : 'Regularize a devolução para evitar bloqueios em novas solicitações.'
        }}
      </AlertDescription>
    </Alert>

    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <Card v-for="card in stats" :key="card.label" class="transition hover:border-primary/40">
        <CardHeader class="flex-row items-start justify-between space-y-0 pb-3">
          <div>
            <CardTitle class="text-sm font-medium">{{ card.label }}</CardTitle>
            <CardDescription class="mt-1">{{ card.description }}</CardDescription>
          </div>
          <span :class="card.tone" class="grid size-10 shrink-0 place-items-center rounded-xl"><component
              :is="card.icon" class="size-5"/></span>
        </CardHeader>
        <CardContent class="flex items-end justify-between">
          <Skeleton v-if="loading" class="h-9 w-16"/>
          <strong v-else class="text-3xl">{{ card.value }}</strong>
          <Button as-child size="icon-sm" variant="ghost">
            <RouterLink :aria-label="`Ver ${card.label}`" :to="card.to">
              <ArrowRight/>
            </RouterLink>
          </Button>
        </CardContent>
      </Card>
    </div>

    <div class="grid gap-5 xl:grid-cols-[minmax(0,1.35fr)_minmax(20rem,.65fr)]">
      <Card>
        <CardHeader class="flex-row items-start justify-between gap-3">
          <div>
            <CardTitle>{{ canManage ? 'Fila de trabalho' : 'Meus pedidos recentes' }}</CardTitle>
            <CardDescription>{{
                canManage ? 'Pedidos que precisam de acompanhamento da equipe.' : 'Movimentações mais importantes da sua conta.'
              }}
            </CardDescription>
          </div>
          <Button as-child size="sm" variant="outline">
            <RouterLink to="/pedidos">Ver todos
              <ArrowRight/>
            </RouterLink>
          </Button>
        </CardHeader>
        <CardContent>
          <div v-if="loading" class="space-y-3">
            <Skeleton v-for="n in 4" :key="n" class="h-16"/>
          </div>
          <div v-else-if="recentRequests.length" class="divide-y">
            <RouterLink v-for="pedido in recentRequests" :key="pedido.id"
                        class="flex items-center gap-3 py-3 transition hover:text-primary"
                        to="/pedidos">
              <span class="grid size-9 shrink-0 place-items-center rounded-lg bg-muted"><ClipboardList class="size-4"/></span>
              <div class="min-w-0 flex-1">
                <p class="truncate text-sm font-medium">{{ pedido.codigoPedido }}</p>
                <p class="truncate text-xs text-muted-foreground">
                  {{ canManage ? pedido.solicitante : `${pedido.tiposItens} tipo(s) de item` }}</p>
              </div>
              <div class="text-right">
                <Badge :variant="requestStatus(pedido).variant">{{ requestStatus(pedido).label }}</Badge>
                <p class="mt-1 text-[11px] text-muted-foreground">{{ formatDate(pedido.data) }}</p>
              </div>
            </RouterLink>
          </div>
          <div v-else class="grid min-h-52 place-items-center text-center">
            <div>
              <CheckCircle2 class="mx-auto mb-3 size-9 text-emerald-600"/>
              <p class="font-medium">Tudo em dia</p>
              <p class="text-sm text-muted-foreground">Nenhum pedido precisa de atenção agora.</p></div>
          </div>
        </CardContent>
      </Card>

      <Card v-if="canManage">
        <CardHeader>
          <CardTitle>Estoque em atenção</CardTitle>
          <CardDescription>Itens ativos com até 25% de disponibilidade.</CardDescription>
        </CardHeader>
        <CardContent>
          <div v-if="loading" class="space-y-4">
            <Skeleton v-for="n in 4" :key="n" class="h-12"/>
          </div>
          <div v-else-if="lowStock.length" class="space-y-4">
            <div v-for="item in lowStock" :key="item.id" class="space-y-2">
              <div class="flex justify-between gap-3 text-sm"><span class="truncate font-medium">{{
                  item.nome
                }}</span><span class="shrink-0 text-muted-foreground">{{ item.quantidadeDisponivel }}/{{
                  item.quantidade
                }}</span></div>
              <Progress :model-value="availabilityPercentage(item)" class="[&>div]:bg-amber-500"/>
            </div>
            <Button as-child class="w-full" variant="outline">
              <RouterLink to="/estoque">Gerenciar estoque
                <ArrowRight/>
              </RouterLink>
            </Button>
          </div>
          <div v-else class="grid min-h-52 place-items-center text-center">
            <div>
              <CheckCircle2 class="mx-auto mb-3 size-9 text-emerald-600"/>
              <p class="font-medium">Estoque saudável</p>
              <p class="text-sm text-muted-foreground">Nenhum item em nível crítico.</p></div>
          </div>
        </CardContent>
      </Card>

      <Card v-else>
        <CardHeader>
          <CardTitle>Acesso rápido</CardTitle>
          <CardDescription>Atalhos para suas tarefas mais comuns.</CardDescription>
        </CardHeader>
        <CardContent class="grid gap-3">
          <Button as-child class="h-auto justify-between p-4">
            <RouterLink to="/estoque"><span class="flex items-center gap-3"><PackagePlus/>Solicitar materiais</span>
              <ArrowRight/>
            </RouterLink>
          </Button>
          <Button as-child class="h-auto justify-between p-4" variant="outline">
            <RouterLink to="/pedidos"><span class="flex items-center gap-3"><ClipboardList/>Acompanhar pedidos</span>
              <ArrowRight/>
            </RouterLink>
          </Button>
          <div class="rounded-xl border bg-muted/40 p-4 text-sm">
            <div class="flex items-center gap-2 font-medium">
              <ShieldCheck class="size-4 text-primary"/>
              Boas práticas
            </div>
            <p class="mt-2 text-xs leading-relaxed text-muted-foreground">Confira quantidades antes de solicitar e
              devolva os itens dentro do prazo informado.</p></div>
        </CardContent>
      </Card>
    </div>

    <Card v-if="canManage">
      <CardHeader>
        <CardTitle>Ações administrativas</CardTitle>
        <CardDescription>Acesso direto às áreas operacionais.</CardDescription>
      </CardHeader>
      <CardContent class="grid gap-3 md:grid-cols-3">
        <Button as-child class="h-auto justify-between p-4">
          <RouterLink to="/pedidos"><span class="flex items-center gap-3"><ClipboardCheck/>Gerenciar pedidos</span>
            <ArrowRight/>
          </RouterLink>
        </Button>
        <Button as-child class="h-auto justify-between p-4" variant="outline">
          <RouterLink to="/estoque"><span class="flex items-center gap-3"><Boxes/>Gerenciar estoque</span>
            <ArrowRight/>
          </RouterLink>
        </Button>
        <Button v-if="auth.userRole === 'ADMIN'" as-child class="h-auto justify-between p-4" variant="outline">
          <RouterLink to="/relatorios"><span class="flex items-center gap-3"><BarChart3/>Gerar relatórios</span>
            <ArrowRight/>
          </RouterLink>
        </Button>
      </CardContent>
    </Card>
  </div>
</template>
