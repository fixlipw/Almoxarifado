<script lang="ts" setup>
  import type { EmprestimoVisual } from '@/types'
  import { onMounted, ref } from 'vue'
  import { api } from '@/api'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import EmprestimoCard from '@/components/emprestimos/EmprestimoCard.vue'
  import EmprestimoDetalhesDialog from '@/components/emprestimos/EmprestimoDetalhesDialog.vue'

  const emprestimos = ref<EmprestimoVisual[]>([])
  const dialogAberto = ref(false)
  const emprestimoSelecionado = ref<EmprestimoVisual | null>(null)

  async function fetchPendentes () {
    try {
      const [todosPedidos, todosUsuarios, todoEstoque] = await Promise.all([
        api.getPedidos(),
        api.getUsuarios(),
        api.getEstoque(),
      ])

      // Pedidos pendentes: não aprovados e não finalizados (null é pending no nosso mock)
      const pedidosPendentes = todosPedidos.filter(p => (p.aprovado === null || p.aprovado === undefined) && !p.finalizado)

      emprestimos.value = await Promise.all(
        pedidosPendentes.map(async (pedido): Promise<EmprestimoVisual> => {
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
            aprovadoEm: null,
            email: solicitante?.email || '',
            curso: solicitante?.curso || '',
            tipo: solicitante?.acesso === 'ALUNO' ? 'Aluno' : 'Professor',
            status: 'Pendente',
            codigo: pedido.id,
            observacoes: pedido.feedback,
            dataAtualizacao: pedido.dataAtualizacao?.split('T')[0] || '',
          }
        }),
      )
    } catch (error) {
      console.error('Erro ao buscar pedidos pendentes', error)
    }
  }

  onMounted(() => {
    fetchPendentes()
  })

  function abrirDetalhes (emprestimo: EmprestimoVisual) {
    emprestimoSelecionado.value = emprestimo
    dialogAberto.value = true
  }

  async function apagarEmprestimo (id: string) {
    try {
      await api.deletePedido(id)
      await fetchPendentes()
    } catch (error) {
      console.error('Erro ao excluir pedido', error)
      alert('Erro ao excluir pedido')
    }
  }

  const confirmAction = ref(false)
  const isAcaoLoading = ref(false)
  const acaoAtual = ref<'approve' | 'reject' | 'return'>('approve')
  const tituloConfirmacao = ref('')
  const mensagemConfirmacao = ref('')

  function confirmarAcao (acao: 'approve' | 'reject' | 'return') {
    if (!emprestimoSelecionado.value) return
    acaoAtual.value = acao
    if (acao === 'approve') {
      tituloConfirmacao.value = 'Aprovar Pedido'
      mensagemConfirmacao.value = 'Tem certeza que deseja aprovar este pedido de empréstimo?'
    } else if (acao === 'reject') {
      tituloConfirmacao.value = 'Rejeitar Pedido'
      mensagemConfirmacao.value = 'Tem certeza que deseja rejeitar e excluir este pedido de empréstimo?'
    }
    confirmAction.value = true
  }

  async function executarAcao () {
    if (!emprestimoSelecionado.value) return
    isAcaoLoading.value = true
    try {
      if (acaoAtual.value === 'approve') {
        const agn = new Date().toISOString()
        await api.updatePedido(emprestimoSelecionado.value.codigo, {
          aprovado: true,
          dataAprovacao: agn,
          dataAtualizacao: agn,
        })
      } else if (acaoAtual.value === 'reject') {
        await api.deletePedido(emprestimoSelecionado.value.codigo)
      }
      confirmAction.value = false
      await fetchPendentes()
    } catch (error) {
      console.error('Erro ao executar ação', error)
      alert('Erro ao processar a ação')
    } finally {
      isAcaoLoading.value = false
      emprestimoSelecionado.value = null
    }
  }
</script>

<template>
  <div class="h-100">
    <div v-if="emprestimos.length === 0" class="text-center text-medium-emphasis my-10">
      Nenhum empréstimo pendente no momento.
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
          :codigo="emprestimo.id"
          :items="emprestimo.items"
          items-label="Itens solicitados"
          :requested-at="emprestimo.solicitadoEm"
          requested-at-label="Data da solicitação"
          :show-button="true"
          status="Pendente"
          status-color="warning"
          :subtitle="`Matrícula: ${emprestimo.matricula}`"
          :title="`${emprestimo.nome}`"
          @delete="apagarEmprestimo(emprestimo.id)"
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
      confirm-text="Confirmar"
      :is-loading="isAcaoLoading"
      :message="mensagemConfirmacao"
      :title="tituloConfirmacao"
      @cancel="confirmAction = false"
      @confirm="executarAcao"
    />
  </div>
</template>
