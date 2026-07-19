<script lang="ts" setup>
import {computed, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {
  BarChart3,
  Boxes,
  ChevronDown,
  ClipboardList,
  LayoutDashboard,
  LogOut,
  Menu,
  Moon,
  Package,
  Settings,
  ShoppingCart,
  Sun,
  UserCircle2,
  UserRound,
  Users,
  X
} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {Badge} from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle} from '@/components/ui/sheet'
import {Separator} from '@/components/ui/separator'
import {useAuthStore} from '@/stores/auth'
import {useCartStore} from '@/stores/cart'
import {useNotificationStore} from '@/stores/notifications'
import {createPedido} from '@/services/pedidos'
import {useThemePreference} from '@/composables/useThemePreference'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const notifications = useNotificationStore()
const menuOpen = ref(false)
const cartOpen = ref(false)
const submitting = ref(false)
const theme = useThemePreference()

const nav = computed(() => [
  {label: 'Visão geral', to: '/dashboard', icon: LayoutDashboard},
  {label: 'Estoque', to: '/estoque', icon: Boxes},
  {label: 'Pedidos', to: '/pedidos', icon: ClipboardList},
  ...(auth.userRole === 'ADMIN' ? [{label: 'Usuários', to: '/usuarios', icon: Users}] : []),
  ...(auth.userRole === 'ADMIN' ? [{label: 'Relatórios', to: '/relatorios', icon: BarChart3}] : []),
])

function toggleTheme() {
  theme.toggleMode()
}

async function checkout() {
  if (!cart.items.length || submitting.value) return
  submitting.value = true
  try {
    await createPedido({
      codigoPedido: crypto.randomUUID(),
      itens: cart.items.map(item => ({estoqueId: item.id, quantidadeItem: item.quantity})),
    })
    cart.clearCart()
    cartOpen.value = false
    notifications.success('Pedido enviado com sucesso.')
  } catch {
    notifications.error('Não foi possível enviar o pedido.')
  } finally {
    submitting.value = false
  }
}

function logout() {
  auth.logout(globalThis.location.origin)
}

function goToProfile() {
  router.push('/perfil')
}

