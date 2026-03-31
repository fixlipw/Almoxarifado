<script setup lang="ts">
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import AppPage from '@/components/ui/AppPage.vue'

  const nomes = [
    'Maria Silva',
  ]

  const itensExemplo = [
    { id: 1, name: 'Resistor 10kΩ', quantity: 2 },
    { id: 2, name: 'Protoboard 830 pontos', quantity: 1 },
    { id: 3, name: 'Kit Arduino Uno', quantity: 1 },
    { id: 4, name: 'Sensor ultrassônico', quantity: 1 },
    { id: 5, name: 'Alicate de corte', quantity: 1 },
  ]

  const emprestimos = Array.from({ length: 7 }, (_, index) => {
    const quantidadeItens = Math.floor(Math.random() * 3) + 1 // de 1 a 3 itens
    return {
      id: index + 1,
      items: itensExemplo.slice(0, quantidadeItens),
      nome: nomes[index % nomes.length],
      matricula: '12345678',
      devolvidoEm: '2026-03-30',
    }
  })
</script>

<template>
  <AppPage>
    <v-row class="mb-5" dense>
      <v-col
        v-for="emprestimo in emprestimos"
        :key="emprestimo.id"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <EmprestimoCard
          :items="emprestimo.items"
          items-label="Itens devolvidos"
          requested-at="2026-03-25"
          requested-at-label="Data da solicitação"
          returned-at="2026-03-30"
          returned-at-label="Data da devolução"
          status="Finalizado"
          :show-button="true"
          status-color="info"
          button-text="Ver Detalhes"
          :subtitle="`Matrícula: ${emprestimo.matricula}`"
          :title="`${emprestimo.nome}`"
        />
      </v-col>
    </v-row>
  </AppPage>
</template>
