<script lang="ts" setup>

import AppPage from "@/components/ui/AppPage.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/types.ts";
import type {PedidoResponse} from '@/types/dtos'
import PedidoTable from '@/components/pedido/PedidoTable.vue'
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import {mapearParaPedidoVisual, mapearPedidoDetalhes} from "@/components/pedido/window/utils.ts";
import {getPedidosAtivos, returnPedido} from "@/services/pedidos.ts";
import {useCartStore} from "@/stores/cart.ts";
import {useNotificationStore} from "@/stores/notifications.ts";
import {useDisplay} from "vuetify/framework";

const {mdAndUp} = useDisplay()

const pedidos = ref<PedidoResponse[]>([])
const dialogAberto = ref(false)
const pedidoSelecionado = ref<PedidoVisualProps | null>(null)
const isLoading = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const cartStore = useCartStore()
const rules = useNotificationStore()

const pedidoDetalhesSelecionado = computed(() => {
  if (!pedidoSelecionado.value) return null
  return mapearPedidoDetalhes(pedidoSelecionado.value)
})

function abrirDetalhes(pedido: PedidoVisualProps) {
  pedidoSelecionado.value = pedido
  dialogAberto.value = true
}

const confirmarAcaoConfirmar = ref(false)
const isAcaoLoading = ref(false)
const tituloConfirmacao = ref('')
const mensagemConfirmacao = ref('')

async function carregarPedidos() {
  isLoading.value = true
  try {
    const data = await getPedidosAtivos({
      page: currentPage.value - 1,
      size: pageSize.value,
    })

    if (data && Array.isArray((data as any).content)) {
      pedidos.value = (data as any).content
      totalItems.value = (data as any).totalElements || 0
    } else {
      console.warn('Resposta inesperada ao carregar pedidos ativos:', data)
      rules.error('Resposta inesperada do servidor ao carregar os empréstimos ativos.')
    }
  } catch (e) {
    console.error('Erro ao carregar pedidos ativos:', e)
    rules.error('Erro ao carregar os empréstimos ativos.')
  } finally {
    isLoading.value = false
  }
}

watch(currentPage, carregarPedidos)

onMounted(carregarPedidos)

watch(() => cartStore.checkedOut, async checkedOut => {
  if (!checkedOut) return
  await carregarPedidos()
  cartStore.setCheckout(false)
})

function confirmarAcaoDetalhes(acao: 'approve' | 'reject' | 'return') {
  if (!pedidoSelecionado.value) return
  if (acao === 'return') {
    tituloConfirmacao.value = 'Registrar Devolução'
    mensagemConfirmacao.value = 'Tem certeza que deseja registrar a devolução de todos os itens e finalizar o empréstimo?'
    confirmarAcaoConfirmar.value = true
  }
}

async function executarDevolucao() {
  if (!pedidoSelecionado.value) return
  isAcaoLoading.value = true
  try {
    await returnPedido(pedidoSelecionado.value.id)
    rules.success('Devolução registrada com sucesso!')

    confirmarAcaoConfirmar.value = false
    dialogAberto.value = false
    pedidoSelecionado.value = null

    globalThis.window.location.reload()
  } catch (e) {
    console.error('Erro ao efetuar devolução:', e)
    rules.error('Erro ao registrar a devolução.')
  } finally {
    isAcaoLoading.value = false
  }
}
</script>

<template>
  <AppPage
      :is-empty="!isLoading && pedidos.length === 0"
      :loading="isLoading"
      empty-text="Assim que houver um empréstimo em andamento, ele aparecerá aqui."
      empty-title="Nenhum empréstimo ativo"
      icon="mdi-clipboard-check-outline"
  >
    <!-- Modo Grade -->
    <template v-if="!mdAndUp">
      <v-row class="mb-4" density="comfortable">
        <v-col
            v-for="pedidoRaw in pedidos"
            :key="pedidoRaw.id"
            cols="12"
            md="4"
            sm="6"
        >
          <PedidoCard
              :items="mapearParaPedidoVisual(pedidoRaw).items"
              :requested-at="mapearParaPedidoVisual(pedidoRaw).solicitadoEm"
              :show-button="true"
              :subtitle="`Matrícula: ${mapearParaPedidoVisual(pedidoRaw).matricula}`"
              :title="mapearParaPedidoVisual(pedidoRaw).nome"
              button-text="Ver Detalhes"
              items-label="Itens emprestados"
              requested-at-label="Data da solicitação"
              :status="mapearParaPedidoVisual(pedidoRaw).status"
              :status-color="mapearParaPedidoVisual(pedidoRaw).statusColor"
              @details="abrirDetalhes(mapearParaPedidoVisual(pedidoRaw))"
          />
        </v-col>
      </v-row>
    </template>

    <template v-else>
      <PedidoTable
          :pedidos="pedidos"
          :loading="isLoading"
          @details="(p) => abrirDetalhes(mapearParaPedidoVisual(p))"
      />
    </template>

    <!-- Paginação -->
    <div v-if="totalPages > 1" class="d-flex justify-center mt-4 mb-2">
      <v-pagination
          v-model="currentPage"
          :length="totalPages"
          :total-visible="4"
          density="compact"
          rounded="md"
          show-first-last-page
      />
    </div>

    <PedidoDetalhesDialog
        v-if="pedidoDetalhesSelecionado"
        v-model="dialogAberto"
        :pedido-detalhes="pedidoDetalhesSelecionado"
        @action="confirmarAcaoDetalhes"
        @update:model-value="(val: any) => { dialogAberto = val; if (!val) pedidoSelecionado = null }"
    />

    <ConfirmDialog
        v-model="confirmarAcaoConfirmar"
        :is-loading="isAcaoLoading"
        :message="mensagemConfirmacao"
        :title="tituloConfirmacao"
        cancel-text="Cancelar"
        confirm-text="Confirmar Devolução"
        @cancel="confirmarAcaoConfirmar = false"
        @confirm="executarDevolucao"
    />
  </AppPage>
</template>
