<script lang="ts" setup>
import type { ItemCardProps } from '@/components/estoque/types'

import { computed } from 'vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppProgressBar from '@/components/ui/AppProgressBar.vue'
import { useAuthStore } from '@/stores/auth'
import { useDisplay } from 'vuetify'

const props = withDefaults(defineProps<ItemCardProps>(), {
  category: '',
  description: '',
  status: 'Ativo',
  statusColor: 'success',
  icon: 'mdi-package-variant-closed',
  iconColor: 'primary',
  buttonText: 'Adicionar ao Pedido',
  isBlocked: false,
})

const authStore = useAuthStore()
const { xs } = useDisplay()

const emit = defineEmits<{
  (e: 'edit' | 'toggle-lock' | 'add-to-cart' | 'request-quantity'): void
}>()

const isOutOfStock = computed(() => props.available === 0)
const isBlocked = computed(() => props.isBlocked)
const isUnavailable = computed(() => isOutOfStock.value || isBlocked.value)
const isBolsistaOrAdmin = computed(() => authStore.userRole === 'BOLSISTA' || authStore.userRole === 'ADMIN')

const statusChipColor = computed(() => {
  return props.statusColor.trim() ? props.statusColor : 'success'
})

const unavailableMessage = computed(() => {
  return isBlocked.value ? 'Item bloqueado no momento' : 'Indisponivel no momento'
})
</script>

<template>
  <AppCard card-class="h-100" content-class="d-flex flex-column h-100">
    <template #header>
      <div class="d-flex w-100 align-start justify-space-between mb-2">
        <div class="d-flex align-start ga-3">
          <v-avatar color="info" rounded="lg" variant="tonal">
            <v-icon :color="props.iconColor">{{ props.icon }}</v-icon>
          </v-avatar>
          <div>
            <div class="text-title-medium font-weight-bold" style="line-height: 1.2;">{{ props.title }}</div>
            <div v-if="props.category" class="text-body-small text-medium-emphasis">{{ props.category }}</div>
          </div>
        </div>
        <v-chip
          v-if="props.status"
          :color="statusChipColor"
          class="font-weight-bold ml-2"
          size="small"
          variant="flat"
        >
          {{ props.status }}
        </v-chip>
      </div>
    </template>

    <div class="d-flex flex-column flex-grow-1">
      <div v-if="props.description" class="text-body-medium text-medium-emphasis mb-4">
        {{ props.description }}
      </div>

      <div class="mt-auto">
        <div class="d-flex justify-space-between align-center mb-2">
          <span class="text-body-medium text-medium-emphasis">Disponivel</span>
          <span class="text-body-medium font-weight-bold">{{ props.available }} / {{ props.total }}</span>
        </div>
        <AppProgressBar
          :show-text="false"
          :total="props.total"
          :value="props.available"
          class="mb-3"
        />
        <v-row density="comfortable">
          <v-col v-if="!isBolsistaOrAdmin" cols="12">
            <div v-if="isUnavailable" class="text-error font-weight-bold text-center py-2">
              {{ unavailableMessage }}
            </div>
            <AppButton
              v-else
              block
              class="text-none"
              color="primary"
              elevation="0"
              @click="emit('request-quantity')"
            >
              {{ props.buttonText }}
            </AppButton>
          </v-col>
          <v-col v-else cols="12" class="d-flex ga-2" :class="{ 'justify-center': xs }">
            <template v-if="!isUnavailable">
              <v-tooltip v-if="xs" :text="props.buttonText" location="top">
                <template #activator="{ props: tooltipProps }">
                  <AppButton
                    v-bind="tooltipProps"
                    class="px-0 flex-shrink-0"
                    color="primary"
                    elevation="0"
                    style="min-width: 44px; width: 44px;"
                    @click="emit('request-quantity')"
                  >
                    <v-icon>mdi-cart-plus</v-icon>
                  </AppButton>
                </template>
              </v-tooltip>
              <AppButton
                v-else
                class="text-none flex-grow-1"
                color="primary"
                elevation="0"
                @click="emit('request-quantity')"
              >
                {{ props.buttonText }}
              </AppButton>
            </template>
            <div v-else class="text-error font-weight-bold text-center py-2 flex-grow-1">
              {{ unavailableMessage }}
            </div>

            <v-tooltip text="Editar Item" location="top">
              <template #activator="{ props: tooltipProps }">
                <AppButton
                  v-bind="tooltipProps"
                  class="px-0 flex-shrink-0"
                  color="primary"
                  elevation="0"
                  style="min-width: 44px; width: 44px;"
                  variant="outlined"
                  @click="emit('edit')"
                >
                  <v-icon>mdi-pencil</v-icon>
                </AppButton>
              </template>
            </v-tooltip>
            <v-tooltip :text="isBlocked ? 'Desbloquear Item' : 'Bloquear Item'" location="top">
              <template #activator="{ props: tooltipProps }">
                <AppButton
                  v-bind="tooltipProps"
                  class="px-0 flex-shrink-0"
                  :color="isBlocked ? 'success' : 'warning'"
                  elevation="0"
                  style="min-width: 44px; width: 44px;"
                  variant="outlined"
                  @click="emit('toggle-lock')"
                >
                  <v-icon>{{ isBlocked ? 'mdi-lock-open-variant' : 'mdi-lock' }}</v-icon>
                </AppButton>
              </template>
            </v-tooltip>
          </v-col>
        </v-row>
      </div>
    </div>
  </AppCard>
</template>
