<template>
  <v-app>
    <AppHeader
      :active-section="activeSection"
      :drawer="drawer"
      :mobile="mobile"
      :nav-items="navItems"
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

    <AppFooter />
  </v-app>
</template>

<script lang="ts" setup>
  import type { NavItem, NavSection } from './types'
  import { ref, watch } from 'vue'
  import { useDisplay } from 'vuetify'
  import AppFooter from './AppFooter.vue'
  import AppHeader from './AppHeader.vue'
  import AppSidebar from './AppSidebar.vue'

  const { mobile } = useDisplay()
  const drawer = ref(false)
  const activeSection = ref<NavSection>('dashboard')

  const navItems: NavItem[] = [
    { label: 'Dashboard', value: 'dashboard', icon: 'mdi-view-dashboard-outline' },
    { label: 'Inventário', value: 'inventario', icon: 'mdi-cube-outline' },
    { label: 'Meus Empréstimos', value: 'emprestimos', icon: 'mdi-clipboard-text-outline' },
  ]

  watch(mobile, () => {
    if (!mobile.value) {
      drawer.value = false
    }
  })

  function selectSection (section: NavSection) {
    activeSection.value = section
    drawer.value = false
  }
</script>

<style scoped>
.app-main {
  min-height: calc(100vh - 72px - 72px);
}
</style>
