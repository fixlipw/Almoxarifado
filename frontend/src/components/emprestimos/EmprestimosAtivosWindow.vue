<script setup lang="ts">
  import type { EmprestimoVisual } from '@/types'
  import { onMounted, ref } from 'vue'
  import { api } from '@/api'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import EmprestimoDetalhesDialog from '@/components/emprestimos/EmprestimoDetalhesDialog.vue'
  import AppPage from '@/components/ui/AppPage.vue'

  const emprestimos = ref<EmprestimoVisual[]>([])
  const dialogAberto = ref(false)
  const emprestimoSelecionado = ref<EmprestimoVisual | null>(null)

  onMounted(async () => {
    try {
      const [todosPedidos, todosUsuarios, todoEstoque] = await Promise.all([
        api.getPedidos(),
        api.getUsuarios(),
        api.getEstoque(),
      ])

      // Filtra apenas pedidos ativos (aprovados e ainda não finalizados)
      const pedidosAtivos = todosPedidos.filter(p => p.aprovado === true && !p.finalizado)

      // Monta os dados para o componente
      emprestimos.value = await Promise.all(
        pedidosAtivos.map(async (pedido): Promise<EmprestimoVisual> => {
          const solicitante = todosUsuarios.find(u => u.id === pedido.solicitanteId)
          const itens = await api.getItensPedido(pedido.id)

          return {
            id: pedido.id,
            items: itens.map(ip => {
              const est = todoEstoque.find(e => e.id === ip.estoqueId)
              return { id: ip.id, name: est?.nome || ip.estoqueId, quantity: ip.quantidadeItem }
            }),
            nome: solicitante?.nome + ' ' + (solicitante?.sobrenome || ''),
            matricula: solicitante?.matricula || '',
            solicitadoEm: pedido.dataSolicitacao.split('T')[0],
            aprovadoEm: pedido.dataAprovacao?.split('T')[0] || '',
            email: solicitante?.email || '',
            curso: solicitante?.curso || '',
            tipo: solicitante?.acesso === 'ALUNO' ? 'Aluno' : 'Professor',
            status: 'Ativo',
            codigo: pedido.codigoPedido,
            observacoes: pedido.feedback,
            dataAtualizacao: pedido.dataAtualizacao?.split('T')[0] || '',
          }
        }),
      )
    } catch (error) {
      console.error('Erro ao buscar pedidos ativos', error)
    }
  })

  function abrirDetalhes (emprestimo: EmprestimoVisual) {
    emprestimoSelecionado.value = emprestimo
    dialogAberto.value = true
  }

  const confirmAction = ref(false)
  const isAcaoLoading = ref(false)
  const tituloConfirmacao = ref('')
  const mensagemConfirmacao = ref('')

  function confirmarAcao (acao: 'approve' | 'reject' | 'return') {
    if (!emprestimoSelecionado.value) return
    if (acao === 'return') {
      tituloConfirmacao.value = 'Registrar Devolução'
      mensagemConfirmacao.value = 'Tem certeza que deseja registrar a devolução de todos os itens e finalizar o empréstimo?'
      confirmAction.value = true
    }
  }

  async function fetchAtivos () {
    try {
      const [todosPedidos, todosUsuarios, todoEstoque] = await Promise.all([
        api.getPedidos(),
        api.getUsuarios(),
        api.getEstoque(),
      ])

      const pedidosAtivos = todosPedidos.filter(p => p.aprovado === true && !p.finalizado)

      emprestimos.value = await Promise.all(
        pedidosAtivos.map(async (pedido): Promise<EmprestimoVisual> => {
          const solicitante = todosUsuarios.find(u => u.id === pedido.solicitanteId)
          const itens = await api.getItensPedido(pedido.id)

          return {
            id: pedido.id,
            items: itens.map(ip => {
              const est = todoEstoque.find(e => e.id === ip.estoqueId)
              return { id: ip.estoqueId, name: est?.nome || ip.estoqueId, quantity: ip.quantidadeItem }
            }),
            nome: solicitante?.nome + ' ' + (solicitante?.sobrenome || ''),
            matricula: solicitante?.matricula || '',
            solicitadoEm: pedido.dataSolicitacao.split('T')[0],
            aprovadoEm: pedido.dataAprovacao?.split('T')[0] || '',
            email: solicitante?.email || '',
            curso: solicitante?.curso || '',
            tipo: solicitante?.acesso === 'ALUNO' ? 'Aluno' : 'Professor',
            status: 'Ativo',
            codigo: pedido.id,
            observacoes: pedido.feedback,
            dataAtualizacao: pedido.dataAtualizacao?.split('T')[0] || '',
          }
        }),
      )
    } catch (error) {
      console.error('Erro ao buscar pedidos ativos', error)
    }
  }

  onMounted(async () => {
    await fetchAtivos()
  })

  async function executarDevolucao () {
    if (!emprestimoSelecionado.value) return
    isAcaoLoading.value = true
    try {
      const agn = new Date().toISOString()
      await api.updatePedido(emprestimoSelecionado.value.codigo, {
        finalizado: true,
        dataFinalizado: agn,
        dataAtualizacao: agn,
      })
      confirmAction.value = false
      await fetchAtivos()
    } catch (error) {
      console.error('Erro ao registrar devolucao', error)
      alert('Erro ao processar devolução')
    } finally {
      isAcaoLoading.value = false
      emprestimoSelecionado.value = null
    }
  }
</script>

<template>
  <AppPage>
    <div v-if="emprestimos.length === 0" class="text-center text-medium-emphasis my-10">
      Nenhum empréstimo ativo no momento.
    </div>

    <v-row v-else class="mb-5" density="comfortable">
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
          items-label="Itens emprestados"
          :requested-at="emprestimo.solicitadoEm"
          requested-at-label="Data da solicitação"
          :show-button="true"
          status="Ativo"
          status-color="success"
          :subtitle="`Matrícula: ${emprestimo.matricula}`"
          :title="emprestimo.nome"
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
        itens: emprestimoSelecionado.items.map(i => ({
          id: i.id,
          estoqueId: i.id.toString(),
          nomeItem: i.name,
          quantidadeItem: i.quantity
        })),
        dataSolicitacao: emprestimoSelecionado.solicitadoEm,
        dataAtualizacao: emprestimoSelecionado.dataAtualizacao,
        observacoes: emprestimoSelecionado.observacoes,
      }"
      @action="confirmarAcao"
      @update:model-value="val => { dialogAberto = val; if (!val) emprestimoSelecionado = null }"
    />

    <ConfirmDialog
      v-model="confirmAction"
      cancel-text="Cancelar"
      confirm-text="Confirmar Devolução"
      :is-loading="isAcaoLoading"
      :message="mensagemConfirmacao"
      :title="tituloConfirmacao"
      @cancel="confirmAction = false"
      @confirm="executarDevolucao"
    />
  </AppPage>
</template>
