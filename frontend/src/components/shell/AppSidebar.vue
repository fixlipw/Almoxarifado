<template>
  <v-navigation-drawer
    v-if="mobile"
    class="app-top-drawer border-0"
    height="auto"
    location="top"
    :model-value="drawer"
    temporary
    @update:model-value="emit('update:drawer', $event)"
  >
    <v-list class="pa-4" nav>
      <v-list-item
        v-for="item in navItems"
        :key="item.value"
        :active="activeSection === item.value"
        :base-color="activeSection === item.value ? 'primary' : undefined"
        :color="activeSection === item.value ? 'primary' : 'inherit'"
        :prepend-icon="item.icon"
        rounded="md"
        :title="item.label"
        :variant="activeSection === item.value ? 'flat' : 'text'"
        @click="emit('select-section', item.value)"
      />
    </v-list>
  </v-navigation-drawer>
</template>

<script lang="ts" setup>
  import type { NavItem, NavSection } from './types'

  defineProps<{
    drawer: boolean
    mobile: boolean
    navItems: NavItem[]
    activeSection: NavSection
  }>()

  const emit = defineEmits<{
    'update:drawer': [value: boolean]
    'select-section': [section: NavSection]
  }>()
</script>

<style scoped>
.app-top-drawer {
  inline-size: 100%;
  max-inline-size: 100%;
  block-size: auto !important;
  max-block-size: min(80dvh, 360px);
}

.app-top-drawer:deep(.v-navigation-drawer__content) {
  overflow-y: auto;
}
</style>
