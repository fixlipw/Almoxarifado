<script lang="ts" setup>

import AppPage from "@/components/ui/AppPage.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/types.ts";
import type { PedidoResponse } from '@/types/dtos'
import PedidoTable from '@/components/pedido/PedidoTable.vue'
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import {mapearParaPedidoVisual, mapearPedidoDetalhes} from "@/components/pedido/window/utils.ts";
function mapToVisual(apiPedido: PedidoResponse) {
  return mapearParaPedidoVisual(apiPedido)
}
import {getPedidosAtrasados, returnPedido} from "@/services/pedidos.ts";
import {useCartStore} from "@/stores/cart.ts";
import {useNotificationStore} from "@/stores/notifications.ts";
import {useAuthStore} from "@/stores/auth.ts";
import {useDisplay} from "vuetify/framework";

const {mdAndUp} = useDisplay()
const props = defineProps<{ viewMode?: string }>()

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
const authStore = useAuthStore()

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
    const userId = authStore.userRole === 'ALUNO' ? authStore.session?.usuario?.id : undefined
    const data = await getPedidosAtrasados({
      userId,
      page: currentPage.value - 1,
      size: pageSize.value,
    })

    if (data && Array.isArray((data as any).content)) {
      pedidos.value = (data as any).content
      totalItems.value = (data as any).totalElements
    } else {
      pedidos.value = []
      totalItems.value = 0
    }
  } catch (e) {
    console.error('Erro ao carregar pedidos atrasados:', e)
    rules.error('Ocorreu um erro ao carregar pedidos atrasados.')
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
  const userId = authStore.session?.usuario?.id
  if (!userId) {
    rules.error('Usuário não autenticado.')
    return
  }

  isAcaoLoading.value = true
  try {
    await returnPedido(pedidoSelecionado.value.id)
    rules.success('Devolução registrada com sucesso!')

    confirmarAcaoConfirmar.value = false
    dialogAberto.value = false
    pedidoSelecionado.value = null

    window.location.reload()
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
      empty-text="Nenhum empréstimo atrasado no momento."
      empty-title="Nenhum atraso"
      icon="mdi-alert-circle-outline"
  >
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
              :items="mapToVisual(pedidoRaw).items"
              :requested-at="mapToVisual(pedidoRaw).solicitadoEm"
              :returned-at="mapToVisual(pedidoRaw).finalizadoEm ?? undefined"
              :show-button="true"
              :subtitle="`Matrícula: ${mapToVisual(pedidoRaw).matricula}`"
              :title="mapToVisual(pedidoRaw).nome"
              button-text="Ver Detalhes"
              items-label="Itens emprestados"
              requested-at-label="Data da solicitação"
              returned-at-label="Data de devolução"
              :status="mapToVisual(pedidoRaw).status"
              :status-color="mapToVisual(pedidoRaw).statusColor"
              @details="abrirDetalhes(mapToVisual(pedidoRaw))"
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

<style scoped>

</style>