function goToSettings() {
  router.push('/configuracoes')
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <header class="sticky top-0 z-40 border-b bg-background/90 backdrop-blur-xl">
      <div class="mx-auto flex h-16 max-w-7xl items-center gap-3 px-4 sm:px-6">
        <Button aria-label="Abrir menu" class="md:hidden" size="icon" variant="ghost" @click="menuOpen = !menuOpen">
          <component :is="menuOpen ? X : Menu"/>
        </Button>
        <RouterLink class="flex items-center gap-3" to="/dashboard">
          <span class="grid size-9 place-items-center rounded-lg bg-primary text-primary-foreground"><Package
              class="size-5"/></span>
          <span class="hidden leading-tight sm:block"><strong class="block text-sm">Almoxarifado UFC</strong><small
              class="text-muted-foreground">Campus Quixadá</small></span>
        </RouterLink>
        <nav class="ml-5 hidden items-center gap-1 md:flex">
          <RouterLink v-for="item in nav" :key="item.to"
                      :class="{ 'bg-accent text-accent-foreground': route.path.startsWith(item.to) }"
                      :to="item.to"
                      class="flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition hover:bg-accent hover:text-accent-foreground">
            <component :is="item.icon" class="size-4"/>
            {{ item.label }}
          </RouterLink>
        </nav>
        <div class="ml-auto flex items-center gap-1">
          <Button :aria-label="theme.isDark.value ? 'Ativar tema claro' : 'Ativar tema escuro'" size="icon"
                  variant="ghost"
                  @click="toggleTheme">
            <component :is="theme.isDark.value ? Sun : Moon"/>
          </Button>
          <Button aria-label="Abrir carrinho" class="relative" size="icon" variant="ghost" @click="cartOpen = true">
            <ShoppingCart/>
            <Badge v-if="cart.count" class="absolute -right-1 -top-1 h-5 min-w-5 justify-center px-1 text-[10px]">
              {{ cart.count }}
            </Badge>
          </Button>
          <DropdownMenu>
            <DropdownMenuTrigger as-child>
              <Button aria-label="Abrir menu do usuário" class="gap-2 px-3" variant="ghost">
                <UserRound class="size-4 text-muted-foreground"/>
                <span class="hidden max-w-32 flex-col items-start leading-tight lg:flex">
                  <span class="truncate text-xs font-medium">{{ auth.userName }}</span>
                  <span class="text-[10px] text-muted-foreground">{{ auth.userRole }}</span>
                </span>
                <ChevronDown class="size-4 text-muted-foreground"/>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" class="w-56">
              <DropdownMenuLabel>
                <div class="flex flex-col gap-0.5">
                  <span class="truncate text-sm font-medium">{{ auth.userName }}</span>
                  <span class="text-xs font-normal text-muted-foreground">{{ auth.userRole }}</span>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator/>
              <DropdownMenuItem class="gap-2" @click="goToProfile">
                <UserCircle2 class="size-4"/>
                Meu perfil
              </DropdownMenuItem>
              <DropdownMenuItem class="gap-2" @click="goToSettings">
                <Settings class="size-4"/>
                Configurações
              </DropdownMenuItem>
              <DropdownMenuSeparator/>
              <DropdownMenuItem class="gap-2 text-destructive focus:text-destructive" @click="logout">
                <LogOut class="size-4"/>
                Sair
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
      <nav v-if="menuOpen" class="border-t p-3 md:hidden">
        <RouterLink v-for="item in nav" :key="item.to" :to="item.to"
                    class="flex items-center gap-3 rounded-md px-3 py-3 text-sm font-medium hover:bg-accent"
                    @click="menuOpen = false">
          <component :is="item.icon" class="size-4"/>
          {{ item.label }}
        </RouterLink>
      </nav>
    </header>
    <main class="mx-auto max-w-7xl px-4 py-8 sm:px-6">
      <slot/>
    </main>

    <Sheet v-model:open="cartOpen">
      <SheetContent class="flex flex-col">
        <SheetHeader>
          <SheetTitle>Carrinho de solicitação</SheetTitle>
          <SheetDescription>Revise quantidades antes de enviar.</SheetDescription>
        </SheetHeader>
        <div v-if="cart.items.length" class="flex-1 space-y-3 overflow-auto py-5">
          <div v-for="item in cart.items" :key="item.id" class="rounded-lg border p-3">
            <div class="flex items-start justify-between gap-3">
              <div><p class="font-medium">{{ item.title }}</p>
                <p class="text-xs text-muted-foreground">{{ item.category }}</p></div>
              <Button size="sm" variant="ghost" @click="cart.removeItem(item.id)">Remover</Button>
            </div>
            <div class="mt-3 flex items-center gap-2">
              <Button size="icon-sm" variant="outline" @click="cart.updateQuantity(item.id, item.quantity - 1)">-
              </Button>
              <span class="w-8 text-center text-sm font-medium">{{ item.quantity }}</span>
              <Button size="icon-sm" variant="outline" @click="cart.updateQuantity(item.id, item.quantity + 1)">+
              </Button>
              <span class="ml-auto text-xs text-muted-foreground">{{ item.available }} disponíveis</span>
            </div>
          </div>
        </div>
        <div v-else class="grid flex-1 place-items-center text-center text-sm text-muted-foreground">
          <div>
            <ShoppingCart class="mx-auto mb-3 size-8"/>
            Seu carrinho está vazio.
          </div>
        </div>
        <Separator/>
        <Button :disabled="cart.isEmpty || submitting" class="mt-4 w-full" @click="checkout">
          {{ submitting ? 'Enviando...' : 'Enviar pedido' }}
        </Button>
      </SheetContent>
    </Sheet>
  </div>
</template>
