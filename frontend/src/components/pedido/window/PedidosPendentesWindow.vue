<script setup lang="ts">

import AppPage from "@/components/ui/AppPage.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/window/types.ts";
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import { mapearPedidoDetalhes, mapearParaPedidoVisual } from "@/components/pedido/window/utils.ts";
import { getPedidosPendentes, deletePedido, approvePedido, rejectPedido } from "@/services/pedidos.ts";
import { useCartStore } from "@/stores/cart.ts";
import { useNotificationStore } from "@/stores/notifications.ts";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth.ts";

  const pedidos = ref<any[]>([])
  const dialogAberto = ref(false)
  const pedidoSelecionado = ref<PedidoVisualProps | null>(null)
  const isLoading = ref(false)

  const cartStore = useCartStore()
  const router = useRouter()
  const notifications = useNotificationStore()
  const authStore = useAuthStore()

  const pedidoDetalhesSelecionado = computed(() => {
    if (!pedidoSelecionado.value) return null
    return mapearPedidoDetalhes(pedidoSelecionado.value)
  })

  function abrirDetalhes (pedido: PedidoVisualProps) {
    pedidoSelecionado.value = pedido
    dialogAberto.value = true
  }

  const confirmarAcaoConfirmar = ref(false)
  const isAcaoLoading = ref(false)
  const tituloConfirmacao = ref('')
  const mensagemConfirmacao = ref('')
  const acaoPendente = ref<'approve' | 'reject' | null>(null)

  async function carregarPedidos () {
    isLoading.value = true
    try {
      const userId = authStore.userRole === 'ALUNO' ? authStore.session?.usuario?.id : undefined
      const data = await getPedidosPendentes(userId)
      pedidos.value = data.map(mapearParaPedidoVisual)
    } catch (e) {
      console.error(e)
    } finally {
      isLoading.value = false
    }
  }

  onMounted(carregarPedidos)

  watch(() => cartStore.checkedOut, async checkedOut => {
    if (!checkedOut) return
    await carregarPedidos()
    cartStore.setCheckout(false)
  })

  function confirmarAcaoDetalhes (acao: 'approve' | 'reject' | 'return') {
    if (!pedidoSelecionado.value) return
    if (acao === 'approve') {
      tituloConfirmacao.value = 'Aprovar Solicitação'
      mensagemConfirmacao.value = 'Tem certeza que deseja aprovar este pedido de empréstimo?'
      acaoPendente.value = 'approve'
      confirmarAcaoConfirmar.value = true
    } else if (acao === 'reject') {
      tituloConfirmacao.value = 'Rejeitar Solicitação'
      mensagemConfirmacao.value = 'Tem certeza que deseja rejeitar este pedido de empréstimo?'
      acaoPendente.value = 'reject'
      confirmarAcaoConfirmar.value = true
    }
  }

  async function executarAcao () {
    if (!pedidoSelecionado.value || !acaoPendente.value) return
    const userId = authStore.session?.usuario?.id
    if (!userId) {
      notifications.error('Usuário não autenticado.')
      return
    }

    isAcaoLoading.value = true
    try {
      const isApprove = acaoPendente.value === 'approve'
      if (isApprove) {
        await approvePedido(pedidoSelecionado.value.id)
        notifications.success('Pedido aprovado com sucesso!')
      } else {
        await rejectPedido(pedidoSelecionado.value.id)
        notifications.success('Pedido rejeitado com sucesso!')
      }

      confirmarAcaoConfirmar.value = false
      dialogAberto.value = false
      pedidoSelecionado.value = null
      acaoPendente.value = null

      window.location.reload()
    } catch (e) {
      console.error('Erro ao executar ação no pedido:', e)
      notifications.error('Ocorreu um erro ao processar a solicitação.')
    } finally {
      isAcaoLoading.value = false
    }
  }

  async function handleEdit (pedido: PedidoVisualProps) {
    try {
      cartStore.clearCart()
      for (const item of pedido.items) {
        const itemId = item.estoqueId || item.id.toString()
        cartStore.addItem({
          id: itemId,
          title: item.name,
          available: item.available || item.quantity,
          quantity: item.quantity,
          category: 'EQUIPAMENTO',
        })
        cartStore.updateQuantity(itemId, item.quantity)
      }

      await deletePedido(pedido.id)
      pedidos.value = pedidos.value.filter(p => p.id !== pedido.id)

      notifications.info('Itens restaurados no carrinho para edição.')
      await router.push('/pedidos')
    } catch (e) {
      console.error('Erro ao editar pedido:', e)
      notifications.error('Erro ao carregar o pedido para edição.')
    }
  }

  async function handleDelete (pedido: PedidoVisualProps) {
    try {
      await deletePedido(pedido.id)
      pedidos.value = pedidos.value.filter(p => p.id !== pedido.id)
      notifications.success('Pedido excluído com sucesso.')
    } catch (e) {
      console.error('Erro ao deletar pedido:', e)
      notifications.error('Falha ao excluir o pedido.')
    }
  }
</script>

<template>
  <AppPage
    empty-text="Novas solicitações aparecerão aqui para acompanhamento."
    empty-title="Nenhum pedido pendente"
    icon="mdi-timer-sand-empty"
    :is-empty="!isLoading && pedidos.length === 0"
    :loading="isLoading"
  >
    <v-row class="mb-5" density="comfortable">
      <v-col
        v-for="pedido in pedidos"
        :key="pedido.id"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <PedidoCard
          button-text="Ver Detalhes"
          :items="pedido.items"
          items-label="Itens solicitados"
          :requested-at="pedido.solicitadoEm"
          requested-at-label="Data da solicitação"
          :show-button="true"
          status="Pendente"
          status-color="warning"
          :subtitle="`Matrícula: ${pedido.matricula}`"
          :title="pedido.nome"
          @details="abrirDetalhes(pedido)"
          @delete="handleDelete(pedido)"
          @edit="handleEdit(pedido)"
        />
      </v-col>
    </v-row>

    <PedidoDetalhesDialog
      v-if="pedidoDetalhesSelecionado"
      v-model="dialogAberto"
      :emprestimo="pedidoDetalhesSelecionado"
      @action="confirmarAcaoDetalhes"
      @update:model-value="(val: any) => { dialogAberto = val; if (!val) pedidoSelecionado = null }"
    />

    <ConfirmDialog
      v-model="confirmarAcaoConfirmar"
      cancel-text="Cancelar"
      :confirm-text="acaoPendente === 'approve' ? 'Confirmar Aprovação' : 'Confirmar Rejeição'"
      :is-loading="isAcaoLoading"
      :message="mensagemConfirmacao"
      :title="tituloConfirmacao"
      @cancel="confirmarAcaoConfirmar = false"
      @confirm="executarAcao"
    />
  </AppPage>
</template>

