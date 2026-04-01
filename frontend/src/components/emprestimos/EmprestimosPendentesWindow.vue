<script lang="ts" setup>
  import { ref } from 'vue'
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import EmprestimoDetalhesDialog from '@/components/emprestimos/EmprestimoDetalhesDialog.vue'
  import AppAlert from '@/components/ui/AppAlert.vue'
  import AppPage from '@/components/ui/AppPage.vue'

  type Item = {
    id: number
    name: string
    quantity: number
  }

  const nomes = [
    'Maria Silva',
  ]

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
    { id: 11, name: 'Resistor 1kΩ', quantity: 3 },
    { id: 12, name: 'Resistor 100Ω', quantity: 2 },
    { id: 13, name: 'Resistor 470Ω', quantity: 1 },
    { id: 14, name: 'Resistor 22kΩ', quantity: 2 },
    { id: 15, name: 'Capacitor 100µF', quantity: 2 },
    { id: 16, name: 'Capacitor 10µF', quantity: 3 },
    { id: 17, name: 'LED Vermelho 5mm', quantity: 5 },
    { id: 18, name: 'LED Verde 5mm', quantity: 5 },
    { id: 19, name: 'Transistor 2N2222', quantity: 1 },
    { id: 20, name: 'Diodo 1N4007', quantity: 3 },
  ]

  const emprestimos = Array.from({ length: 7 }, (_, index) => {
    const diasAtras = Math.floor(Math.random() * 10) + 1 // 1 a 10 dias atrás
    const dataPendente = new Date(2026, 3, 1 - diasAtras).toISOString().split('T')[0]
    const nomeUsuario = nomes[0]

    return {
      id: index + 1,
      items: itensDisponiveis,
      nome: nomeUsuario,
      matricula: `202${Math.floor(Math.random() * 9)}${Math.floor(Math.random() * 100_000).toString().padStart(5, '0')}`,
      solicitadoEm: dataPendente,
      email: nomeUsuario.toLowerCase().replace(' ', '.') + '@alu.ufc.br',
      curso: 'Ciência da Computação',
      tipo: 'Aluno',
      status: 'Ativo',
      codigo: `#emp${index + 1}`,
      observacoes: 'Necessário para projeto da disciplina de Sistemas Embarcados',
    }
  })

  const dialogAberto = ref(false)
  const emprestimoSelecionado = ref<any | null>(null)

  function abrirDetalhes (emprestimo: any) {
    emprestimoSelecionado.value = emprestimo
    dialogAberto.value = true
  }
</script>

<template>
  <AppPage>
    <AppAlert
      description="Você será notificado quando suas solicitações forem processadas"
      icon="mdi-timer-sand"
      title="Aguardando aprovação do administrador"
      tone="warning"
    />

    <v-row class="mt-5 mb-5" density="comfortable">
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
          button-text="Ver Detalhes"
          :items="emprestimo.items"
          items-label="Itens solicitados"
          :requested-at="emprestimo.solicitadoEm"
          requested-at-label="Data da solicitação"
          :show-button="true"
          status="Pendente"
          status-color="warning"
          :subtitle="`Matrícula: ${emprestimo.matricula}`"
          :title="`${emprestimo.nome}`"
          @details="abrirDetalhes(emprestimo)"
        />
      </v-col>
    </v-row>

    <EmprestimoDetalhesDialog
      v-if="emprestimoSelecionado"
      v-model="dialogAberto"
      :emprestimo="{
        codigo: emprestimoSelecionado.codigo,
        status: emprestimoSelecionado.status,
        solicitante: {
          nome: emprestimoSelecionado.nome,
          email: emprestimoSelecionado.email,
          matricula: emprestimoSelecionado.matricula,
          curso: emprestimoSelecionado.curso,
          tipo: emprestimoSelecionado.tipo,
        },
        itens: emprestimoSelecionado.items,
        dataSolicitacao: emprestimoSelecionado.solicitadoEm,
        observacoes: emprestimoSelecionado.observacoes,
      }"
      @update:model-value="val => { dialogAberto = val; if (!val) emprestimoSelecionado.value = null }"
    />
  </AppPage>
</template>
