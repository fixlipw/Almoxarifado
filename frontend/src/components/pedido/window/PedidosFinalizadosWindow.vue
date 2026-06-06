<script lang="ts" setup>

import AppPage from "@/components/ui/AppPage.vue";
import {computed, onMounted, ref, watch} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/types.ts";
import type {PedidoResponse} from '@/types/dtos'
import PedidoTable from '@/components/pedido/PedidoTable.vue'
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import {mapearParaPedidoVisual, mapearPedidoDetalhes} from "@/components/pedido/window/utils.ts";
import {getPedidosFinalizados} from "@/services/pedidos.ts";
import {useCartStore} from "@/stores/cart.ts";
import {useDisplay} from "vuetify";
import useNotificationStore from "@/stores/notifications.ts";

const {mdAndUp} = useDisplay()

const pedidos = ref<PedidoResponse[]>([])
const dialogAberto = ref(false)
const pedidoSelecionado = ref<PedidoVisualProps | null>(null)
const isLoading = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

const rules = useNotificationStore()
const cartStore = useCartStore()

const pedidoDetalhesSelecionado = computed(() => {
  if (!pedidoSelecionado.value) return null
  return mapearPedidoDetalhes(pedidoSelecionado.value)
})

function abrirDetalhes(pedido: PedidoVisualProps) {
  pedidoSelecionado.value = pedido
  dialogAberto.value = true
}

async function carregarPedidos() {
  isLoading.value = true
  try {
    const data = await getPedidosFinalizados({
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
  } catch (error) {
    console.error("Erro ao carregar pedidos finalizados:", error)
    rules.error('Ocorreu um erro ao carregar os pedidos finalizados.')
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

</script>

<template>
  <AppPage
      :is-empty="!isLoading && pedidos.length === 0"
      :loading="isLoading"
      empty-text="Os pedidos concluídos serão listados aqui."
      empty-title="Nenhum pedido finalizado"
      icon="mdi-clipboard-text-clock-outline"
  >
    <template v-if="!mdAndUp">
      <v-row class="mb-5" density="comfortable">
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
              :returned-at="mapearParaPedidoVisual(pedidoRaw).finalizadoEm ?? undefined"
              :show-button="true"
              :subtitle="`Matrícula: ${mapearParaPedidoVisual(pedidoRaw).matricula}`"
              :title="mapearParaPedidoVisual(pedidoRaw).nome"
              button-text="Ver Detalhes"
              items-label="Itens emprestados"
              requested-at-label="Data da solicitação"
              returned-at-label="Data de devolução"
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
        @update:model-value="(val: any) => { dialogAberto = val; if (!val) pedidoSelecionado = null }"
    />
  </AppPage>
</template>

