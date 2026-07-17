<template>
  <TooltipProvider>
    <component :is="layout">
    <router-view/>
    <div class="fixed right-4 top-4 z-[100] flex w-[min(24rem,calc(100vw-2rem))] flex-col gap-2">
      <div
          v-for="notification in store.notifications"
          :key="notification.id"
          class="flex items-start gap-3 rounded-lg border bg-background p-4 shadow-lg"
      >
        <component :is="notificationIcon(notification.color)" :class="notificationColor(notification.color)"
                   class="mt-0.5 size-4 shrink-0"/>
        <p class="flex-1 text-sm">{{ notification.message }}</p>
        <button aria-label="Fechar aviso" class="rounded p-1 text-muted-foreground hover:bg-muted"
                @click="store.removeNotification(notification.id)">
          <X class="size-4"/>
        </button>
      </div>
    </div>
    </component>
  </TooltipProvider>
</template>

<script lang="ts" setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import {AlertCircle, CheckCircle2, Info, TriangleAlert, X} from 'lucide-vue-next'
import LayoutBlank from '@/layouts/BlankLayout.vue'
import LayoutDefault from '@/layouts/DefaultLayout.vue'
import {type Notification, useNotificationStore} from '@/stores/notifications'
import {TooltipProvider} from '@/components/ui/tooltip'

const route = useRoute()
const layout = computed(() => route.meta.layout === 'blank' ? LayoutBlank : LayoutDefault)
const store = useNotificationStore()
const notificationIcon = (color: Notification['color']) => ({
  success: CheckCircle2, error: AlertCircle, warning: TriangleAlert, info: Info,
}[color])
const notificationColor = (color: Notification['color']) => ({
  success: 'text-emerald-600', error: 'text-destructive', warning: 'text-amber-600', info: 'text-sky-600',
}[color])
</script>
