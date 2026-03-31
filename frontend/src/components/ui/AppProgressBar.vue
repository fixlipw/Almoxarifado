<script setup lang="ts">
  import { computed } from 'vue'

  interface AppProgressBarProps {
    total: number
    value: number
    color?: string
    label?: string
    showText?: boolean
    valueText?: string
    height?: number
    bgColor?: string
  }

  const props = withDefaults(defineProps<AppProgressBarProps>(), {
    label: '',
    showText: true,
    valueText: '',
    height: 8,
    bgColor: 'grey-lighten-3',
  })

  const progressValue = computed(() => {
    if (props.total === 0) return 0
    const value = (props.value / props.total) * 100
    return Math.min(100, Math.max(0, value))
  })

  const progressColor = computed(() => {
    if (props.color?.trim()) return props.color
    if (props.total <= 0) return 'grey'

    const ratio = props.value / props.total
    if (ratio <= 0.2) return 'error'
    if (ratio <= 0.5) return 'warning'
    return 'success'
  })

  const progressText = computed(() => {
    if (props.valueText) {
      return props.valueText
    }
    return `${props.value} / ${props.total}`
  })
</script>

<template>
  <div class="w-100">
    <div v-if="props.label || props.showText" class="d-flex align-center justify-space-between mb-1">
      <span v-if="props.label" class="text-body-2 text-grey-darken-1">{{ props.label }}</span>
      <span v-if="props.showText" class="text-body-2 font-weight-medium">{{ progressText }}</span>
    </div>

    <v-progress-linear
      :bg-color="props.bgColor"
      :color="progressColor"
      :height="props.height"
      :model-value="progressValue"
      rounded
    />
  </div>
</template>
