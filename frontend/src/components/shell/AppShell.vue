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
    />

    <AppFooter />
  </v-app>
</template>

<script lang="ts" setup>
  import type { NavItem, NavSection } from './types'
  import { ref, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useDisplay } from 'vuetify'
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

  function handleCheckout () {
    console.log('Pedido finalizado:', cartStore.items)
  }

</script>

<style scoped>
.app-main {
  min-height: calc(100vh - 72px - 72px);
}
</style>
