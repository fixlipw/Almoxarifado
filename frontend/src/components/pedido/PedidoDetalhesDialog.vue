<script lang="ts" setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import {deletePedido} from '@/services/pedidos'
import {useCartStore} from '@/stores/cart'
import {useAuthStore} from '@/stores/auth'
import type {PedidoDetalhesProps} from "@/components/pedido/types.ts";
import {useDisplay} from "vuetify";

interface Props {
  modelValue: boolean
  pedidoDetalhes: PedidoDetalhesProps
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'action': [action: 'approve' | 'reject' | 'return']
}>()

const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()
const {smAndUp} = useDisplay()


const showConfirm = ref(false)
const isLoading = ref(false)
const isAluno = authStore.userRole === 'ALUNO'

function statusColor(status: string) {
  const normalized = status.toLowerCase()
  if (normalized.includes('pendente')) return 'warning'
  if (normalized.includes('ativo')) return 'success'
  if (normalized.includes('finalizado') || normalized.includes('devolvido')) return 'info'
  if (normalized.includes('atrasado')) return 'error'
  return 'primary'
}

function handleAction(actionType: 'approve' | 'reject' | 'return') {
  emit('action', actionType)
}

async function handleConfirmEdit() {
  isLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))

    cartStore.clearCart()
    try {
      await deletePedido(props.pedidoDetalhes.codigo)
    } catch (error) {
      console.error('Não foi possível excluir pedido durante edição', error)
    }

    showConfirm.value = false
    emit('update:modelValue', false)

    await router.push('/inventario')
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <v-dialog :model-value="modelValue" max-width="550" @update:model-value="$emit('update:modelValue', $event)">
    <AppCard :content-class="'pa-3'" :header-class="'pb-2'" class="overflow-x-hidden">
      <template #header>
        <v-row align="start" class="flex-nowrap" justify="space-between" no-gutters>
          <v-col class="d-flex flex-column flex-sm-row align-start align-sm-center ga-1 ga-sm-2">
            <v-card-title class="pa-0 text-title-medium font-weight-bold text-wrap">
              Detalhes do Empréstimo
            </v-card-title>
            <v-chip :color="statusColor(pedidoDetalhes.status)" class="font-weight-medium" size="x-small">
              {{ pedidoDetalhes.status }}
            </v-chip>
          </v-col>
          <v-col class="pl-2" cols="auto">
            <v-btn flat icon size="small" @click="$emit('update:modelValue', false)">
              <v-icon size="18">mdi-close</v-icon>
            </v-btn>
          </v-col>
        </v-row>
        <v-row class="text-body-small text-medium-emphasis pa-0 mt-1">
          {{ pedidoDetalhes.codigo }}
        </v-row>
      </template>

      <v-divider class="my-2"/>

      <v-card-text style="overflow-y: auto; max-height: 60vh; padding-bottom: 0;">
        <v-list class="pa-0" density="compact">

          <v-list-item class="px-0">
            <v-list-item-title class="font-weight-bold mb-2 text-title-medium">
              Solicitante
            </v-list-item-title>

            <v-list-item-subtitle>
              <v-list-item class="px-0" density="compact">
                <template #prepend>
                  <v-icon class="mr-1" size="20">mdi-account-outline</v-icon>
                </template>
                <v-list-item-title class="text-body-medium font-weight-bold text-wrap">
                  <span> {{ pedidoDetalhes.solicitante.nome }} </span>
                  <v-chip v-if="pedidoDetalhes.solicitante.tipo" class="ml-2" color="primary" size="x-small"
                          variant="flat">
                    {{ pedidoDetalhes.solicitante.tipo }}
                  </v-chip>
                </v-list-item-title>
                <v-list-item-media class="d-flex flex-column mt-1 text-body-medium text-wrap">
                  <span class="text-medium-emphasis">Matrícula: {{ pedidoDetalhes.solicitante.matricula }}</span>
                  <span class="text-medium-emphasis">Curso: {{ pedidoDetalhes.solicitante.curso }}</span>
                </v-list-item-media>
              </v-list-item>
            </v-list-item-subtitle>
          </v-list-item>

          <v-divider class="my-3"/>

          <v-list-item class="px-0">
            <v-list-item-title class="font-weight-bold mb-2 text-title-medium">
              Itens Solicitados ({{ pedidoDetalhes.itens.length }})
            </v-list-item-title>

            <v-list class="pa-0" density="compact">
              <v-list-item v-for="item in pedidoDetalhes.itens" :key="item.id" class="px-0">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-cube-outline</v-icon>
                </template>
                <v-list-item-title class="d-flex flex-column text-body-medium pr-2">
                  <span>{{ item.nomeItem || item.estoqueId }}</span>
                  <span v-if="!smAndUp" class="text-body-small font-weight-medium text-nowrap">
                    Quantidade: {{ item.quantidadeItem }}
                  </span>
                </v-list-item-title>
                <template #append>
                  <span v-if="smAndUp" class="text-body-small font-weight-medium text-nowrap">
                    Qtd: {{ item.quantidadeItem }}
                  </span>
                </template>
              </v-list-item>
            </v-list>
          </v-list-item>

          <v-divider class="my-3"/>

          <v-list-item class="px-0">
            <v-list-item-title class="font-weight-bold mb-2 text-title-medium">
              Histórico e Prazos
            </v-list-item-title>

            <v-timeline class="mt-1" density="compact" side="end" truncate-line="both">
              <v-timeline-item
                  dot-color="primary"
                  size="small"
              >
                <template #icon>
                  <v-icon size="14">mdi-calendar-blank-outline</v-icon>
                </template>
                <div class="d-flex flex-column">
                  <span class="text-body-medium">Solicitação enviada</span>
                  <span class="text-body-small text-medium-emphasis">{{ pedidoDetalhes.dataSolicitacao }}</span>
                </div>
              </v-timeline-item>

              <v-timeline-item
                  v-if="pedidoDetalhes.dataAprovacao"
                  dot-color="success"
                  size="small"
              >
                <template #icon>
                  <v-icon size="14">mdi-check-circle-outline</v-icon>
                </template>
                <div class="d-flex flex-column">
                  <span class="text-body-medium">Solicitação aprovada</span>
                  <span class="text-body-small text-medium-emphasis">{{ pedidoDetalhes.dataAprovacao }}</span>
                </div>
              </v-timeline-item>

              <v-timeline-item
                  v-if="pedidoDetalhes.dataDevolucao"
                  dot-color="warning"
                  size="small"
              >
                <template #icon>
                  <v-icon size="14">mdi-calendar-check</v-icon>
                </template>
                <div class="d-flex flex-column">
                  <span class="text-body-medium">Devolução dos itens</span>
                  <span class="text-body-small text-medium-emphasis">{{ pedidoDetalhes.dataDevolucao }}</span>
                </div>
              </v-timeline-item>

              <v-timeline-item
                  v-if="pedidoDetalhes.dataAtualizacao"
                  dot-color="grey"
                  size="small"
              >
                <template #icon>
                  <v-icon size="14">mdi-update</v-icon>
                </template>
                <div class="d-flex flex-column">
                  <span class="text-body-medium">Última atualização</span>
                  <span class="text-body-small text-medium-emphasis">{{ pedidoDetalhes.dataAtualizacao }}</span>
                </div>
              </v-timeline-item>

            </v-timeline>
          </v-list-item>

          <v-divider v-if="pedidoDetalhes.observacoes" class="my-3"/>

          <v-list-item v-if="pedidoDetalhes.observacoes" class="px-0">
            <v-list-item-title class="font-weight-bold mb-1 text-title-medium">Observações</v-list-item-title>
            <div class="text-body-medium text-medium-emphasis" style="white-space: pre-line; word-break: break-word;">
              {{ pedidoDetalhes.observacoes }}
            </div>
          </v-list-item>

        </v-list>
      </v-card-text>

      <template #actions>
        <div class="d-flex flex-column flex-sm-row ga-2 w-100 px-2 pb-2">
          <AppButton
              v-if="pedidoDetalhes.status.toLowerCase().includes('pendente') && !isAluno"
              class="flex-grow-1"
              color="success"
              @click="handleAction('approve')"
          >
            Aprovar
          </AppButton>

          <AppButton
              v-if="pedidoDetalhes.status.toLowerCase().includes('pendente') && !isAluno"
              class="flex-grow-1"
              color="error"
              @click="handleAction('reject')"
          >
            Rejeitar
          </AppButton>

          <AppButton
              v-if="pedidoDetalhes.status.toLowerCase().includes('ativo') && !isAluno"
              class="flex-grow-1"
              color="info"
              @click="handleAction('return')"
          >
            Registrar Devolução
          </AppButton>

          <AppButton
              :class="!pedidoDetalhes.status.toLowerCase().includes('pendente') && !pedidoDetalhes.status.toLowerCase().includes('ativo') ? 'block w-100' : 'flex-grow-1'"
              color="primary"
              variant="outlined"
              @click="$emit('update:modelValue', false)"
          >
            Fechar
          </AppButton>
        </div>
      </template>

      <ConfirmDialog
          v-model="showConfirm"
          :is-loading="isLoading"
          cancel-text="Cancelar"
          confirm-text="Continuar"
          message="Ao devolver os itens e editar este empréstimo, o pedido de empréstimo atual será excluído. Deseja continuar?"
          title="Confirmar Devolução e Exclusão"
          @cancel="showConfirm = false"
          @confirm="handleConfirmEdit"
      />
    </AppCard>
  </v-dialog>
</template>
