<script lang="ts" setup>
import {computed, onMounted, ref, watch} from 'vue'
import {
  ChevronLeft,
  ChevronRight,
  Loader2,
  Lock,
  Pencil,
  Plus,
  Search,
  ShieldCheck,
  Trash2,
  Unlock,
  UserRound,
  Users
} from 'lucide-vue-next'
import {Badge} from '@/components/ui/badge'
import {Button} from '@/components/ui/button'
import {Card, CardContent} from '@/components/ui/card'
import {Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle} from '@/components/ui/dialog'
import {Input} from '@/components/ui/input'
import {Label} from '@/components/ui/label'
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select'
import {Skeleton} from '@/components/ui/skeleton'
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table'
import {Tooltip, TooltipContent, TooltipTrigger} from '@/components/ui/tooltip'
import {createUsuario, deleteUsuario, getUsuarios, updateUsuario, updateUsuarioStatus} from '@/services/usuarios'
import {ApiError} from '@/services/api'
import {useNotificationStore} from '@/stores/notifications'
import type {RoleAcesso, UsuarioRequest, UsuarioResponse} from '@/types/dtos'

const notifications = useNotificationStore()
const users = ref<UsuarioResponse[]>([])
const loading = ref(true)
const saving = ref(false)
const search = ref('')
const role = ref('todos')
const statusFilter = ref('todos')
const page = ref(1)
const pageSize = 10
const editorOpen = ref(false)
const confirmationOpen = ref(false)
const selected = ref<UsuarioResponse | null>(null)
const pendingAction = ref<'status' | 'delete' | null>(null)

const emptyForm = (): UsuarioRequest => ({
  username: '', email: '', nome: '', nomeSocial: '', cpf: '', curso: 'SI', papel: 'ALUNO',
  fotoPerfil: '', status: true, bloqueado: false, matricula: ''
})
const form = ref<UsuarioRequest>(emptyForm())

const roleValue = (value: string) => ({Administrador: 'ADMIN', Aluno: 'ALUNO', Bolsista: 'BOLSISTA'}[value] ?? value)
const roleLabel = (value: string) => ({
  ADMIN: 'Administrador',
  ALUNO: 'Aluno',
  BOLSISTA: 'Bolsista'
}[roleValue(value)] ?? value)
const courseLabel = (value: string) => ({
  IA: 'Inteligência Artificial', CC: 'Ciência da Computação', SI: 'Sistemas de Informação',
  RC: 'Redes de Computadores', ES: 'Engenharia de Software', EC: 'Engenharia de Computação', DD: 'Design Digital'
}[value] ?? value)

const filtered = computed(() => {
  const query = search.value.trim().toLocaleLowerCase('pt-BR')
  return users.value.filter(user => {
    const matchesQuery = !query || [user.nome, user.nomeSocial, user.username, user.email, user.matricula, user.cpf]
        .some(value => value?.toLocaleLowerCase('pt-BR').includes(query))
    const matchesRole = role.value === 'todos' || roleValue(user.papel) === role.value
    const matchesStatus = statusFilter.value === 'todos' || (statusFilter.value === 'ativos') === user.status
    return matchesQuery && matchesRole && matchesStatus
  })
})
const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))
const paginated = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))
const rangeStart = computed(() => filtered.value.length ? (page.value - 1) * pageSize + 1 : 0)
const rangeEnd = computed(() => Math.min(page.value * pageSize, filtered.value.length))

watch([search, role, statusFilter], () => page.value = 1)
watch(totalPages, value => {
  if (page.value > value) page.value = value
})

