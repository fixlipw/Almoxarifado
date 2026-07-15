<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {
  Check,
  ClipboardList,
  Clock3,
  Eye,
  Loader2,
  Mail,
  PackageCheck,
  RotateCcw,
  Trash2,
  UserRound,
  X
} from 'lucide-vue-next'
import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {Card} from '@/components/ui/card'
import {Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle} from '@/components/ui/dialog'
import {Skeleton} from '@/components/ui/skeleton'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table'
import {Tabs, TabsList, TabsTrigger} from '@/components/ui/tabs'
import {
  approvePedido,
  deletePedido,
  getPedidosAtivos,
  getPedidosAtrasados,
  getPedidosFinalizados,
  getPedidosPendentes,
  rejectPedido,
  returnPedido
} from '@/services/pedidos'
import type {PedidoResponse} from '@/types/dtos'
import {useAuthStore} from '@/stores/auth'
import {useNotificationStore} from '@/stores/notifications'

type PedidoAction = 'approve' | 'reject' | 'return' | 'delete'

const auth = useAuthStore()
const notifications = useNotificationStore()
const tab = ref('pendentes')
const loading = ref(true)
const acting = ref(false)
const pedidos = ref<PedidoResponse[]>([])
const selected = ref<PedidoResponse | null>(null)
const detailsOpen = ref(false)
const confirmationOpen = ref(false)
const pendingAction = ref<PedidoAction | null>(null)
const canManage = computed(() => auth.userRole === 'ADMIN' || auth.userRole === 'BOLSISTA')
const loaders = {
  ativos: getPedidosAtivos,
  pendentes: getPedidosPendentes,
  finalizados: getPedidosFinalizados,
  atrasados: getPedidosAtrasados
}
const tabLabels = {
  ativos: 'Ativo',
  pendentes: 'Pendente',
  finalizados: 'Finalizado',
  atrasados: 'Atrasado'
}
const confirmation = computed(() => ({
  approve: {
    title: 'Aprovar solicitação',
    description: 'Confirma a aprovação deste pedido e a retirada dos itens do estoque?',
    button: 'Aprovar'
  },
  reject: {title: 'Rejeitar solicitação', description: 'Confirma a rejeição deste pedido?', button: 'Rejeitar'},
  return: {
    title: 'Registrar devolução',
    description: 'Confirma a devolução de todos os itens e a finalização do empréstimo?',
    button: 'Registrar devolução'
  },
  delete: {
    title: 'Excluir pedido',
    description: 'Esta ação remove definitivamente o pedido. Deseja continuar?',
    button: 'Excluir'
  },
}[pendingAction.value ?? 'approve']))

async function load() {
  loading.value = true
  try {
    pedidos.value = await loaders[tab.value as keyof typeof loaders]({size: 100})
  } catch {
    pedidos.value = []
    notifications.error('Não foi possível carregar os pedidos.')
  } finally {
    loading.value = false
  }
}

watch(tab, load)
onMounted(load)

function formatDate(value?: string) {
  return value ? new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(value)) : 'Não informado'
}

function requester(pedido: PedidoResponse) {
  return pedido.solicitante?.nome || pedido.solicitante?.email || 'Solicitante'
}

function requesterDetail(pedido: PedidoResponse) {
  return pedido.solicitante?.matricula || pedido.solicitante?.email || 'Sem identificação complementar'
}

function openDetails(pedido: PedidoResponse) {
  selected.value = pedido
  detailsOpen.value = true
}

function requestAction(pedido: PedidoResponse, action: PedidoAction) {
  selected.value = pedido
  pendingAction.value = action
  confirmationOpen.value = true
}

