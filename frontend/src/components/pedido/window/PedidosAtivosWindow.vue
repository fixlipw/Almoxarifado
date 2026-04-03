<script setup lang="ts">

import AppPage from "@/components/ui/AppPage.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/window/types.ts";
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import { mapearPedidoDetalhes, mapearParaPedidoVisual } from "@/components/pedido/window/utils.ts";
import { getPedidosAtivos } from "@/services/pedidos.ts";
import { useCartStore } from "@/stores/cart.ts";
import { useNotificationStore } from "@/stores/notifications.ts";

  const pedidos = ref<any[]>([])
  const dialogAberto = ref(false)
  const pedidoSelecionado = ref<PedidoVisualProps | null>(null)
  const isLoading = ref(false)

  const cartStore = useCartStore()
  const rules = useNotificationStore()

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

  async function carregarPedidos () {
    isLoading.value = true
    try {
      const data = await getPedidosAtivos()
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
    if (acao === 'return') {
      tituloConfirmacao.value = 'Registrar Devolução'
      mensagemConfirmacao.value = 'Tem certeza que deseja registrar a devolução de todos os itens e finalizar o empréstimo?'
      confirmarAcaoConfirmar.value = true
    }
  }

  function executarDevolucao () {
    if (!pedidoSelecionado.value) return
    isAcaoLoading.value = true
    // TODO: implement devolucao in services
    setTimeout(() => {
      isAcaoLoading.value = false
      confirmarAcaoConfirmar.value = false
      dialogAberto.value = false
      rules.success('Devolução registrada com sucesso!')
      pedidoSelecionado.value = null
    }, 2000)
  }
</script>

<template>
  <AppPage
    empty-text="Assim que houver um empréstimo em andamento, ele aparecerá aqui."
    empty-title="Nenhum empréstimo ativo"
    icon="mdi-clipboard-check-outline"
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
          items-label="Itens emprestados"
          :requested-at="pedido.solicitadoEm"
          requested-at-label="Data da solicitação"
          :show-button="true"
          status="Ativo"
          status-color="success"
          :subtitle="`Matrícula: ${pedido.matricula}`"
          :title="pedido.nome"
          @details="abrirDetalhes(pedido)"
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
      confirm-text="Confirmar Devolução"
      :is-loading="isAcaoLoading"
      :message="mensagemConfirmacao"
      :title="tituloConfirmacao"
      @cancel="confirmarAcaoConfirmar = false"
      @confirm="executarDevolucao"
    />
  </AppPage>
</template>
