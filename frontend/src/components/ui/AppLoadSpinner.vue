<script setup lang="ts">
  import type { AppLoadSpinnerProps } from '@/components/ui/types'

  const props = withDefaults(defineProps<AppLoadSpinnerProps>(), {
    label: 'Carregando dados...',
    color: 'primary',
    size: 64,
    width: 5,
    minHeight: '40vh',
  })
</script>

<template>
  <div
    class="app-load-spinner d-flex flex-column align-center justify-center text-center w-100"
    :style="{ minHeight: props.minHeight }"
  >
    <v-progress-circular
      :color="props.color"
      indeterminate
      :size="props.size"
      :width="props.width"
    />

    <p v-if="props.label" class="text-body-medium text-medium-emphasis mt-4 mb-0">
      {{ props.label }}
    </p>
  </div>
</template>

<style scoped>
  .app-load-spinner {
    gap: 0;
  }
</style>