async function executeAction() {
  if (!selected.value || !pendingAction.value) return
  acting.value = true
  try {
    switch (pendingAction.value) {
      case 'approve':
        await approvePedido(selected.value.id)
        notifications.success('Pedido aprovado.')
        break
      case 'reject':
        await rejectPedido(selected.value.id)
        notifications.success('Pedido rejeitado.')
        break
      case 'return':
        await returnPedido(selected.value.id)
        notifications.success('Devolução registrada.')
        break
      case 'delete':
        await deletePedido(selected.value.id)
        notifications.success('Pedido excluído.')
        break
    }
    confirmationOpen.value = false
    detailsOpen.value = false
    selected.value = null
    pendingAction.value = null
    await load()
  } catch {
    notifications.error('Não foi possível atualizar o pedido.')
  } finally {
    acting.value = false
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-medium text-primary">Solicitações</p>
        <h1 class="text-3xl font-bold tracking-tight">{{ canManage ? 'Gestão de pedidos' : 'Meus empréstimos' }}</h1>
        <p class="mt-1 text-sm text-muted-foreground">
          {{
            canManage ? 'Visualize e gerencie os pedidos de todos os usuários.' : 'Acompanhe suas solicitações e devoluções.'
          }}
        </p>
      </div>
      <Button as-child>
        <RouterLink to="/estoque">
          <ClipboardList/>
          Solicitar itens
        </RouterLink>
      </Button>
    </div>

    <Tabs v-model="tab" class="w-full">
      <TabsList class="flex w-full overflow-x-auto flex-nowrap scrollbar-none justify-start sm:justify-center">
        <TabsTrigger class="flex-1 shrink-0 sm:flex-initial" value="pendentes">Pendentes</TabsTrigger>
        <TabsTrigger class="flex-1 shrink-0 sm:flex-initial" value="ativos">Ativos</TabsTrigger>
        <TabsTrigger class="flex-1 shrink-0 sm:flex-initial" value="atrasados">Atrasados</TabsTrigger>
        <TabsTrigger class="flex-1 shrink-0 sm:flex-initial" value="finalizados">Finalizados</TabsTrigger>
      </TabsList>
    </Tabs>

    <div v-if="loading" class="space-y-3">
      <Skeleton v-for="n in 5" :key="n" class="h-14 w-full"/>
    </div>
    <template v-else-if="pedidos.length">
      <!-- Desktop Table View -->
      <Card class="hidden sm:block overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Solicitante</TableHead>
              <TableHead class="hidden md:table-cell">Pedido</TableHead>
              <TableHead class="hidden sm:table-cell">Itens</TableHead>
              <TableHead class="hidden lg:table-cell">Data</TableHead>
              <TableHead class="hidden sm:table-cell">Status</TableHead>
              <TableHead class="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="pedido in pedidos" :key="pedido.id">
              <TableCell>
                <div class="flex items-center gap-3">
                  <span class="grid size-9 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary"><UserRound
                      class="size-4"/></span>
                  <div class="min-w-0"><p class="truncate font-medium">{{ requester(pedido) }}</p>
                    <p class="truncate text-xs text-muted-foreground">{{ requesterDetail(pedido) }}</p></div>
                </div>
              </TableCell>
              <TableCell class="hidden md:table-cell font-mono text-xs">
                <div :title="pedido.codigoPedido" class="truncate max-w-[8rem] lg:max-w-none">
                  {{ pedido.codigoPedido }}
                </div>
              </TableCell>
              <TableCell class="hidden sm:table-cell">
                <Badge variant="outline">{{ pedido.itens.length }} tipo(s)</Badge>
              </TableCell>
              <TableCell class="hidden lg:table-cell text-sm text-muted-foreground">
                {{ formatDate(pedido.dataSolicitacao) }}
              </TableCell>
              <TableCell class="hidden sm:table-cell">
                <Badge :variant="tab === 'atrasados' ? 'destructive' : 'secondary'">
                  {{ tabLabels[tab as keyof typeof tabLabels] }}
                </Badge>
              </TableCell>
              <TableCell>
                <div class="flex justify-end gap-1">
                  <Button :aria-label="`Ver detalhes do pedido ${pedido.codigoPedido}`" size="icon-sm" variant="ghost"
                          @click="openDetails(pedido)">
                    <Eye/>
                  </Button>
                  <template v-if="canManage">
                    <Button v-if="tab === 'pendentes'" aria-label="Rejeitar pedido" size="icon-sm" variant="ghost"
                            @click="requestAction(pedido, 'reject')">
                      <X class="text-destructive"/>
                    </Button>
                    <Button v-if="tab === 'pendentes'" aria-label="Aprovar pedido" size="icon-sm" variant="ghost"
                            @click="requestAction(pedido, 'approve')">
                      <Check class="text-emerald-600"/>
                    </Button>
                    <Button v-if="tab === 'ativos' || tab === 'atrasados'" aria-label="Registrar devolução"
                            size="icon-sm"
                            variant="ghost" @click="requestAction(pedido, 'return')">
                      <RotateCcw class="text-sky-600"/>
                    </Button>
                    <Button aria-label="Excluir pedido" size="icon-sm" variant="ghost"
                            @click="requestAction(pedido, 'delete')">
                      <Trash2 class="text-destructive"/>
                    </Button>
                  </template>
                </div>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Card>

      <!-- Mobile Cards View -->
      <div class="grid gap-4 sm:hidden">
        <Card v-for="pedido in pedidos" :key="pedido.id"
              class="p-4 space-y-3 hover:border-primary/40 transition-colors">
          <div class="flex items-center justify-between gap-3">
            <div class="flex items-center gap-3 min-w-0">
              <span class="grid size-9 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary">
                <UserRound class="size-4"/>
              </span>
              <div class="min-w-0">
                <p class="truncate font-medium text-sm">{{ requester(pedido) }}</p>
                <p class="truncate text-xs text-muted-foreground">{{ requesterDetail(pedido) }}</p>
              </div>
            </div>
            <Badge :variant="tab === 'atrasados' ? 'destructive' : 'secondary'"
                   class="shrink-0 text-[10px] px-1.5 py-0.5">
              {{ tabLabels[tab as keyof typeof tabLabels] }}
            </Badge>
          </div>

          <div class="grid grid-cols-2 gap-2 text-xs border-t border-b py-2 my-2">
            <div>
              <p class="text-muted-foreground font-medium">Pedido</p>
              <p class="font-mono text-foreground font-semibold truncate">{{ pedido.codigoPedido }}</p>
            </div>
            <div>
              <p class="text-muted-foreground font-medium">Itens</p>
              <p class="text-foreground font-semibold">{{ pedido.itens.length }} tipo(s)</p>
            </div>
            <div class="col-span-2">
              <p class="text-muted-foreground font-medium">Data da Solicitação</p>
              <p class="text-foreground font-semibold">{{ formatDate(pedido.dataSolicitacao) }}</p>
            </div>
          </div>

          <div class="flex flex-wrap items-center justify-end gap-1.5 pt-2 border-t">
            <Button :aria-label="`Ver detalhes do pedido ${pedido.codigoPedido}`" class="h-8 px-2" size="sm"
                    variant="outline"
                    @click="openDetails(pedido)">
              <Eye class="size-4 mr-1"/>
              Detalhes
            </Button>
            <template v-if="canManage">
              <Button v-if="tab === 'pendentes'" aria-label="Rejeitar pedido" class="h-8 w-8 text-destructive hover:bg-destructive/10" size="icon-sm"
                      variant="ghost"
                      @click="requestAction(pedido, 'reject')">
                <X class="size-4"/>
              </Button>
              <Button v-if="tab === 'pendentes'" aria-label="Aprovar pedido" class="h-8 px-2 text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950/20" size="sm"
                      variant="ghost"
                      @click="requestAction(pedido, 'approve')">
                <Check class="size-4 mr-1"/>
                Aprovar
              </Button>
              <Button v-if="tab === 'ativos' || tab === 'atrasados'" aria-label="Registrar devolução" class="h-8 px-2 text-sky-600 hover:bg-sky-50 dark:hover:bg-sky-950/20"
                      size="sm" variant="ghost"
                      @click="requestAction(pedido, 'return')">
                <RotateCcw class="size-4 mr-1"/>
                Devolver
              </Button>
              <Button aria-label="Excluir pedido" class="h-8 w-8 text-destructive hover:bg-destructive/10" size="icon-sm"
                      variant="ghost"
                      @click="requestAction(pedido, 'delete')">
                <Trash2 class="size-4"/>
              </Button>
            </template>
          </div>
        </Card>
      </div>
    </template>
    <Card v-else class="grid min-h-64 place-items-center text-center">
      <div class="p-5">
        <component :is="tab === 'finalizados' ? PackageCheck : Clock3"
                   class="mx-auto mb-3 size-10 text-muted-foreground"/>
        <p class="font-medium">Nenhum pedido nesta situação</p>
        <p class="text-sm text-muted-foreground">Quando houver movimentações, elas aparecerão aqui.</p>
      </div>
    </Card>

    <Dialog v-model:open="detailsOpen">
      <DialogContent class="max-w-2xl">
        <DialogHeader>
          <div class="flex items-center gap-2">
            <DialogTitle>Detalhes do pedido</DialogTitle>
            <Badge :variant="tab === 'atrasados' ? 'destructive' : 'secondary'">
              {{ tabLabels[tab as keyof typeof tabLabels] }}
            </Badge>
          </div>
          <DialogDescription>{{ selected?.codigoPedido }}</DialogDescription>
        </DialogHeader>
        <div v-if="selected" class="space-y-5">
          <div class="grid gap-3 rounded-xl border bg-muted/30 p-4 sm:grid-cols-2">
            <div class="flex gap-3">
              <UserRound class="mt-0.5 size-4 text-primary"/>
              <div><p class="text-xs text-muted-foreground">Solicitante</p>
                <p class="font-medium">{{ requester(selected) }}</p>
                <p class="text-xs text-muted-foreground">{{
                    selected.solicitante?.matricula || 'Matrícula não informada'
                  }}</p></div>
            </div>
            <div class="flex gap-3">
              <Mail class="mt-0.5 size-4 text-primary"/>
              <div><p class="text-xs text-muted-foreground">E-mail</p>
                <p class="break-all text-sm">{{ selected.solicitante?.email || 'Não informado' }}</p></div>
            </div>
          </div>
          <div>
            <h3 class="mb-2 text-sm font-semibold">Itens solicitados</h3>
            <div class="space-y-2">
              <div v-for="item in selected.itens" :key="item.id"
                   class="flex justify-between rounded-lg border px-3 py-2 text-sm"><span>{{
                  item.nomeItem
                }}</span><strong>{{ item.quantidadeItem }} un.</strong></div>
            </div>
          </div>
          <div class="grid gap-3 text-sm sm:grid-cols-3">
            <div><p class="text-xs text-muted-foreground">Solicitado em</p>
              <p>{{ formatDate(selected.dataSolicitacao) }}</p></div>
            <div><p class="text-xs text-muted-foreground">Aprovado em</p>
              <p>{{ formatDate(selected.dataAprovacao) }}</p></div>
            <div><p class="text-xs text-muted-foreground">Finalizado em</p>
              <p>{{ formatDate(selected.dataFinalizado) }}</p></div>
          </div>
          <div v-if="selected.feedback" class="rounded-lg border p-3 text-sm"><p
              class="mb-1 text-xs font-medium text-muted-foreground">Observações</p>{{ selected.feedback }}
          </div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="detailsOpen = false">Fechar</Button>
          <template v-if="canManage && selected">
            <Button v-if="tab === 'pendentes'" variant="destructive" @click="requestAction(selected, 'reject')">
              <X/>
              Rejeitar
            </Button>
            <Button v-if="tab === 'pendentes'" @click="requestAction(selected, 'approve')">
              <Check/>
              Aprovar
            </Button>
            <Button v-if="tab === 'ativos' || tab === 'atrasados'" @click="requestAction(selected, 'return')">
              <RotateCcw/>
              Registrar devolução
            </Button>
          </template>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <Dialog v-model:open="confirmationOpen">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ confirmation.title }}</DialogTitle>
          <DialogDescription>{{ confirmation.description }}</DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button :disabled="acting" variant="outline" @click="confirmationOpen = false">Cancelar</Button>
          <Button :disabled="acting"
                  :variant="pendingAction === 'reject' || pendingAction === 'delete' ? 'destructive' : 'default'"
                  @click="executeAction">
            <Loader2 v-if="acting" class="animate-spin"/>
            {{ confirmation.button }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>
