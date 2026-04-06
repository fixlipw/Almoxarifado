<template>
  <v-app-bar border="b" class="px-2" flat height="72">
    <v-app-bar-nav-icon
        v-if="mobile"
        :aria-label="drawer ? 'Fechar menu' : 'Abrir menu'"
        :icon="drawer ? 'mdi-close' : 'mdi-menu'"
        @click="emit('toggle-menu')"
    />

    <div :class="mobile ? 'ml-1' : 'ml-2'" class="d-flex align-center ga-3">
      <v-avatar color="primary" rounded="lg" size="36">
        <v-icon icon="mdi-package-variant-closed"/>
      </v-avatar>

      <div v-if="!mobile" class="brand-copy">
        <div :class="mobile ? 'text-body-medium' : ''" class="text-title-medium font-weight-bold">Almoxarifado UFC</div>
        <div class="text-body-small text-medium-emphasis">Campus Quixadá</div>
      </div>
    </div>

    <v-spacer/>

    <div v-if="!mobile" class="d-flex align-center ga-1 ml-10">
      <AppButton
          v-for="item in navItems"
          :key="item.value"
          :color="activeSection === item.value ? 'primary' : 'inherit'"
          :prepend-icon="item.icon"
          :variant="activeSection === item.value ? 'flat' : 'text'"

          class="text-none px-4"
          rounded="md"
          @click="emit('select-section', item.value)"
      >
        {{ item.label }}
      </AppButton>
    </div>

    <v-spacer/>

    <div class="d-flex align-center ga-2 mr-2">
      <v-tooltip location="bottom">
        <template #activator="{ props }">
          <v-btn
              :icon="theme.global.current.value.dark ? 'mdi-weather-night' : 'mdi-weather-sunny'"
              rounded="md"
              v-bind="props"
              variant="text"
              @click="toggleTheme"
          />
        </template>
        <span>Alternar Tema</span>
      </v-tooltip>

      <v-tooltip location="bottom">
        <template #activator="{ props }">
          <v-badge
              :content="cartItemCount"
              :model-value="cartItemCount > 0"
              color="primary"
              v-bind="props"
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
      <v-chip v-if="authStore.userRole" prepend-icon="mdi-account-outline" size="small" variant="outlined">
        {{ formatRole(authStore.userRole) }}
      </v-chip>
      <v-menu offset-y>
        <template #activator="{ props }">
          <v-btn
              v-if="mobile"
              icon="mdi-account"
              rounded="md"
              v-bind="props"
              variant="text"
          />
          <v-chip
              v-else
              class="cursor-pointer"
              prepend-icon="mdi-account"
              size="small"
              v-bind="props"
              variant="tonal"
          >
            {{ authStore.userName }}
            <v-icon class="ml-1" icon="mdi-chevron-down" size="small"/>
          </v-chip>
        </template>
        <v-list density="compact" min-width="150">
          <v-list-item prepend-icon="mdi-logout" @click="authStore.logout()">
            <v-list-item-title>Sair</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </div>
  </v-app-bar>
</template>

<script lang="ts" setup>
import type {NavItem, NavSection} from './types'
import {computed, onMounted} from 'vue'
import {useTheme} from 'vuetify'
import AppButton from '@/components/ui/AppButton.vue'
import {useCartStore} from '@/stores/cart'
import {useAuthStore} from '@/stores/auth'

const theme = useTheme()
const authStore = useAuthStore()

const cart = useCartStore()
const cartItemCount = computed(() => cart.count)

const formatRole = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return 'Administrador'
    case 'BOLSISTA':
      return 'Bolsista'
    case 'ALUNO':
      return 'Aluno'
    default:
      return 'Usuário'
  }
}

onMounted(() => {
  const savedTheme = localStorage.getItem('almoxarifado-theme')
  if (savedTheme) {
    useTheme().change(savedTheme)
  } else {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    useTheme().change(prefersDark ? 'dark' : 'light')
  }
})

function toggleTheme() {
  const nextTheme = theme.global.current.value.dark ? 'light' : 'dark'
  theme.change(nextTheme)
  localStorage.setItem('almoxarifado-theme', nextTheme)
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
