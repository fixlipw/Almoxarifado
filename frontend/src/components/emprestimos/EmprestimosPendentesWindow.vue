<script lang="ts" setup>
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import AppAlert from '@/components/ui/AppAlert.vue'
  import AppPage from '@/components/ui/AppPage.vue'

  type Item = {
    id: number
    name: string
    quantity: number
  }

  const itensDisponiveis: Item[] = [
    { id: 1, name: 'Resistor 10kΩ', quantity: 1 },
    { id: 2, name: 'Multímetro Digital', quantity: 1 },
    { id: 3, name: 'Protoboard 830 pontos', quantity: 1 },
    { id: 4, name: 'Fonte de bancada', quantity: 1 },
    { id: 5, name: 'Osciloscópio', quantity: 1 },
    { id: 6, name: 'Kit Arduino Uno', quantity: 1 },
    { id: 7, name: 'Sensor ultrassônico', quantity: 2 },
    { id: 8, name: 'Módulo relé 5V', quantity: 2 },
    { id: 9, name: 'Jumpers macho/fêmea', quantity: 10 },
    { id: 10, name: 'Alicate de corte', quantity: 1 },
  ]

  const embaralhar = <T>(lista: T[]): T[] =>
    [...lista].sort(() => Math.random() - 0.5)

    const emprestimos = Array.from({ length: 7 }, (_, index) => {
      const quantidadeItens = Math.floor(Math.random() * 4) + 1 // de 1 a 4 itens
      return {
        id: index + 1,
        items: embaralhar(itensDisponiveis).slice(0, quantidadeItens),
      }
    })
</script>

<template>
  <AppPage>
    <AppAlert
      description="Você será notificado quando suas solicitações forem processadas"
      icon="mdi-timer-sand"
      title="Aguardando aprovação do administrador"
      tone="warning"
    />

    <v-row class="mt-5 mb-5" dense>
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
          items-label="Itens solicitados"
          requested-at="2026-04-01"
          requested-at-label="Data da solicitação"
          status="Pendente"
          status-color="warning"
          subtitle="Matrícula: 12345678"
          :title="`Maria Silva`"
        />
      </v-col>
    </v-row>
  </AppPage>
</template>
