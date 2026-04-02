<script setup lang="ts">

import AppPage from "@/components/ui/AppPage.vue";
import {computed, onMounted, ref} from "vue";
import type {PedidoVisualProps} from "@/components/pedido/window/types.ts";
import PedidoDetalhesDialog from "@/components/pedido/PedidoDetalhesDialog.vue";
import PedidoCard from "@/components/pedido/PedidoCard.vue";
import { mapearPedidoDetalhes, mapearParaPedidoVisual } from "@/components/pedido/window/utils.ts";
import { getPedidosFinalizados } from "@/services/pedidos.ts";

  const pedidos = ref<any[]>([])
  const dialogAberto = ref(false)
  const pedidoSelecionado = ref<PedidoVisualProps | null>(null)

  const pedidoDetalhesSelecionado = computed(() => {
    if (!pedidoSelecionado.value) return null
    return mapearPedidoDetalhes(pedidoSelecionado.value)
  })

  function abrirDetalhes (pedido: PedidoVisualProps) {
    pedidoSelecionado.value = pedido
    dialogAberto.value = true
  }

  onMounted(async () => {
    try {
      const data = await getPedidosFinalizados()
      pedidos.value = data.map(mapearParaPedidoVisual)
    } catch (e) {
      console.error(e)
    }
  })

</script>

<template>
  <AppPage>
    <div v-if="pedidos.length === 0" class="text-center text-medium-emphasis my-10">
      Nenhum pedido finalizado.
    </div>

    <v-row v-else class="mb-5" density="comfortable">
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
          :returned-at="pedido.finalizadoEm"
          returned-at-label="Data de devolução"
          :show-button="true"
          status="Finalizado"
          status-color="grey"
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
      @update:model-value="(val: any) => { dialogAberto = val; if (!val) pedidoSelecionado = null }"
    />
  </AppPage>
</template>

