<script lang="ts" setup>
import {computed} from 'vue'
import type {PedidoResponse} from '@/types/dtos'
import {formatDate} from '@/utils'
import AppTable from '@/components/ui/AppTable.vue'
import AppButton from '@/components/ui/AppButton.vue'
import {useAuthStore} from '@/stores/auth'

const {pedidos, loading} = withDefaults(defineProps<{
  pedidos: PedidoResponse[]
  loading?: boolean
}>(), {loading: false})

const emit = defineEmits<{
  (e: 'edit', pedido: PedidoResponse): void
  (e: 'details', pedido: PedidoResponse): void
  (e: 'delete', pedido: PedidoResponse): void
}>()

const authStore = useAuthStore()
const isBolsistaOrAdmin = computed(() => authStore.userRole === 'BOLSISTA' || authStore.userRole === 'ADMIN')

const headers = [
  {key: 'info', title: 'Solicitante'},
  {key: 'items', title: 'Itens', width: '100px'},
]

const headersProcessados = computed(() => {
  if (pedidos.some(p => p.aprovado)) {
    headers.push({key: 'requestedAt', title: 'Data da aprovação', width: '180px'})
  } else if (pedidos.some(p => p.finalizado)) {
    headers.push({key: 'requestedAt', title: 'Data da devolução', width: '180px'})
  } else {
    headers.push({key: 'requestedAt', title: 'Data da solicitação', width: '180px'})}
  return [...headers, {key: 'status', title: 'Status', width: '120px'}, {key: 'actions', title: 'Ações', align: 'end' as const, width: '120px'}]
})


function statusFor(item: PedidoResponse) {
  if (item.finalizado) return 'Finalizado'
  if (item.aprovado) return 'Ativo'
  return 'Pendente'
}

function statusColorFor(item: PedidoResponse) {
  const s = statusFor(item).toLowerCase()
  if (s.includes('pendente')) return 'warning'
  if (s.includes('ativo')) return 'success'
  if (s.includes('finalizado') || s.includes('devolvido')) return 'info'
  if (s.includes('atrasado')) return 'error'
  return 'primary'
}

function formatDateOrDash(date?: string) {
  const val = date ? formatDate(date) : ''
  return val || '—'
}

</script>

<template>
  <AppTable
      :headers="headersProcessados"
      :items="pedidos"
      :loading="loading"
      empty-text="Nenhum pedido encontrado."
      item-key="id"
  >
    <!-- Solicitante: nome + subtítulo (matrícula) -->
    <template #item.info="{ item }">
      <div class="d-flex align-center ga-3 py-2">
        <div>
          <div class="text-body-2 font-weight-bold" style="line-height: 1.2;">{{
              item.solicitante?.nome || item.nome
            }}
          </div>
          <div v-if="item.solicitante?.matricula || item.matricula" class="text-caption text-medium-emphasis">
            {{ item.solicitante?.matricula || item.matricula }}
          </div>
        </div>
      </div>
    </template>

    <!-- Itens: quantidade -->
    <template #item.items="{ item }">
      <div class="text-caption font-weight-bold">{{ (item.itens || []).length }}</div>
    </template>

    <!-- Data da ação -->
    <template #item.requestedAt="{ item }">
      <div class="text-caption">{{ formatDateOrDash(item.dataSolicitacao) }}</div>
    </template>

    <!-- Status -->
    <template #item.status="{ item }">
      <v-chip :color="statusColorFor(item) || 'primary'" class="font-weight-bold" size="small" variant="flat">
        {{ statusFor(item) || '—' }}
      </v-chip>
    </template>

    <!-- Ações -->
    <template #item.actions="{ item }">
      <div class="d-flex align-center justify-end ga-2">
        <v-tooltip location="top" text="Ver detalhes">
          <template #activator="{ props: tooltipProps }">
            <AppButton :aria-label="`Ver detalhes do pedido ${item.id}`" class="px-0" color="info"
             style="min-width: 36px; width: 36px; height: 36px;" v-bind="tooltipProps" variant="tonal"
             @click="emit('details', item)">
              <v-icon size="18">mdi-eye</v-icon>
            </AppButton>
          </template>
        </v-tooltip>

        <template v-if="isBolsistaOrAdmin">
          <v-tooltip location="top" text="Editar pedido">
            <template #activator="{ props: tooltipProps }">
              <AppButton :aria-label="`Editar pedido ${item.id}`" class="px-0" color="primary"
               style="min-width: 36px; width: 36px; height: 36px;" v-bind="tooltipProps" variant="tonal"
               @click="emit('edit', item)">
                <v-icon size="18">mdi-pencil</v-icon>
              </AppButton>
            </template>
          </v-tooltip>

          <v-tooltip location="top" text="Excluir pedido">
            <template #activator="{ props: tooltipProps }">
              <AppButton :aria-label="`Excluir pedido ${item.id}`" class="px-0" color="error"
               style="min-width: 36px; width: 36px; height: 36px;" v-bind="tooltipProps" variant="tonal"
               @click="emit('delete', item)">
                <v-icon size="18">mdi-delete</v-icon>
              </AppButton>
            </template>
          </v-tooltip>
        </template>
      </div>
    </template>
  </AppTable>
</template>
