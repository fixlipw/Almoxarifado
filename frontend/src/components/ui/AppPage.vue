<template>
  <v-container class="pa-5 h-90 overflow-hidden" fluid>
    <AppCard
      v-if="subtitle || title"
      card-class="w-100"
      content-class="pa-4"
      :subtitle="subtitle"
      :title="title"
    >
      <template v-if="!isEmpty && !loading" #button>
        <slot name="actions" />
      </template>
    </AppCard>
    <main class="mt-2">
      <template v-if="loading">
        <AppLoadSpinner :label="loadingText" />
      </template>

      <template v-else-if="!isEmpty">
        <slot />
      </template>

      <v-empty-state
        v-else
        :icon="icon"
        size="80"
        :text="emptyText"
        :title="emptyTitle"
      >
        <template #actions>
          <slot name="actions" />
        </template>
      </v-empty-state>
    </main>
  </v-container>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  import AppCard from '@/components/ui/AppCard.vue'
  import AppLoadSpinner from '@/components/ui/AppLoadSpinner.vue'

  const props = defineProps({
    title: { type: String, required: false, default: '' },
    subtitle: { type: String, required: false, default: '' },
    saudacao: { type: String, required: false, default: '' },
    loading: { type: Boolean, default: false },
    loadingText: { type: String, default: 'Carregando dados...' },
    isEmpty: { type: Boolean, default: false },
    emptyTitle: { type: String, default: 'Nenhum registro encontrado' },
    emptyText: { type: String, default: 'Clique no botão abaixo para realizar o cadastro' },
    icon: { type: String, default: 'mdi-magnify' },
  })

  const route = useRoute()
  const title = computed(() => {
    if (props.title) {
      return props.title
    }

    return typeof route.meta.title === 'string' ? route.meta.title : ''
  })
</script>
