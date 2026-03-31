<script lang="ts" setup>
  import { ref } from 'vue'

  import EmprestimosAtivosWindow from '@/components/emprestimos/EmprestimosAtivosWindow.vue'
  import EmprestimosFinalizadosWindow from '@/components/emprestimos/EmprestimosFinalizadosWindow.vue'
  import EmprestimosPendentesWindow from '@/components/emprestimos/EmprestimosPendentesWindow.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppTabs from '@/components/ui/AppTabs.vue'

  const abaAtual = ref('ativos')

  const abas = [
    { label: 'Ativos', value: 'ativos' },
    { label: 'Pendentes', value: 'pendentes', badge: 1 },
    { label: 'Finalizados', value: 'finalizados' },
  ]
</script>

<template>
  <AppPage subtitle="Acompanhe suas solicitações e devoluções" title="Meus Empréstimos">
    <template #actions>
      <AppButton
        class="text-none"
        color="primary"
        prepend-icon="mdi-plus"
        rounded="md"
        to="/inventario"
        variant="flat"
      >
        Nova Solicitação
      </AppButton>
    </template>

    <AppTabs v-model="abaAtual" class="mb-4" :items="abas">
      <template #panel="{ value }">
        <v-sheet class="pa-4" rounded="lg">
          <EmprestimosAtivosWindow v-if="value === 'ativos'" />
          <EmprestimosPendentesWindow v-else-if="value === 'pendentes'" />
          <EmprestimosFinalizadosWindow v-else-if="value === 'finalizados'" />
        </v-sheet>
      </template>
    </AppTabs>
  </AppPage>
</template>
