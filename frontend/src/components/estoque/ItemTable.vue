<script setup lang="ts">
import {computed} from 'vue'
import type {ItemCardProps} from './types'
import AppTable from '@/components/ui/AppTable.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppProgressBar from '@/components/ui/AppProgressBar.vue'
import {useAuthStore} from '@/stores/auth'

const props = defineProps<{
  items: ItemCardProps[]
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'edit', item: ItemCardProps): void
  (e: 'toggle-lock', item: ItemCardProps): void
  (e: 'request-quantity', item: ItemCardProps): void
}>()

const authStore = useAuthStore()
const isBolsistaOrAdmin = computed(() => authStore.userRole === 'BOLSISTA' || authStore.userRole === 'ADMIN')

const headers = [
  {key: 'info', title: 'Item'},
  {key: 'status', title: 'Status', width: '120px'},
  {key: 'availability', title: 'Disponibilidade', width: '200px'},
  {key: 'actions', title: 'Ações', align: 'end' as const, width: '160px'},
]
</script>

<template>
  <AppTable
      :headers="headers"
      :items="items"
      :loading="loading"
      empty-text="Nenhum item encontrado."
      item-key="id"
  >
    <!-- Item: nome + categoria -->
    <template #item.info="{ item }">
      <div class="d-flex align-center ga-3 py-2">
        <v-avatar :color="item.iconColor || 'primary'" rounded="lg" size="36" variant="tonal">
          <v-icon :color="item.iconColor || 'primary'" size="20">
            {{ item.icon || 'mdi-package-variant-closed' }}
          </v-icon>
        </v-avatar>
        <div>
          <div class="text-body-2 font-weight-bold" style="line-height: 1.2;">{{ item.title }}</div>
          <div v-if="item.category" class="text-caption text-medium-emphasis">{{ item.category }}</div>
        </div>
      </div>
    </template>

    <!-- Item: chip de status -->
    <template #item.status="{ item }">
      <v-chip
          :color="item.statusColor || 'success'"
          class="font-weight-bold"
          size="small"
          variant="flat"
      >
        {{ item.status }}
      </v-chip>
    </template>

    <!-- Item: barra de disponibilidade -->
    <template #item.availability="{ item }">
      <div style="min-width: 150px">
        <div class="d-flex justify-space-between align-center mb-1">
          <span class="text-caption text-medium-emphasis">Disponível</span>
          <span class="text-caption font-weight-bold">{{ item.available }} / {{ item.total }}</span>
        </div>
        <AppProgressBar
            :show-text="false"
            :total="item.total"
            :value="item.available"
            height="6"
        />
      </div>
    </template>

    <!-- Item: ações -->
    <template #item.actions="{ item }">
      <div class="d-flex align-center justify-end ga-2">

        <!-- Usuário comum -->
        <template v-if="!isBolsistaOrAdmin">
          <AppButton
              v-if="!item.isBlocked && item.available > 0"
              class="text-none"
              color="primary"
              size="small"
              @click="emit('request-quantity', item)"
          >
            {{ item.buttonText || 'Solicitar' }}
          </AppButton>
          <span v-else class="text-error text-caption font-weight-bold">
            {{ item.isBlocked ? 'Bloqueado' : 'Indisponível' }}
          </span>
        </template>

        <!-- Bolsista / Admin -->
        <template v-else>
          <v-tooltip location="top" text="Adicionar ao pedido">
            <template #activator="{ props: tooltipProps }">
              <AppButton
                  v-bind="tooltipProps"
                  :disabled="item.isBlocked || item.available === 0"
                  class="px-0"
                  color="primary"
                  style="min-width: 36px; width: 36px; height: 36px;"
                  variant="tonal"
                  @click="emit('request-quantity', item)"
              >
                <v-icon size="18">mdi-cart-plus</v-icon>
              </AppButton>
            </template>
          </v-tooltip>

          <v-tooltip location="top" text="Editar item">
            <template #activator="{ props: tooltipProps }">
              <AppButton
                  v-bind="tooltipProps"
                  class="px-0"
                  color="primary"
                  style="min-width: 36px; width: 36px; height: 36px;"
                  variant="outlined"
                  @click="emit('edit', item)"
              >
                <v-icon size="18">mdi-pencil</v-icon>
              </AppButton>
            </template>
          </v-tooltip>

          <v-tooltip :text="item.isBlocked ? 'Desbloquear item' : 'Bloquear item'" location="top">
            <template #activator="{ props: tooltipProps }">
              <AppButton
                  v-bind="tooltipProps"
                  :color="item.isBlocked ? 'success' : 'warning'"
                  class="px-0"
                  style="min-width: 36px; width: 36px; height: 36px;"
                  variant="outlined"
                  @click="emit('toggle-lock', item)"
              >
                <v-icon size="18">{{ item.isBlocked ? 'mdi-lock-open-variant' : 'mdi-lock' }}</v-icon>
              </AppButton>
            </template>
          </v-tooltip>
        </template>
      </div>
    </template>
  </AppTable>
</template>