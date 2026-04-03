<script lang="ts" setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import {deletePedido} from '@/services/pedidos'
import {useCartStore} from '@/stores/cart'
import type {PedidoDetalhesProps} from "@/components/pedido/types.ts";

interface Props {
  modelValue: boolean
  emprestimo: PedidoDetalhesProps
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'action': [action: 'approve' | 'reject' | 'return']
}>()

const router = useRouter()
const cartStore = useCartStore()

const showConfirm = ref(false)
const isLoading = ref(false)
const isAluno = props.emprestimo.solicitante.tipo.toLowerCase() === 'aluno'

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
  emit('update:modelValue', false)
}

async function handleConfirmEdit() {
  isLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))

    cartStore.clearCart()

    for (const item of props.emprestimo.itens) {

    }

    try {
      await deletePedido(props.emprestimo.codigo)
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
            <v-chip :color="statusColor(emprestimo.status)" class="font-weight-medium" size="x-small">
              {{ emprestimo.status }}
            </v-chip>
          </v-col>
          <v-col class="pl-2" cols="auto">
            <v-btn flat icon size="small" @click="$emit('update:modelValue', false)">
              <v-icon size="18">mdi-close</v-icon>
            </v-btn>
          </v-col>
        </v-row>
        <v-card-subtitle class="text-body-small text-medium-emphasis pa-0 mt-1">
          Código: {{ emprestimo.codigo }}
        </v-card-subtitle>
      </template>

      <v-divider class="my-2"/>

      <v-card-text style="overflow-y: auto; max-height: 60vh; padding-bottom: 0;">
        <v-list class="pa-0" density="compact">

          <v-list-item class="px-0">
            <v-list-item-title class="font-weight-bold mb-2 text-title-medium">
              Solicitante
            </v-list-item-title>

            <v-list-item class="px-0" density="compact">
              <template #prepend>
                <v-icon class="mr-1" size="20">mdi-account-outline</v-icon>
              </template>
              <v-list-item-title class="text-body-medium font-weight-bold text-wrap">
                {{ emprestimo.solicitante.nome }}
              </v-list-item-title>
              <v-list-item-subtitle class="d-flex flex-column mt-1 text-body-medium">
                <span>E-mail: {{ emprestimo.solicitante.email }}</span>
                <span v-if="emprestimo.solicitante.matricula">Matrícula: {{ emprestimo.solicitante.matricula }}</span>
                <span>{{ emprestimo.solicitante.curso }}</span>
              </v-list-item-subtitle>
              <template #append>
                <v-chip class="ml-2 mt-n4" size="x-small">
                  {{ emprestimo.solicitante.tipo }}
                </v-chip>
              </template>
            </v-list-item>
          </v-list-item>

          <v-divider class="my-3"/>

          <v-list-item class="px-0">
            <v-list-item-title class="font-weight-bold mb-2 text-title-medium">
              Itens Solicitados ({{ emprestimo.itens.length }})
            </v-list-item-title>

            <v-list class="pa-0" density="compact">
              <v-list-item v-for="item in emprestimo.itens" :key="item.id" class="px-0">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-cube-outline</v-icon>
                </template>
                <v-list-item-title class="text-body-medium text-wrap pr-2">
                  {{ item.nomeItem || item.estoqueId }}
                </v-list-item-title>
                <template #append>
              <span class="text-body-small font-weight-medium text-nowrap">
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

            <v-list class="pa-0" density="compact">
              <v-list-item class="px-0 py-1">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-calendar-blank-outline</v-icon>
                </template>
                <v-list-item-title class="text-body-medium text-wrap">Solicitação enviada</v-list-item-title>
                <template #append><span class="text-body-small">{{ emprestimo.dataSolicitacao }}</span></template>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataAprovacao" class="px-0 py-1">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-check-circle-outline</v-icon>
                </template>
                <v-list-item-title class="text-body-medium text-wrap">Solicitação aprovada</v-list-item-title>
                <template #append><span class="text-body-small">{{ emprestimo.dataAprovacao }}</span></template>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataDevolucao" class="px-0 py-1">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-calendar-check</v-icon>
                </template>
                <v-list-item-title class="text-body-medium text-wrap">Devolução dos itens</v-list-item-title>
                <template #append><span class="text-body-small">{{ emprestimo.dataDevolucao }}</span></template>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataAtualizacao" class="px-0 py-1">
                <template #prepend>
                  <v-icon class="mr-1" size="18">mdi-update</v-icon>
                </template>
                <v-list-item-title class="text-body-medium text-wrap">Última atualização</v-list-item-title>
                <template #append><span class="text-body-small">{{ emprestimo.dataAtualizacao }}</span></template>
              </v-list-item>
            </v-list>
          </v-list-item>

          <v-divider v-if="emprestimo.observacoes" class="my-3"/>

          <v-list-item v-if="emprestimo.observacoes" class="px-0">
            <v-list-item-title class="font-weight-bold mb-1 text-title-medium">Observações</v-list-item-title>
            <div class="text-body-medium text-medium-emphasis" style="white-space: pre-line; word-break: break-word;">
              {{ emprestimo.observacoes }}
            </div>
          </v-list-item>

        </v-list>
      </v-card-text>

      <template #actions>
        <div class="d-flex flex-column flex-sm-row ga-2 w-100 px-2 pb-2">
          <AppButton
              v-if="emprestimo.status.toLowerCase().includes('pendente') && !isAluno"
              class="flex-grow-1"
              color="success"
              @click="handleAction('approve')"
          >
            Aprovar
          </AppButton>

          <AppButton
              v-if="emprestimo.status.toLowerCase().includes('pendente') && !isAluno"
              class="flex-grow-1"
              color="error"
              @click="handleAction('reject')"
          >
            Rejeitar
          </AppButton>

          <AppButton
              v-if="emprestimo.status.toLowerCase().includes('ativo') && !isAluno"
              class="flex-grow-1"
              color="info"
              @click="handleAction('return')"
          >
            Registrar Devolução
          </AppButton>

          <AppButton
              :class="!emprestimo.status.toLowerCase().includes('pendente') && !emprestimo.status.toLowerCase().includes('ativo') ? 'block w-100' : 'flex-grow-1'"
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
