<script setup lang="ts">
import { ref } from 'vue'
import AppPage from '@/components/ui/AppPage.vue'
import RelatorioCard from '@/components/relatorios/RelatorioCard.vue'

const reports = ref([
  {
    key: 'estoque',
    title: 'Estoque Completo',
    subtitle: 'Relação de todos os itens do almoxarifado',
    icon: 'mdi-archive',
  },
])
</script>

<template>
  <AppPage title="Relatórios" subtitle="Gere relatórios de empréstimos, devoluções e itens mais solicitados">
    <v-row density="comfortable">
      <template v-for="report in reports" :key="report.key">
        <v-col cols="12" md="4">
          <RelatorioCard
            :key-name="report.key"
            :title="report.title"
            :subtitle="report.subtitle"
            :icon="report.icon"
          />
        </v-col>
      </template>
    </v-row>
  </AppPage>
</template>