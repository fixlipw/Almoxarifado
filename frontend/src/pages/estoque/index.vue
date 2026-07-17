<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {
  Boxes,
  ChevronsLeft,
  ChevronsRight,
  Loader2,
  Lock,
  PackagePlus,
  Pencil,
  Plus,
  Search,
  ShoppingCart,
  Unlock
} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {Badge} from '@/components/ui/badge'
import {Card, CardContent} from '@/components/ui/card'
import {Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle} from '@/components/ui/dialog'
import {Input} from '@/components/ui/input'
import {Label} from '@/components/ui/label'
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select'
import {Skeleton} from '@/components/ui/skeleton'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table'
import {Progress} from '@/components/ui/progress'
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationFirst,
  PaginationItem,
  PaginationLast,
  PaginationNext,
  PaginationPrevious
} from '@/components/ui/pagination'
import {createEstoque, getEstoques, updateEstoque} from '@/services/estoque'
import {Tooltip, TooltipContent, TooltipTrigger} from '@/components/ui/tooltip'
import type {EstoqueRequest, EstoqueResponse} from '@/types/dtos'
import {useAuthStore} from '@/stores/auth'
import {useCartStore} from '@/stores/cart'
import {useNotificationStore} from '@/stores/notifications'

const auth = useAuthStore()
const cart = useCartStore()
const notifications = useNotificationStore()
const items = ref<EstoqueResponse[]>([])
const loading = ref(true)
const search = ref('')
const type = ref('todos')
const status = ref('todos')
const page = ref(0)
const totalElements = ref(0)
const pageSize = ref('10')
const editorOpen = ref(false)
const quantityOpen = ref(false)
const saving = ref(false)
const selected = ref<EstoqueResponse | null>(null)
const quantity = ref(1)
const form = ref<EstoqueRequest>({
  nome: '',
  quantidade: 1,
  quantidadeDisponivel: 1,
  tipo: 'COMPONENTE',
  isAtivado: true
})
const canManage = computed(() => auth.userRole === 'ADMIN' || auth.userRole === 'BOLSISTA')
const filtered = computed(() => items.value.filter(item => (type.value === 'todos' || item.tipo === type.value) && (status.value === 'todos' || (status.value === 'ativo') === (item.isAtivado !== false))))
const paginationPage = computed({
  get: () => page.value + 1,
  set: value => {
    page.value = value - 1
  }
})
const firstItem = computed(() => totalElements.value ? page.value * Number(pageSize.value) + 1 : 0)
const lastItem = computed(() => Math.min((page.value + 1) * Number(pageSize.value), totalElements.value))

async function load() {
  loading.value = true
  try {
    const data = await getEstoques({
      page: page.value,
      size: Number(pageSize.value),
      search: search.value.trim() || undefined
    })
    items.value = data.content
    totalElements.value = data.totalElements
  } catch {
    notifications.error('Não foi possível carregar o estoque.')
  } finally {
    loading.value = false
  }
}

let timer: ReturnType<typeof setTimeout>
watch(search, () => {
  clearTimeout(timer);
  timer = setTimeout(() => {
    page.value = 0;
    load()
  }, 350)
})
watch(page, load)
watch(pageSize, () => {
  if (page.value === 0) load()
  else page.value = 0
})
onMounted(load)

function availabilityPercentage(item: EstoqueResponse) {
  if (item.quantidade <= 0) return 0
  return Math.min(100, Math.max(0, Math.round(item.quantidadeDisponivel / item.quantidade * 100)))
}

function availabilityTone(item: EstoqueResponse) {
  const percentage = availabilityPercentage(item)
  if (percentage === 0) return '[&>div]:bg-destructive'
  if (percentage <= 25) return '[&>div]:bg-amber-500'
  return '[&>div]:bg-emerald-500'
}

function openCreate() {
  selected.value = null
  form.value = {nome: '', quantidade: 1, quantidadeDisponivel: 1, tipo: 'COMPONENTE', isAtivado: true}
  editorOpen.value = true
}

function openEdit(item: EstoqueResponse) {
  selected.value = item
  form.value = {
    nome: item.nome,
    quantidade: item.quantidade,
    quantidadeDisponivel: item.quantidadeDisponivel,
    tipo: item.tipo,
    isAtivado: item.isAtivado !== false
  }
  editorOpen.value = true
}

