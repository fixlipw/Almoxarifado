<script setup lang="ts">
  import { useAttrs } from 'vue'

  defineOptions({
    inheritAttrs: false,
  })

  const attrs = useAttrs()
</script>

<template>
  <v-btn rounded="lg" variant="tonal" v-bind="attrs">
    <slot />
  </v-btn>
</template>