async function load() {
  loading.value = true
  try {
    users.value = await getUsuarios()
  } catch {
    notifications.error('Não foi possível carregar os usuários.')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  selected.value = null
  form.value = emptyForm()
  editorOpen.value = true
}

function openEdit(user: UsuarioResponse) {
  selected.value = user
  form.value = {
    username: user.username, email: user.email, nome: user.nome, nomeSocial: user.nomeSocial ?? '',
    cpf: user.cpf, curso: user.curso, papel: roleValue(user.papel) as RoleAcesso,
    fotoPerfil: user.fotoPerfil ?? '', status: user.status, bloqueado: user.bloqueado,
    matricula: user.matricula ?? ''
  }
  editorOpen.value = true
}

function normalizeCpf(value: string) {
  return value.replace(/\D/g, '')
}

async function save() {
  const cpf = normalizeCpf(form.value.cpf)
  if (form.value.username.trim().length < 3 || form.value.nome.trim().length < 3 || !form.value.email.includes('@') || cpf.length !== 11) {
    notifications.warning('Preencha nome, usuário, e-mail e CPF válidos.')
    return
  }
  saving.value = true
  try {
    const payload: UsuarioRequest = {
      ...form.value,
      username: form.value.username.trim(), email: form.value.email.trim().toLowerCase(),
      nome: form.value.nome.trim(), nomeSocial: form.value.nomeSocial?.trim() || undefined,
      cpf, matricula: form.value.matricula?.trim() || undefined, fotoPerfil: form.value.fotoPerfil?.trim() || undefined
    }
    if (selected.value) await updateUsuario(selected.value.id, payload)
    else await createUsuario(payload)
    notifications.success(selected.value ? 'Usuário atualizado.' : 'Usuário criado.')
    editorOpen.value = false
    await load()
  } catch (error) {
    notifications.error(error instanceof ApiError ? error.message : 'Não foi possível salvar o usuário.')
  } finally {
    saving.value = false
  }
}

function requestAction(user: UsuarioResponse, action: 'status' | 'delete') {
  selected.value = user
  pendingAction.value = action
  confirmationOpen.value = true
}

async function executeAction() {
  if (!selected.value || !pendingAction.value) return
  saving.value = true
  try {
    if (pendingAction.value === 'delete') {
      await deleteUsuario(selected.value.id)
      notifications.success('Usuário excluído.')
    } else {
      await updateUsuarioStatus(selected.value.id, !selected.value.status)
      notifications.success(selected.value.status ? 'Usuário desativado.' : 'Usuário ativado.')
    }
    confirmationOpen.value = false
    await load()
  } catch (error) {
    notifications.error(error instanceof ApiError ? error.message : 'Não foi possível concluir a ação.')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <p class="text-sm font-medium text-primary">Administração</p>
        <h1 class="text-3xl font-bold tracking-tight">Usuários</h1>
        <p class="mt-1 text-sm text-muted-foreground">Gerencie perfis, papéis e acessos ao almoxarifado.</p>
      </div>
      <Button @click="openCreate">
        <Plus/>
        Novo usuário
      </Button>
    </div>

    <Card>
      <CardContent class="grid gap-3 p-4 md:grid-cols-[1fr_12rem_12rem]">
        <div class="relative">
          <Search class="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>
          <Input v-model="search" class="pl-9" placeholder="Buscar por nome, e-mail, CPF..."/>
        </div>
        <Select v-model="role">
          <SelectTrigger>
            <SelectValue placeholder="Todos os papéis"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="todos">Todos os papéis</SelectItem>
            <SelectItem value="ALUNO">Alunos</SelectItem>
            <SelectItem value="BOLSISTA">Bolsistas</SelectItem>
            <SelectItem value="ADMIN">Administradores</SelectItem>
          </SelectContent>
        </Select>
        <Select v-model="statusFilter">
          <SelectTrigger>
            <SelectValue placeholder="Todos os status"/>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="todos">Todos os status</SelectItem>
            <SelectItem value="ativos">Ativos</SelectItem>
            <SelectItem value="inativos">Inativos</SelectItem>
          </SelectContent>
        </Select>
      </CardContent>
    </Card>

    <div v-if="loading" class="space-y-3">
      <Skeleton v-for="n in 6" :key="n" class="h-14 w-full"/>
    </div>
    <template v-else-if="paginated.length">
      <Card class="hidden overflow-hidden sm:block">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Usuário</TableHead>
              <TableHead class="hidden lg:table-cell">Curso</TableHead>
              <TableHead>Papel</TableHead>
              <TableHead>Status</TableHead>
              <TableHead class="text-right">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="user in paginated" :key="user.id">
              <TableCell>
                <div class="flex items-center gap-3"><span
                    class="grid size-9 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary"><UserRound
                    class="size-4"/></span>
                  <div class="min-w-0"><p class="truncate font-medium">{{ user.nome }}</p>
                    <p class="truncate text-xs text-muted-foreground">{{ user.email }} ·
                      {{ user.matricula || user.username }}</p></div>
                </div>
              </TableCell>
              <TableCell class="hidden max-w-52 truncate text-muted-foreground lg:table-cell">{{
                  courseLabel(user.curso)
                }}
              </TableCell>
              <TableCell>
                <Badge :variant="roleValue(user.papel) === 'ADMIN' ? 'default' : 'outline'">
                  <ShieldCheck v-if="roleValue(user.papel) === 'ADMIN'"/>
                  {{ roleLabel(user.papel) }}
                </Badge>
              </TableCell>
              <TableCell>
                <Badge :variant="user.status ? 'secondary' : 'destructive'">{{
                    user.status ? 'Ativo' : 'Inativo'
                  }}
                </Badge>
              </TableCell>
              <TableCell>
                <div class="flex justify-end gap-1">
                  <Tooltip>
                    <TooltipTrigger as-child>
                      <Button aria-label="Editar usuário" size="icon-sm" variant="ghost" @click="openEdit(user)">
                        <Pencil/>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>Editar usuário</TooltipContent>
                  </Tooltip>
                  <Tooltip>
                    <TooltipTrigger as-child>
                      <Button :aria-label="user.status ? 'Desativar usuário' : 'Ativar usuário'" size="icon-sm"
                              variant="ghost" @click="requestAction(user, 'status')">
                        <component :is="user.status ? Lock : Unlock"/>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>{{ user.status ? 'Desativar' : 'Ativar' }}</TooltipContent>
                  </Tooltip>
                  <Tooltip>
                    <TooltipTrigger as-child>
                      <Button aria-label="Excluir usuário" size="icon-sm" variant="ghost"
                              @click="requestAction(user, 'delete')">
                        <Trash2 class="text-destructive"/>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>Excluir usuário</TooltipContent>
                  </Tooltip>
                </div>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Card>

      <div class="grid gap-3 sm:hidden">
        <Card v-for="user in paginated" :key="user.id" class="p-4">
          <div class="flex items-start gap-3"><span
              class="grid size-10 shrink-0 place-items-center rounded-lg bg-primary/10 text-primary"><UserRound
              class="size-5"/></span>
            <div class="min-w-0 flex-1"><p class="truncate font-medium">{{ user.nome }}</p>
              <p class="truncate text-xs text-muted-foreground">{{ user.email }}</p>
              <div class="mt-3 flex flex-wrap gap-2">
                <Badge variant="outline">{{ roleLabel(user.papel) }}</Badge>
                <Badge :variant="user.status ? 'secondary' : 'destructive'">{{
                    user.status ? 'Ativo' : 'Inativo'
                  }}
                </Badge>
              </div>
            </div>
          </div>
          <div class="mt-4 flex justify-end gap-1 border-t pt-3">
            <Button size="sm" variant="outline" @click="openEdit(user)">
              <Pencil/>
              Editar
            </Button>
            <Button size="icon-sm" variant="ghost" @click="requestAction(user, 'status')">
              <component :is="user.status ? Lock : Unlock"/>
            </Button>
            <Button size="icon-sm" variant="ghost" @click="requestAction(user, 'delete')">
              <Trash2 class="text-destructive"/>
            </Button>
          </div>
        </Card>
      </div>

      <div class="flex flex-col items-center justify-between gap-3 text-sm text-muted-foreground sm:flex-row"><span>Mostrando {{
          rangeStart
        }}–{{ rangeEnd }} de {{ filtered.length }}</span>
        <div class="flex items-center gap-2">
          <Button :disabled="page === 1" size="icon-sm" variant="outline" @click="page--">
            <ChevronLeft/>
          </Button>
          <span>Página {{ page }} de {{ totalPages }}</span>
          <Button :disabled="page === totalPages" size="icon-sm" variant="outline" @click="page++">
            <ChevronRight/>
          </Button>
        </div>
      </div>
    </template>
    <Card v-else class="grid min-h-64 place-items-center text-center">
      <div class="p-5">
        <Users class="mx-auto mb-3 size-10 text-muted-foreground"/>
        <p class="font-medium">Nenhum usuário encontrado</p>
        <p class="text-sm text-muted-foreground">Ajuste os filtros ou cadastre um novo usuário.</p></div>
    </Card>

    <Dialog v-model:open="editorOpen">
      <DialogContent class="max-h-[90vh] max-w-2xl overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{{ selected ? 'Editar usuário' : 'Novo usuário' }}</DialogTitle>
          <DialogDescription>Informe os dados de identificação e permissão de acesso.</DialogDescription>
        </DialogHeader>
        <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="save">
          <div class="space-y-2 sm:col-span-2"><Label for="name">Nome completo</Label><Input id="name"
                                                                                             v-model="form.nome"
                                                                                             minlength="3" required/>
          </div>
          <div class="space-y-2"><Label for="social-name">Nome social</Label><Input id="social-name"
                                                                                    v-model="form.nomeSocial"/></div>
          <div class="space-y-2"><Label for="username">Nome de usuário</Label><Input id="username"
                                                                                     v-model="form.username"
                                                                                     minlength="3"
                                                                                     required/></div>
          <div class="space-y-2"><Label for="email">E-mail</Label><Input id="email" v-model="form.email" required
                                                                         type="email"/></div>
          <div class="space-y-2"><Label for="cpf">CPF</Label><Input id="cpf" v-model="form.cpf" inputmode="numeric"
                                                                    maxlength="14" placeholder="000.000.000-00"
                                                                    required/></div>
          <div class="space-y-2"><Label for="registration">Matrícula</Label><Input id="registration"
                                                                                   v-model="form.matricula"
                                                                                   maxlength="30"/></div>
          <div class="space-y-2"><Label>Curso</Label><Select v-model="form.curso">
            <SelectTrigger>
              <SelectValue/>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="IA">Inteligência Artificial</SelectItem>
              <SelectItem value="CC">Ciência da Computação</SelectItem>
              <SelectItem value="SI">Sistemas de Informação</SelectItem>
              <SelectItem value="RC">Redes de Computadores</SelectItem>
              <SelectItem value="ES">Engenharia de Software</SelectItem>
              <SelectItem value="EC">Engenharia de Computação</SelectItem>
              <SelectItem value="DD">Design Digital</SelectItem>
            </SelectContent>
          </Select></div>
          <div class="space-y-2"><Label>Papel</Label><Select v-model="form.papel">
            <SelectTrigger>
              <SelectValue/>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALUNO">Aluno</SelectItem>
              <SelectItem value="BOLSISTA">Bolsista</SelectItem>
              <SelectItem value="ADMIN">Administrador</SelectItem>
            </SelectContent>
          </Select></div>
          <div class="space-y-2 sm:col-span-2"><Label for="photo">URL da foto (opcional)</Label><Input id="photo"
                                                                                                       v-model="form.fotoPerfil"
                                                                                                       type="url"/>
          </div>
          <DialogFooter class="sm:col-span-2">
            <Button type="button" variant="outline" @click="editorOpen = false">Cancelar</Button>
            <Button :disabled="saving" type="submit">
              <Loader2 v-if="saving" class="animate-spin"/>
              {{ selected ? 'Salvar alterações' : 'Criar usuário' }}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>

    <Dialog v-model:open="confirmationOpen">
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{{
              pendingAction === 'delete' ? 'Excluir usuário' : selected?.status ? 'Desativar usuário' : 'Ativar usuário'
            }}
          </DialogTitle>
          <DialogDescription v-if="pendingAction === 'delete'">Esta ação remove definitivamente {{ selected?.nome }}.
            Deseja continuar?
          </DialogDescription>
          <DialogDescription v-else>Confirma a alteração do acesso de {{ selected?.nome }}?</DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button :disabled="saving" variant="outline" @click="confirmationOpen = false">Cancelar</Button>
          <Button :disabled="saving"
                  :variant="pendingAction === 'delete' || selected?.status ? 'destructive' : 'default'"
                  @click="executeAction">
            <Loader2 v-if="saving" class="animate-spin"/>
            Confirmar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>
