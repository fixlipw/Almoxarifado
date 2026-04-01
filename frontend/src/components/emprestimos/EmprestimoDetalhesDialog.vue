<script setup lang="ts">
  import type { EmprestimoDetalhes } from '@/types'
  import { ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { api } from '@/api'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'
  import { useCartStore } from '@/stores/cart'

  interface Props {
    modelValue: boolean
    emprestimo: EmprestimoDetalhes
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

  function statusColor (status: string) {
    const normalized = status.toLowerCase()
    if (normalized.includes('pendente')) return 'warning'
    if (normalized.includes('ativo')) return 'success'
    if (normalized.includes('finalizado') || normalized.includes('devolvido')) return 'info'
    if (normalized.includes('atrasado')) return 'error'
    return 'primary'
  }

  function handleAction (actionType: 'approve' | 'reject' | 'return') {
    emit('action', actionType)
    emit('update:modelValue', false)
  }

  async function handleConfirmEdit () {
    isLoading.value = true
    try {
      await new Promise(resolve => setTimeout(resolve, 500))

      cartStore.clearCart()

      for (const item of props.emprestimo.itens) {
        cartStore.addItem({
          id: item.estoqueId,
          quantity: item.quantidadeItem,
          title: item.nomeItem ?? item.estoqueId,
          category: 'Empréstimo',
          available: item.quantidadeItem,
          total: item.quantidadeItem,
          emprestimoId: props.emprestimo.codigo,
        }, item.quantidadeItem)
      }

      try {
        await api.deletePedido(props.emprestimo.codigo)
      } catch (error) {
        console.error('Não foi possível excluir pedido durante edição', error)
      }

      // Fechar diálogos
      showConfirm.value = false
      emit('update:modelValue', false)

      await router.push('/inventario')
    } finally {
      isLoading.value = false
    }
  }
</script>

<template>
  <v-dialog max-width="500" :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)">
    <AppCard class="overflow-x-hidden" :content-class="'pa-3'" :header-class="'pb-2'">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-title-medium font-weight-bold" style="font-size: 1.1rem;">Detalhes do Empréstimo</v-card-title>
            <v-chip class="ml-2" :color="statusColor(emprestimo.status)" size="x-small" style="font-size: 0.8rem;">{{ emprestimo.status }}</v-chip>
          </v-col>
          <v-col cols="auto">
            <v-btn flat icon size="small" @click="$emit('update:modelValue', false)"><v-icon size="18">mdi-close</v-icon></v-btn>
          </v-col>
        </v-row>
        <v-card-subtitle class="text-body-small text-medium-emphasis pa-0 mt-1" style="font-size: 0.85rem;">Código: {{ emprestimo.codigo }}</v-card-subtitle>
      </template>
      <v-divider class="my-2" />
      <v-card-text style="overflow-y: auto; max-height: 55vh; padding-bottom: 0;">
        <v-list class="pa-0" density="compact">
          <v-list-item>
            <v-list-item-title class="font-weight-bold mb-1" style="font-size: 1rem;">Solicitante</v-list-item-title>
            <v-row align="center" class="ga-2" no-gutters>
              <v-col cols="auto"><v-icon size="16">mdi-account-outline</v-icon></v-col>
              <v-col cols="auto" style="font-size: 0.95rem;">{{ emprestimo.solicitante.nome }}</v-col>
              <v-col cols="auto"><v-chip class="ml-2" size="x-small" style="font-size: 0.75rem;">{{ emprestimo.solicitante.tipo }}</v-chip></v-col>
            </v-row>
            <v-list-item-subtitle class="text-body-medium mt-1" style="font-size: 0.9rem;">
              <span>E-mail: {{ emprestimo.solicitante.email }}</span>
              <span v-if="emprestimo.solicitante.matricula">· Matrícula: {{ emprestimo.solicitante.matricula }}</span>
            </v-list-item-subtitle>
            <v-list-item-subtitle class="text-body-medium" style="font-size: 0.9rem;">{{ emprestimo.solicitante.curso }}</v-list-item-subtitle>
          </v-list-item>
          <v-divider class="my-2" />
          <v-list-item>
            <v-row align="center" class="w-100" justify="space-between" no-gutters>
              <v-col cols="auto">
                <v-list-item-title class="font-weight-bold mb-1" style="font-size: 1rem;">Itens Solicitados ({{ emprestimo.itens.length }})</v-list-item-title>
              </v-col>
            </v-row>
            <v-list class="pa-0" density="compact">
              <v-list-item v-for="item in emprestimo.itens" :key="item.id" class="pa-0">
                <v-row align="center" class="ga-2" no-gutters>
                  <v-col cols="auto"><v-icon size="16">mdi-cube-outline</v-icon></v-col>
                  <v-col cols="auto" style="font-size: 0.95rem;">{{ item.nomeItem || item.estoqueId }}</v-col>
                  <v-col class="ml-auto" cols="auto" style="font-size: 0.9rem;">Qtd: {{ item.quantidadeItem }}</v-col>
                </v-row>
              </v-list-item>
            </v-list>
          </v-list-item>
          <v-divider class="my-2" />
          <v-list-item>
            <v-list-item-title class="font-weight-bold mb-1" style="font-size: 1rem;">Histórico e Prazos</v-list-item-title>
            <v-list class="pa-0" density="compact">
              <v-list-item class="py-1">
                <v-row align="center" class="ga-2" no-gutters>
                  <v-col cols="auto"><v-icon size="16">mdi-calendar-blank-outline</v-icon></v-col>
                  <v-col cols="auto" style="font-size: 0.95rem;">Solicitação enviada</v-col>
                  <v-col class="ml-auto" cols="auto" style="font-size: 0.9rem;">{{ emprestimo.dataSolicitacao }}</v-col>
                </v-row>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataAprovacao" class="py-1">
                <v-row align="center" class="ga-2" no-gutters>
                  <v-col cols="auto"><v-icon size="16">mdi-check-circle-outline</v-icon></v-col>
                  <v-col cols="auto" style="font-size: 0.95rem;">Solicitação aprovada</v-col>
                  <v-col class="ml-auto" cols="auto" style="font-size: 0.9rem;">{{ emprestimo.dataAprovacao }}</v-col>
                </v-row>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataDevolucao" class="py-1">
                <v-row align="center" class="ga-2" no-gutters>
                  <v-col cols="auto"><v-icon size="16">mdi-calendar-check</v-icon></v-col>
                  <v-col cols="auto" style="font-size: 0.95rem;">Devolução dos itens</v-col>
                  <v-col class="ml-auto" cols="auto" style="font-size: 0.9rem;">{{ emprestimo.dataDevolucao }}</v-col>
                </v-row>
              </v-list-item>

              <v-list-item v-if="emprestimo.dataAtualizacao" class="py-1">
                <v-row align="center" class="ga-2" no-gutters>
                  <v-col cols="auto"><v-icon size="16">mdi-update</v-icon></v-col>
                  <v-col cols="auto" style="font-size: 0.95rem;">Última atualização</v-col>
                  <v-col class="ml-auto" cols="auto" style="font-size: 0.9rem;">{{ emprestimo.dataAtualizacao }}</v-col>
                </v-row>
              </v-list-item>
            </v-list>
          </v-list-item>
          <v-divider class="my-2" />
          <v-list-item>
            <v-list-item-title class="font-weight-bold mb-1" style="font-size: 1rem;">Observações</v-list-item-title>
            <span class="text-body-medium" style="white-space: pre-line; word-break: break-word; font-size: 0.92rem;">{{ emprestimo.observacoes }}</span>
          </v-list-item>
        </v-list>
      </v-card-text>
      <template #actions>
        <div class="d-flex ga-2 w-100 px-2 pb-2">
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
        cancel-text="Cancelar"
        confirm-text="Continuar"
        :is-loading="isLoading"
        message="Ao devolver os itens e editar este empréstimo, o pedido de empréstimo atual será excluído. Deseja continuar?"
        title="Confirmar Devolução e Exclusão"
        @cancel="showConfirm = false"
        @confirm="handleConfirmEdit"
      />
    </AppCard>
  </v-dialog>
</template>
