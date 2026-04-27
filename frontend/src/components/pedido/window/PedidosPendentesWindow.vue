<script lang="ts" setup>

import AppPage from "@/components/ui/AppPage.vue";
import ConfirmDialog from "@/components/common/ConfirmDialog.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/types.ts";
import type {PedidoResponse} from '@/types/dtos.ts'
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import {mapearParaPedidoVisual, mapearPedidoDetalhes} from "@/components/pedido/window/utils.ts";
import {approvePedido, deletePedido, getPedidosPendentes, rejectPedido} from "@/services/pedidos.ts";
import {useCartStore} from "@/stores/cart.ts";
import {useNotificationStore} from "@/stores/notifications.ts";
import {useRouter} from "vue-router";
import {useAuthStore} from "@/stores/auth.ts";
import {useDisplay} from "vuetify";
import PedidoTable from "@/components/pedido/PedidoTable.vue";

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
const router = useRouter()
const notifications = useNotificationStore()
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
const acaoPendente = ref<'approve' | 'reject' | null>(null)

async function carregarPedidos() {
  isLoading.value = true
  try {
    const userId = authStore.userRole === 'ALUNO' ? authStore.session?.usuario?.id : undefined
    const data = await getPedidosPendentes({
      userId,
      page: currentPage.value - 1,
      size: pageSize.value,
    })

    if (data && Array.isArray((data as any).content)) {
      pedidos.value = (data as any).content
      totalItems.value = (data as any).totalElements ?? 0
    } else {
      pedidos.value = []
      totalItems.value = 0
    }
  } catch (e) {
    console.error('Erro ao carregar pedidos pendentes:', e)
    notifications.error('Ocorreu um erro ao carregar os pedidos pendentes.')
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

async function executarAcao() {
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

async function handleEdit(pedido: PedidoVisualProps) {
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

async function handleDelete(pedido: PedidoVisualProps) {
  try {
    await deletePedido(pedido.id)
    await carregarPedidos()
    notifications.success('Pedido excluído com sucesso.')
  } catch (e) {
    console.error('Erro ao deletar pedido:', e)
    notifications.error('Falha ao excluir o pedido.')
  }
}
</script>

<template>
  <AppPage
      :is-empty="!isLoading && pedidos.length === 0"
      :loading="isLoading"
      empty-text="Novas solicitações aparecerão aqui para acompanhamento."
      empty-title="Nenhum pedido pendente"
      icon="mdi-timer-sand-empty"
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
              items-label="Itens solicitados"
              requested-at-label="Data da solicitação"
              :status="mapearParaPedidoVisual(pedidoRaw).status"
              :status-color="mapearParaPedidoVisual(pedidoRaw).statusColor"
              @delete="handleDelete(mapearParaPedidoVisual(pedidoRaw))"
              @details="abrirDetalhes(mapearParaPedidoVisual(pedidoRaw))"
              @edit="handleEdit(mapearParaPedidoVisual(pedidoRaw))"
          />
        </v-col>
      </v-row>
    </template>

    <template v-else>
      <PedidoTable
          :pedidos="pedidos"
          :loading="isLoading"
          @details="(p) => abrirDetalhes(mapearParaPedidoVisual(p))"
          @edit="(p) => handleEdit(mapearParaPedidoVisual(p))"
          @delete="(p) => handleDelete(mapearParaPedidoVisual(p))"
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
        :confirm-text="acaoPendente === 'approve' ? 'Confirmar Aprovação' : 'Confirmar Rejeição'"
        :is-loading="isAcaoLoading"
        :message="mensagemConfirmacao"
        :title="tituloConfirmacao"
        cancel-text="Cancelar"
        @cancel="confirmarAcaoConfirmar = false"
        @confirm="executarAcao"
    />
  </AppPage>
</template>