async function save() {
  if (!form.value.nome.trim() || Number(form.value.quantidade) < 1) return notifications.warning('Informe nome e quantidade válidos.')
  saving.value = true
  try {
    const payload = {
      ...form.value,
      nome: form.value.nome.trim(),
      quantidade: Number(form.value.quantidade),
      quantidadeDisponivel: Number(form.value.quantidadeDisponivel)
    }
    if (selected.value) await updateEstoque(selected.value.id, payload)
    else await createEstoque(payload)
    notifications.success(selected.value ? 'Item atualizado.' : 'Item criado.')
    editorOpen.value = false
    await load()
  } catch {
    notifications.error('Não foi possível salvar o item.')
  } finally {
    saving.value = false
  }
}

async function toggle(item: EstoqueResponse) {
  try {
    await updateEstoque(item.id, {
      nome: item.nome,
      quantidade: item.quantidade,
      quantidadeDisponivel: item.quantidadeDisponivel,
      tipo: item.tipo,
      isAtivado: item.isAtivado === false
    })
    notifications.success(item.isAtivado === false ? 'Item desbloqueado.' : 'Item bloqueado.')
    await load()
  } catch {
    notifications.error('Não foi possível alterar o item.')
  }
}

function openQuantity(item: EstoqueResponse) {
  selected.value = item;
  quantity.value = 1;
  quantityOpen.value = true
}

