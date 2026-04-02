<script setup lang="ts">
  import type {ItemCardProps} from "@/components/estoque/types.ts";

  import { computed } from 'vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'
  import AppProgressBar from '@/components/ui/AppProgressBar.vue'

  const props = withDefaults(defineProps<ItemCardProps>(), {
    category: '',
    description: '',
    status: 'Ativo',
    statusColor: 'success',
    icon: 'mdi-package-variant-closed',
    iconColor: 'primary',
    buttonText: 'Adicionar ao Pedido',
  })

  const emit = defineEmits<{
    (e: 'action' | 'add-to-cart' | 'request-quantity'): void
  }>()

  const isOutOfStock = computed(() => props.available === 0)

  const statusChipColor = computed(() => {
    return props.statusColor.trim() ? props.statusColor : 'success'
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
          class="font-weight-bold ml-2"
          :color="statusChipColor"
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
          <span class="text-body-medium text-medium-emphasis">Disponível</span>
          <span class="text-body-medium font-weight-bold">{{ props.available }} / {{ props.total }}</span>
        </div>
        <AppProgressBar
          class="mb-3"
          :show-text="false"
          :total="props.total"
          :value="props.available"
        />
        <div
          v-if="isOutOfStock"
          class="text-error font-weight-bold text-center py-2"
        >
          Indisponível no momento
        </div>
        <AppButton
          v-else-if="props.buttonText"
          block
          class="text-none"
          color="primary"
          elevation="0"
          @click="emit('request-quantity')"
        >
          {{ props.buttonText }}
        </AppButton>
      </div>
    </div>
  </AppCard>
</template>
