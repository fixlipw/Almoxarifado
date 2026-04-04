<script setup lang="ts">
  import { useAttrs } from 'vue'

  defineOptions({
    inheritAttrs: false,
  })

  const attrs = useAttrs()
</script>

<template>
  <v-btn rounded="lg" variant="tonal" v-bind="attrs" class="px-5 py-2 text-body-medium font-weight-medium elevation-0">
    <slot />
  </v-btn>
</template>
