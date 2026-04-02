<script setup lang="ts">
  import { toRefs } from 'vue'
  import type { AppSearchBarProps } from '@/components/ui/types.ts'

  const props = withDefaults(defineProps<AppSearchBarProps>(), {
    placeholder: 'Pesquisar...',
    clearable: true,
    disabled: false,
    loading: false,
  })

  const { loading, clearable, disabled, placeholder } = toRefs(props)

  const modelValue = defineModel<string>({ default: '' })

  const emit = defineEmits<{
    search: [value: string]
    clear: []
  }>()

  function onSearch () {
    emit('search', modelValue.value)
  }

  function onClear () {
    modelValue.value = ''
    emit('clear')
  }
</script>

<template>
  <v-text-field
    v-model="modelValue"
    :clearable="clearable"
    :disabled="disabled"
    hide-details
    :loading="loading"
    :placeholder="placeholder"
    prepend-inner-icon="mdi-magnify"
    rounded="lg"
    variant="outlined"
    @click:clear="onClear"
    @keydown.enter="onSearch"
  />
</template>
