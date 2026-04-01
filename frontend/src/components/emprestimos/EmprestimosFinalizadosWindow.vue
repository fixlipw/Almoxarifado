<script setup lang="ts">
  import { ref } from 'vue'
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import EmprestimoDetalhesDialog from '@/components/emprestimos/EmprestimoDetalhesDialog.vue'
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
    { id: 6, name: 'Resistor 1kΩ', quantity: 3 },
    { id: 7, name: 'Resistor 100Ω', quantity: 2 },
    { id: 8, name: 'Resistor 470Ω', quantity: 1 },
    { id: 9, name: 'Capacitor 100µF', quantity: 2 },
    { id: 10, name: 'LED Vermelho 5mm', quantity: 5 },
    { id: 11, name: 'Transistor 2N2222', quantity: 1 },
    { id: 12, name: 'Diodo 1N4007', quantity: 3 },
  ]

  const emprestimos = Array.from({ length: 7 }, (_, index) => {
    const diasAtras = Math.floor(Math.random() * 30) + 5 // 5 a 35 dias atrás
    const diasAprovacao = Math.floor(Math.random() * 3) + 1 // 1 a 3 dias após a solicitação
    const diasDevolucao = Math.floor(Math.random() * 7) + 2 // 2 a 8 dias após a aprovação
    const dataSolicitacao = new Date(2026, 3, 1 - diasAtras)
    const dataAprovacao = new Date(dataSolicitacao.getTime() + diasAprovacao * 24 * 60 * 60 * 1000)
    const dataDevolucao = new Date(dataAprovacao.getTime() + diasDevolucao * 24 * 60 * 60 * 1000)
    const nomeUsuario = nomes[0]

    return {
      id: index + 1,
      items: itensExemplo,
      nome: nomeUsuario,
      matricula: `202${Math.floor(Math.random() * 9)}${Math.floor(Math.random() * 100_000).toString().padStart(5, '0')}`,
      solicitadoEm: dataSolicitacao.toISOString().split('T')[0],
      aprovadoEm: dataAprovacao.toISOString().split('T')[0],
      devolvidaEm: dataDevolucao.toISOString().split('T')[0],
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
    <v-row class="mb-5" density="comfortable">
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
          :codigo="emprestimo.codigo"
          :items="emprestimo.items"
          items-label="Itens devolvidos"
          :requested-at="emprestimo.solicitadoEm"
          requested-at-label="Data da solicitação"
          :returned-at="emprestimo.devolvidaEm"
          returned-at-label="Data da devolução"
          :show-button="true"
          status="Finalizado"
          status-color="info"
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
        dataAprovacao: emprestimoSelecionado.aprovadoEm,
        dataDevolucao: emprestimoSelecionado.devolvidaEm,
        observacoes: emprestimoSelecionado.observacoes,
      }"
      @update:model-value="val => { dialogAberto = val; if (!val) emprestimoSelecionado.value = null }"
    />
  </AppPage>
</template>
