<template>
  <v-app>
    <AppHeader
      :active-section="activeSection"
      :drawer="drawer"
      :mobile="mobile"
      :nav-items="navItems"
      @open-cart="showCartDialog = true"
      @select-section="selectSection"
      @toggle-menu="drawer = !drawer"
    />

    <AppSidebar
      v-model:drawer="drawer"
      :active-section="activeSection"
      :mobile="mobile"
      :nav-items="navItems"
      @select-section="selectSection"
    />

    <v-main class="app-main">
      <v-container class="py-6" fluid>
        <slot />
      </v-container>
    </v-main>

    <CartDialog
      v-model="showCartDialog"
      @checkout="handleCheckout"
      @update-loan="handleUpdateLoan"
    />

    <AppFooter />
  </v-app>
</template>

<script lang="ts" setup>
  import type { NavItem, NavSection } from './types'
  import { ref, watch, computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useDisplay } from 'vuetify'
  import AppFooter from './AppFooter.vue'
  import AppHeader from './AppHeader.vue'
  import AppSidebar from './AppSidebar.vue'
  import CartDialog from '@/components/estoque/CartDialog.vue'
  import { createPedido } from '@/services/pedidos'
  import { createItemPedido } from '@/services/itensPedido'
  import { useCartStore } from '@/stores/cart'
  import { useNotificationStore } from '@/stores/notifications'
  import { useAuthStore } from '@/stores/auth'
  import { getLocalISOString } from '@/utils'

  const { mobile } = useDisplay()
  const router = useRouter()
  const route = useRoute()
  const drawer = ref(false)
  const showCartDialog = ref(false)
  const activeSection = ref<NavSection>('dashboard')
  const cartStore = useCartStore()
  const authStore = useAuthStore()

  const isAdministrador = computed(() => authStore.userRole === 'ADMIN')

  const navItems = computed<NavItem[]>(() => {
    const items: NavItem[] = [
      { label: 'Dashboard', value: 'dashboard', icon: 'mdi-view-dashboard-outline' },
      { label: 'Estoque', value: 'estoque', icon: 'mdi-cube-outline' },
      { label: 'Pedidos', value: 'pedidos', icon: 'mdi-clipboard-text-outline' },
    ]

    if (isAdministrador.value) {
      items.push({ label: 'Usuários', value: 'usuarios', icon: 'mdi-account-group-outline' })
      items.push({ label: 'Relatórios', value: 'relatorios', icon: 'mdi-chart-box-outline' })
    }

    return items
  })

  watch(mobile, () => {
    if (!mobile.value) {
      drawer.value = false
    }
  })

  watch(route, newRoute => {
    const path = newRoute.path.split('/')[1] || 'dashboard'
    activeSection.value = path as NavSection
  }, { immediate: true })

  const notifications = useNotificationStore()

  function selectSection (section: NavSection) {
    activeSection.value = section
    drawer.value = false

    if (section === 'dashboard') {
      router.push('/dashboard')
    } else {
      router.push(`/${section}`)
    }
  }

  async function handleCheckout () {
    try {
      if (cartStore.items.length === 0) return

      if (!authStore.session?.usuario?.id) {
        notifications.error('Sessão inválida. Por favor, faça login novamente.')
        return
      }

      const novoPedido = await createPedido({
        id: crypto.randomUUID(),
        solicitante_id: authStore.session.usuario.id,
        codigo_pedido: `PED-${Date.now().toString().slice(-6)}`,
        data_solicitacao: getLocalISOString(),
      })

      for (const item of cartStore.items) {
        await createItemPedido({
          id: crypto.randomUUID(),
          pedido_id: novoPedido.id,
          estoque_id: item.id,
          quantidade_item: item.quantity,
        })
      }

      cartStore.clearCart()
      cartStore.setCheckout(true)
      showCartDialog.value = false
      notifications.success('Pedido finalizado com sucesso!')

    } catch (error) {
      console.error('Erro ao finalizar pedido:', error)
      notifications.error('Não foi possível finalizar o pedido no momento.')
    }
  }

  async function handleUpdateLoan () {
    cartStore.clearCart()
    showCartDialog.value = false
    notifications.success('Pedido atualizado com sucesso!')
  }
</script>

<style scoped>
.app-main {
  min-height: calc(100vh - 72px - 72px);
}
</style>
