<template>
  <component :is="layout">
    <router-view />
  </component>
</template>

<script lang="ts" setup>
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  import LayoutBlank from '@/layouts/blank.vue'
  import LayoutDefault from '@/layouts/default.vue'

  const route = useRoute()
  const layout = computed(() => {
    return route.meta.layout === 'blank' ? LayoutBlank : LayoutDefault
  })
</script>
