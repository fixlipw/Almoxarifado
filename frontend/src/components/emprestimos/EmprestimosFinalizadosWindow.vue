<script setup lang="ts">
  import type { EmprestimoVisual } from '@/types'
  import { onMounted, ref } from 'vue'
  import { api } from '@/api'
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

      // Pedidos finalizados
      const pedidosFinalizados = todosPedidos.filter(p => p.finalizado)

      emprestimos.value = await Promise.all(
        pedidosFinalizados.map(async (pedido): Promise<EmprestimoVisual> => {
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
            finalizadoEm: pedido.dataFinalizado?.split('T')[0] || '',
            email: solicitante?.email || '',
            curso: solicitante?.curso || '',
            tipo: solicitante?.acesso === 'ALUNO' ? 'Aluno' : 'Professor',
            status: pedido.aprovado === false ? 'Rejeitado' : 'Finalizado',
            statusColor: pedido.aprovado === false ? 'error' : 'grey',
            codigo: pedido.codigoPedido,
            observacoes: pedido.feedback,
            dataAtualizacao: pedido.dataAtualizacao?.split('T')[0] || '',
          }
        }),
      )
    } catch (error) {
      console.error('Erro ao buscar pedidos finalizados', error)
    }
  })

  function abrirDetalhes (emprestimo: EmprestimoVisual) {
    emprestimoSelecionado.value = emprestimo
    dialogAberto.value = true
  }
</script>

<template>
  <AppPage>
    <div class="h-100">
      <div v-if="emprestimos.length === 0" class="text-center text-medium-emphasis my-10">
        Nenhum empréstimo finalizado no momento.
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
            items-label="Itens do pedido"
            :requested-at="emprestimo.finalizadoEm || ''"
            requested-at-label="Data da devolução"
            :show-button="true"
            :status="emprestimo.status"
            :status-color="emprestimo.statusColor"
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
          dataAprovacao: emprestimoSelecionado.aprovadoEm ?? undefined,
          dataDevolucao: emprestimoSelecionado.finalizadoEm ?? undefined,
          dataAtualizacao: emprestimoSelecionado.dataAtualizacao,
          observacoes: emprestimoSelecionado.observacoes,
        }"
        @update:model-value="val => { dialogAberto = val; if (!val) emprestimoSelecionado = null }"
      />
    </div>
  </AppPage>
</template>
