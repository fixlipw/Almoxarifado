<script setup lang="ts">
  interface AppSelectProps {
    label?: string
      items: Array<{ label: string, value: any } | number>
    disabled?: boolean
    clearable?: boolean
  }

  const props = withDefaults(defineProps<AppSelectProps>(), {
    label: '',
    disabled: false,
    clearable: true,
  })

  const modelValue = defineModel<any>({ default: null })

  const emit = defineEmits<{
    change: [value: any]
  }>()

  function onChange () {
    emit('change', modelValue.value)
  }
</script>

<template>
  <v-select
    v-model="modelValue"
    :clearable="props.clearable"
    :disabled="props.disabled"
    density="compact"
    hide-details
    :items="props.items"
    :label="props.label"
    rounded="lg"
    variant="outlined"
    @update:model-value="onChange"
  />
</template>
