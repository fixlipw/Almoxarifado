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
        <v-icon icon="mdi-package-variant-closed" />
      </v-avatar>

      <div v-if="!mobile" class="brand-copy">
        <div class="text-title-medium font-weight-bold" :class="mobile ? 'text-body-medium' : ''">Almoxarifado UFC</div>
        <div class="text-body-small text-medium-emphasis">Campus Quixadá</div>
      </div>
    </div>

    <v-spacer />

    <div v-if="!mobile" class="d-flex align-center ga-1 ml-10">
      <AppButton
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
      </AppButton>
    </div>

    <v-spacer />

    <div class="d-flex align-center ga-2 mr-2">
      <v-tooltip location="bottom">
        <template #activator="{ props }">
          <v-btn
            v-bind="props"
            :icon="theme.global.current.value.dark ? 'mdi-weather-night' : 'mdi-weather-sunny'"
            rounded="md"
            variant="text"
            @click="toggleTheme"
          />
        </template>
        <span>Alternar Tema</span>
      </v-tooltip>

      <v-tooltip location="bottom">
        <template #activator="{ props }">
          <v-badge
            v-bind="props"
            color="primary"
            :content="cartItemCount"
            :model-value="cartItemCount > 0"
          >
            <v-btn
              icon="mdi-cart-outline"
              rounded="md"
              variant="text"
              @click="emit('open-cart')"
            />
          </v-badge>
        </template>
        <span>Carrinho</span>
      </v-tooltip>
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
  import { computed, onMounted } from 'vue'
  import { useTheme } from 'vuetify'
  import AppButton from '@/components/ui/AppButton.vue'
  import { useCartStore } from '@/stores/cart'

  const theme = useTheme()
  const cartStore = useCartStore()

  const cartItemCount = computed(() => cartStore.itemCount)

  onMounted(() => {
    const savedTheme = localStorage.getItem('almoxarifado-theme')
    if (savedTheme) {
      useTheme().change(savedTheme)
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      useTheme().change(prefersDark ? 'dark' : 'light')
    }
  })

  function toggleTheme () {
    theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark'
    localStorage.setItem('almoxarifado-theme', theme.global.name.value)
  }

  defineProps<{
    drawer?: boolean
    mobile: boolean
    navItems: NavItem[]
    activeSection: NavSection
  }>()

  const emit = defineEmits<{
    'open-cart': []
    'toggle-menu': []
    'select-section': [section: NavSection]
  }>()
</script>

<style scoped>
.brand-copy {
  line-height: 1.15;
}
</style>