function addToCart() {
  if (!selected.value) return
  cart.addItem({
    id: selected.value.id,
    title: selected.value.nome,
    category: selected.value.tipo === 'EQUIPAMENTO' ? 'Equipamento' : 'Componente',
    available: selected.value.quantidadeDisponivel,
    quantity: Number(quantity.value)
  })
  quantityOpen.value = false
  notifications.success('Item adicionado ao carrinho.')
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div><p class="text-sm font-medium text-primary">Catálogo</p>
        <h1 class="text-3xl font-bold tracking-tight">Estoque</h1>
        <p class="mt-1 text-sm text-muted-foreground">Consulte a disponibilidade e solicite materiais.</p></div>
      <Button v-if="canManage" @click="openCreate">
        <Plus/>
        Novo item
      </Button>
    </div>
    <Card>
      <CardContent class="grid gap-3 p-4 md:grid-cols-[1fr_12rem_12rem]">
        <div class="relative">
          <Search class="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>
          <Input v-model="search" class="pl-9" placeholder="Buscar por nome..."/></div>
        <Select v-model="type">
          <SelectTrigger>
            <SelectValue placeholder="Todos os tipos"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="todos">Todos os tipos</SelectItem>
            <SelectItem value="COMPONENTE">Componentes</SelectItem>
            <SelectItem value="EQUIPAMENTO">Equipamentos</SelectItem>
          </SelectContent>
        </Select><Select v-model="status">
        <SelectTrigger>
          <SelectValue placeholder="Todos os status"/>
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="todos">Todos os status</SelectItem>
          <SelectItem value="ativo">Ativos</SelectItem>
          <SelectItem value="bloqueado">Bloqueados</SelectItem>
        </SelectContent>
      </Select></CardContent>
    </Card>
    <div v-if="loading" class="space-y-3">
      <Skeleton v-for="n in 5" :key="n" class="h-12 w-full"/>
    </div>
    <template v-else-if="filtered.length">
      <!-- Desktop Table View -->
      <Card class="hidden sm:block overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Item</TableHead>
              <TableHead class="hidden md:table-cell">Tipo</TableHead>
              <TableHead>Disponibilidade</TableHead>
              <TableHead class="hidden sm:table-cell">Status</TableHead>
              <TableHead class="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="item in filtered" :key="item.id">
              <TableCell>
                <div class="flex items-center gap-3 min-w-0">
                  <span class="grid size-9 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary"><Boxes
                      class="size-4"/></span>
                  <span :title="item.nome" class="font-medium truncate">{{ item.nome }}</span>
                </div>
              </TableCell>
              <TableCell class="hidden md:table-cell text-muted-foreground">{{
                  item.tipo === 'EQUIPAMENTO' ? 'Equipamento' : 'Componente'
                }}
              </TableCell>
              <TableCell>
                <div class="w-auto sm:min-w-36 space-y-2">
                  <div class="flex items-center justify-between gap-3 text-xs">
                    <span><strong class="text-sm">{{ item.quantidadeDisponivel }}</strong><span
                        class="text-muted-foreground"> / {{ item.quantidade }}</span></span>
                    <span class="font-medium text-muted-foreground hidden sm:block">{{
                        availabilityPercentage(item)
                      }}%</span>
                  </div>
                  <Progress :aria-label="`${item.quantidadeDisponivel} de ${item.quantidade} itens disponíveis`"
                            :model-value="availabilityPercentage(item)"
                            class="hidden sm:block"/>
                </div>
              </TableCell>
              <TableCell class="hidden sm:table-cell">
                <Badge :variant="item.isAtivado === false ? 'destructive' : 'secondary'">
                  {{ item.isAtivado === false ? 'Bloqueado' : 'Ativo' }}
                </Badge>
              </TableCell>
              <TableCell>
                <div class="flex justify-end gap-1">
                  <Button v-if="item.isAtivado !== false && item.quantidadeDisponivel > 0" class="px-2 sm:px-3" size="sm"
                          variant="outline" @click="openQuantity(item)">
                    <ShoppingCart/>
                    <span class="hidden sm:inline">Solicitar</span>
                  </Button>
                  <Tooltip v-if="canManage">
                    <TooltipTrigger as-child>
                      <Button aria-label="Editar item" size="icon-sm" variant="ghost"
                              @click="openEdit(item)">
                        <Pencil/>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>Editar item</TooltipContent>
                  </Tooltip>
                  <Tooltip v-if="canManage">
                    <TooltipTrigger as-child>
                      <Button :aria-label="item.isAtivado === false ? 'Desbloquear item' : 'Bloquear item'"
                              size="icon-sm"
                              variant="ghost"
                              @click="toggle(item)">
                        <component :is="item.isAtivado === false ? Unlock : Lock"/>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>{{ item.isAtivado === false ? 'Desbloquear item' : 'Bloquear item' }}</TooltipContent>
                  </Tooltip>
                </div>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Card>

      <!-- Mobile Cards View -->
      <div class="grid gap-4 sm:hidden">
        <Card v-for="item in filtered" :key="item.id" class="p-4 space-y-3 hover:border-primary/40 transition-colors">
          <div class="flex items-center justify-between gap-2">
            <div class="flex items-center gap-3 min-w-0">
              <span class="grid size-9 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary">
                <Boxes class="size-4"/>
              </span>
              <span :title="item.nome" class="font-medium truncate text-sm">{{ item.nome }}</span>
            </div>
            <Badge :variant="item.isAtivado === false ? 'destructive' : 'secondary'"
                   class="shrink-0 text-[10px] px-1.5 py-0.5">
              {{ item.isAtivado === false ? 'Bloqueado' : 'Ativo' }}
            </Badge>
          </div>

          <div class="grid grid-cols-2 gap-2 text-xs border-t border-b py-2 my-2">
            <div>
              <p class="text-muted-foreground font-medium">Tipo</p>
              <p class="text-foreground font-semibold">
                {{ item.tipo === 'EQUIPAMENTO' ? 'Equipamento' : 'Componente' }}
              </p>
            </div>
            <div>
              <p class="text-muted-foreground font-medium">Disponibilidade</p>
              <p class="text-foreground font-semibold">
                <strong class="text-sm">{{ item.quantidadeDisponivel }}</strong> / {{ item.quantidade }}
                <span class="text-muted-foreground text-[10px] ml-1">({{ availabilityPercentage(item) }}%)</span>
              </p>
            </div>
          </div>

          <div class="flex flex-wrap items-center justify-end gap-1.5 pt-2 border-t">
            <Button v-if="item.isAtivado !== false && item.quantidadeDisponivel > 0" class="h-8 px-2" size="sm"
                    variant="outline"
                    @click="openQuantity(item)">
              <ShoppingCart class="size-4 mr-1"/>
              Solicitar
            </Button>
            <template v-if="canManage">
              <Tooltip>
                <TooltipTrigger as-child>
                  <Button aria-label="Editar item" class="h-8 w-8 text-primary hover:bg-primary/5 dark:hover:bg-primary/10" size="icon-sm"
                          variant="ghost" @click="openEdit(item)">
                    <Pencil class="size-4"/>
                  </Button>
                </TooltipTrigger>
                <TooltipContent>Editar item</TooltipContent>
              </Tooltip>
              <Tooltip>
                <TooltipTrigger as-child>
                  <Button :aria-label="item.isAtivado === false ? 'Desbloquear item' : 'Bloquear item'"
                          :class="item.isAtivado === false ? 'text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-950/20' : 'text-destructive hover:bg-destructive/10'"
                          class="h-8 w-8"
                          size="icon-sm"
                          variant="ghost"
                          @click="toggle(item)">
                    <component :is="item.isAtivado === false ? Unlock : Lock" class="size-4"/>
                  </Button>
                </TooltipTrigger>
                <TooltipContent>{{ item.isAtivado === false ? 'Desbloquear item' : 'Bloquear item' }}</TooltipContent>
              </Tooltip>
            </template>
          </div>
        </Card>
      </div>
    </template>
    <Card v-else class="grid min-h-64 place-items-center text-center text-muted-foreground">
      <div class="p-5">
        <Boxes class="mx-auto mb-3 size-10"/>
        <p class="font-medium text-foreground">Nenhum item encontrado</p>
        <p class="text-sm">Ajuste os filtros ou tente outra busca.</p>
      </div>
    </Card>
    <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <div class="flex flex-col gap-3 text-sm text-muted-foreground sm:flex-row sm:items-center">
        <span>Exibindo {{ firstItem }}–{{ lastItem }} de {{ totalElements }} itens</span>
        <div class="flex items-center gap-2">
          <span>Itens por página</span>
          <Select v-model="pageSize">
            <SelectTrigger class="h-8 w-20">
              <SelectValue/>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="10">10</SelectItem>
              <SelectItem value="20">20</SelectItem>
              <SelectItem value="50">50</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>
      <Pagination v-model:page="paginationPage" :items-per-page="Number(pageSize)" :sibling-count="1"
                  :total="totalElements" class="mx-0 w-auto justify-start lg:justify-end" show-edges>
        <PaginationContent v-slot="{ items: pages }">
          <PaginationFirst aria-label="Primeira página" title="Primeira página">
            <ChevronsLeft/>
            <span class="hidden sm:block">Primeira</span>
          </PaginationFirst>
          <PaginationPrevious aria-label="Página anterior">
            <span class="hidden sm:block">Anterior</span>
          </PaginationPrevious>
          <template v-for="(paginationItem, index) in pages" :key="index">
            <PaginationItem v-if="paginationItem.type === 'page'"
                            :is-active="paginationItem.value === paginationPage"
                            :value="paginationItem.value">
              {{ paginationItem.value }}
            </PaginationItem>
            <PaginationEllipsis v-else/>
          </template>
          <PaginationNext aria-label="Próxima página">
            <span class="hidden sm:block">Próxima</span>
          </PaginationNext>
          <PaginationLast aria-label="Última página" title="Última página">
            <span class="hidden sm:block">Última</span>
            <ChevronsRight/>
          </PaginationLast>
        </PaginationContent>
      </Pagination>
    </div>

    <Dialog v-model:open="quantityOpen">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Adicionar ao carrinho</DialogTitle>
          <DialogDescription>Escolha a quantidade de {{ selected?.nome }}.</DialogDescription>
        </DialogHeader>
        <div class="space-y-2"><Label for="quantity">Quantidade</Label><Input id="quantity" v-model="quantity"
                                                                              :max="selected?.quantidadeDisponivel"
                                                                              min="1"
                                                                              type="number"/>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="quantityOpen = false">Cancelar</Button>
          <Button @click="addToCart">
            <ShoppingCart/>
            Adicionar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
    <Dialog v-model:open="editorOpen">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{ selected ? 'Editar item' : 'Novo item' }}</DialogTitle>
          <DialogDescription>Dados de inventário são validados novamente pelo servidor.</DialogDescription>
        </DialogHeader>
        <div class="grid gap-4 py-2">
          <div class="space-y-2"><Label for="name">Nome</Label><Input id="name" v-model="form.nome" maxlength="120"/>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div class="space-y-2"><Label for="total">Quantidade total</Label><Input id="total"
                                                                                     v-model="form.quantidade"
                                                                                     min="1" type="number"/></div>
            <div class="space-y-2"><Label for="available">Disponível</Label><Input id="available"
                                                                                   v-model="form.quantidadeDisponivel"
                                                                                   min="0" type="number"/></div>
          </div>
          <div class="space-y-2"><Label>Tipo</Label><Select v-model="form.tipo">
            <SelectTrigger>
              <SelectValue/>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="COMPONENTE">Componente</SelectItem>
              <SelectItem value="EQUIPAMENTO">Equipamento</SelectItem>
            </SelectContent>
          </Select></div>
        </div>
        <DialogFooter>
          <Button variant="outline" @click="editorOpen = false">Cancelar</Button>
          <Button :disabled="saving" @click="save">
            <Loader2 v-if="saving" class="animate-spin"/>
            <PackagePlus v-else/>
            Salvar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>
