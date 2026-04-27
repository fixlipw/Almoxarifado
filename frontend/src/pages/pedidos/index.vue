<script lang="ts" setup>
  import { ref } from 'vue'

  import AppButton from '@/components/ui/AppButton.vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppTabs from '@/components/ui/AppTabs.vue'
  import PedidosAtivosWindow from "@/components/pedido/window/PedidosAtivosWindow.vue";
  import PedidosPendentesWindow from "@/components/pedido/window/PedidosPendentesWindow.vue";
  import PedidosFinalizadosWindow from "@/components/pedido/window/PedidosFinalizadosWindow.vue";
  import PedidosAtrasadosWindow from "@/components/pedido/window/PedidosAtrasadosWindow.vue";
  
  const abaAtual = ref('ativos')

  const abas = [
    { label: 'Ativos', value: 'ativos' },
    { label: 'Pendentes', value: 'pendentes' },
    { label: 'Finalizados', value: 'finalizados' },
    { label: 'Atrasados', value: 'atrasados' },
  ]
</script>

<template>
  <AppPage subtitle="Acompanhe suas solicitações e devoluções" title="Meus Empréstimos">
    <template #actions>
      <div class="d-flex align-center">
        <AppButton
          class="text-none mr-2"
          color="primary"
          prepend-icon="mdi-plus"
          rounded="md"
          to="/estoque"
          variant="flat"
        >
          Solicitar Itens
        </AppButton>
      </div>
    </template>

    <AppTabs v-model="abaAtual" class="mb-4" :items="abas">
      <template #panel="{ value }">
        <v-sheet class="pa-4" rounded="lg">
          <PedidosAtivosWindow v-if="value === 'ativos'" />
          <PedidosPendentesWindow v-else-if="value === 'pendentes'" />
          <PedidosFinalizadosWindow v-else-if="value === 'finalizados'" />
          <PedidosAtrasadosWindow v-else-if="value === 'atrasados'" />
        </v-sheet>
      </template>
    </AppTabs>
  </AppPage>
</template>
