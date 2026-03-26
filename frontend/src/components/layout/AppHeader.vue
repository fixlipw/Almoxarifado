<template>
  <v-app-bar border="b" class="px-2" flat height="72">
    <v-app-bar-nav-icon
      v-if="mobile"
      :aria-label="drawer ? 'Fechar menu' : 'Abrir menu'"
      :icon="drawer ? 'mdi-close' : 'mdi-menu'"
      @click="emit('toggle-menu')"
    />

    <div class="d-flex align-center ga-3" :class="mobile ? 'ml-1' : 'ml-2'">
      <v-avatar color="primary" rounded="lg" size="36">
        <span class="logo-text">UFC</span>
      </v-avatar>

      <div class="brand-copy">
        <div class="text-subtitle-1 font-weight-bold" :class="mobile ? 'text-body-2' : ''">Almoxarifado UFC</div>
        <div class="text-caption text-medium-emphasis">Campus Quixadá</div>
      </div>
    </div>

    <v-spacer />

    <div v-if="!mobile" class="d-flex align-center ga-1 ml-10">
      <v-btn
        v-for="item in navItems"
        :key="item.value"
        class="text-none px-4"
        :color="activeSection === item.value ? 'primary' : 'inherit'"
        :prepend-icon="item.icon"
        rounded="md"
        :variant="activeSection === item.value ? 'flat' : 'text'"
        @click="emit('select-section', item.value)"
      >
        {{ item.label }}
      </v-btn>
    </div>

    <v-spacer />

    <div class="d-flex align-center ga-2 mr-2">
      <v-chip prepend-icon="mdi-account-outline" size="small" variant="outlined">
        Aluno
      </v-chip>
      <v-chip prepend-icon="mdi-account" size="small" variant="tonal">
        Maria
      </v-chip>
    </div>
  </v-app-bar>
</template>

<script lang="ts" setup>
  import type { NavItem, NavSection } from './types'

  defineProps<{
    drawer?: boolean
    mobile: boolean
    navItems: NavItem[]
    activeSection: NavSection
  }>()

  const emit = defineEmits<{
    'toggle-menu': []
    'select-section': [section: NavSection]
  }>()
</script>

<style scoped>
.logo-text {
  color: rgb(var(--v-theme-on-primary));
  font-size: 0.7rem;
  font-weight: 700;
}

.brand-copy {
  line-height: 1.15;
}
</style>
