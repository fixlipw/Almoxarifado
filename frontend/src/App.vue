<template>
  <component :is="layout">
    <router-view />
    <div class="app-snackbar-container">
      <v-snackbar
        v-for="(notification, index) in store.notifications"
        :key="notification.id"
        :model-value="true"
        :color="notification.color"
        location="top right"
        :style="{ marginTop: `${index * 60}px` }"
        variant="flat"
        timeout="-1"
        @update:model-value="(val: any) => !val && store.removeNotification(notification.id)"
      >
        <div class="d-flex align-center ga-2">
          <span>{{ notification.message }}</span>
        </div>
        <template #actions>
          <v-btn icon="mdi-close" variant="text" size="small" @click="store.removeNotification(notification.id)"></v-btn>
        </template>
      </v-snackbar>
    </div>
  </component>
</template>

<script lang="ts" setup>
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  import LayoutBlank from '@/layouts/BlankLayout.vue'
  import LayoutDefault from '@/layouts/DefaultLayout.vue'
  import { useNotificationStore } from '@/stores/notifications'

  const route = useRoute()
  const layout = computed(() => {
    return route.meta.layout === 'blank' ? LayoutBlank : LayoutDefault
  })
  const store = useNotificationStore()
</script>

<style scoped>
.app-snackbar-container {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 9999;
  pointer-events: none;
}
.app-snackbar-container > * {
  pointer-events: auto;
}
</style>
