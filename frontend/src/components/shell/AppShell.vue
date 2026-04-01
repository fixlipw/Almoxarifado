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
  import { ref, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useDisplay } from 'vuetify'
  import { api } from '@/api'
  import CartDialog from '@/components/inventario/CartDialog.vue'
  import { useCartStore } from '@/stores/cart'
  import AppFooter from './AppFooter.vue'
  import AppHeader from './AppHeader.vue'
  import AppSidebar from './AppSidebar.vue'

  const { mobile } = useDisplay()
  const router = useRouter()
  const route = useRoute()
  const cartStore = useCartStore()
  const drawer = ref(false)
  const showCartDialog = ref(false)
  const activeSection = ref<NavSection>('dashboard')

  const navItems: NavItem[] = [
    { label: 'Dashboard', value: 'dashboard', icon: 'mdi-view-dashboard-outline' },
    { label: 'Inventário', value: 'inventario', icon: 'mdi-cube-outline' },
    { label: 'Empréstimos', value: 'emprestimos', icon: 'mdi-clipboard-text-outline' },
  ]

  watch(mobile, () => {
    if (!mobile.value) {
      drawer.value = false
    }
  })

  watch(route, newRoute => {
    const path = newRoute.path.split('/')[1] || 'dashboard'
    activeSection.value = path as NavSection
  }, { immediate: true })

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
      const pedido = await api.createPedido({
        feedback: 'Requisitado pelo sistema',
        solicitanteId: 'u0000000-0000-0000-0000-000000000001', // Mock user for now
        aprovadorId: null,
        finalizadorId: null,
        dataAprovacao: null,
        dataFinalizado: null,
        emprestimoEspecial: false,
      })

      for (const item of cartStore.items) {
        const estoqueId = item.id
        await api.createItemPedido({
          pedidoId: pedido.id,
          estoqueId: estoqueId,
          quantidadeItem: item.quantity,
        })
      }
      cartStore.clearCart()
      alert('Pedido realizado com sucesso!')
    } catch (error) {
      console.error(error)
      alert('Erro ao realizar pedido')
    }
  }

  async function handleUpdateLoan () {
    try {
      const firstItemWithLoan = cartStore.items.find(i => i.emprestimoId)
      if (!firstItemWithLoan?.emprestimoId) return

      const pedidoId = firstItemWithLoan.emprestimoId

      const existingItems = await api.getItensPedido(pedidoId)

      for (const item of existingItems) {
        await api.deleteItemPedido(item.id)
      }

      for (const item of cartStore.items) {
        await api.createItemPedido({
          pedidoId: pedidoId,
          estoqueId: item.id,
          quantidadeItem: item.quantity,
        })
      }

      cartStore.clearCart()
      alert('Pedido atualizado com sucesso!')
    } catch (error) {
      console.error(error)
      alert('Erro ao atualizar pedido')
    }
  }

</script>

<style scoped>
.app-main {
  min-height: calc(100vh - 72px - 72px);
}
</style>
