<script setup lang="ts">
  import { computed } from 'vue'
  import type {AppProgressBarProps} from "@/components/ui/types.ts";

  const props = withDefaults(defineProps<AppProgressBarProps>(), {
    label: '',
    showText: true,
    valueText: '',
    height: 8,
    bgColor: 'grey-lighten-3',
  })

  const progressValue = computed(() => {
    const total = Number(props.total ?? 0)
    const value = Number(props.value ?? 0)
    if (!isFinite(total) || total <= 0) return 0
    const pct = (value / total) * 100
    if (!isFinite(pct)) return 0
    return Math.min(100, Math.max(0, pct))
  })

  const progressColor = computed(() => {
    if (props.color && String(props.color).trim()) return props.color

    const total = Number(props.total ?? 0)
    const value = Number(props.value ?? 0)
    if (!isFinite(total) || total <= 0) return 'grey'

    const ratio = value / total
    if (!isFinite(ratio)) return 'grey'
    if (ratio <= 0.2) return 'error'
    if (ratio <= 0.5) return 'warning'
    return 'success'
  })

  const progressText = computed(() => {
    const total = Number(props.total ?? 0)
    const value = Number(props.value ?? 0)
    if (props.valueText) return props.valueText
    if (props.textFormat) return props.textFormat(value, total)
    return `${value} / ${total}`
  })
</script>

<template>
  <div class="w-100">
    <div v-if="props.label || props.showText" class="d-flex align-center justify-space-between mb-1">
      <span v-if="props.label" class="text-body-medium text-grey-darken-1">{{ props.label }}</span>
      <span v-if="props.showText" class="text-body-medium font-weight-medium">{{ progressText }}</span>
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
