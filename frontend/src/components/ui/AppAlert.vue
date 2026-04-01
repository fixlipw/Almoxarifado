<script lang="ts" setup>
  import type { AppAlertProps } from '@/types'

  import { computed } from 'vue'

  const props = withDefaults(defineProps<AppAlertProps>(), {
    tone: 'info',
    description: '',
    icon: '',
  })

  const fallbackIcons: Record<string, string> = {
    info: 'mdi-information-outline',
    warning: 'mdi-alert-outline',
    success: 'mdi-check-circle-outline',
  }

  const iconName = computed(() => props.icon || fallbackIcons[props.tone])
</script>

<template>
  <v-sheet
    border
    class="app-alert d-flex align-start ga-3 px-4 py-3"
    :class="`border-${tone}`"
    :color="`${tone}-light`"
    role="status"
    rounded="lg"
  >
    <v-icon class="align-self-center" :color="tone" :icon="iconName" size="20" />

    <div>
      <div class="text-title-medium font-weight-bold mb-1">
        {{ title }}
      </div>

      <div v-if="$slots.default || description" class="text-body-large text-medium-emphasis">
        <slot>
          {{ description }}
        </slot>
      </div>
    </div>
  </v-sheet>
</template>

<style scoped>
  .app-alert :deep(.v-icon) {
    margin-top: 2px;
  }
</style>
