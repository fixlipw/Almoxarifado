<script setup lang="ts">
  import type { EmprestimoCardProps } from '@/types'
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'
  import { type CartItem, useCartStore } from '@/stores/cart'

  const props = withDefaults(defineProps<EmprestimoCardProps>(), {
    title: '',
    subtitle: '',
    cardClass: '',
    contentClass: '',
    headerClass: '',
    color: '',
    status: 'Pendente',
    statusColor: '',
    personIcon: 'mdi-account-outline',
    items: () => [],
    codigo: '',
    itemsLabel: 'Itens',
    requestedAt: '',
    requestedAtLabel: 'Solicitado em',
    returnedAt: '',
    returnedAtLabel: 'Data da devolução',
    notes: '',
    notesLabel: 'Observações',
    buttonText: 'Ver Detalhes',
    showButton: true,
    buttonDisabled: false,
    emptyItemsText: 'Nenhum item neste empréstimo.',
  })

  const emit = defineEmits<{
    (e: 'details' | 'edit' | 'delete'): void
  }>()

  const router = useRouter()
  const cartStore = useCartStore()
  const showConfirm = ref(false)
  const showEditConfirm = ref(false)

  const emprestimoStatus = computed(() => {
    if (!props.status) return 'Pendente'
    return props.status
  })

  const computedStatusColor = computed(() => {
    if (props.statusColor.trim()) {
      return props.statusColor
    }

    const normalizedStatus = props.status.toLowerCase()

    if (normalizedStatus.includes('pendente')) {
      return 'warning'
    }

    if (normalizedStatus.includes('ativo')) {
      return 'success'
    }

    if (normalizedStatus.includes('finalizado') || normalizedStatus.includes('devolvido')) {
      return 'info'
    }

    if (normalizedStatus.includes('atrasado')) {
      return 'error'
    }

    return 'primary'
  })

  function openEditConfirm () {
    showEditConfirm.value = true
  }

  function handleEdit () {
    if (!props.items) return

    // Clear current cart before adding items from loan
    cartStore.clearCart()

    for (const item of props.items) {
      const cartItem: Omit<CartItem, 'id'> & { id: string } = {
        id: item.id.toString(), // Usa o ID real do item
        quantity: item.quantity,
        title: item.name,
        category: 'Empréstimo',
        available: item.quantity,
        total: item.quantity,
        emprestimoId: props.codigo,
      }

      cartStore.addItem(cartItem, item.quantity)
    }

    showEditConfirm.value = false

    // Navegar para inventário
    router.push('/inventario')
    emit('delete')
  }

  function openDeleteConfirm () {
    showConfirm.value = true
  }

  function handleConfirmDelete () {
    emit('delete')
    showConfirm.value = false
  }
</script>

<template>
  <AppCard
    card-class="h-100"
    :class="props.cardClass"
    :color="props.color"
    content-class="pa-4"
    :header-class="props.headerClass"
  >
    <template #header>
      <v-row align="start" class="mb-2" justify="space-between" no-gutters>
        <v-col class="d-flex align-start ga-2" cols="auto">
          <v-icon class="mt-1" size="18">{{ props.personIcon }}</v-icon>
          <v-list-item class="pa-0" min-height="0">
            <v-list-item-title class="font-weight-bold text-wrap">{{ props.title }}</v-list-item-title>
            <v-list-item-subtitle v-if="props.subtitle" class="text-body-small">{{ props.subtitle }}</v-list-item-subtitle>
          </v-list-item>
        </v-col>

        <v-col cols="auto">
          <v-chip
            v-if="props.status"
            class="font-weight-bold"
            :color="computedStatusColor"
            size="small"
            variant="text"
          >
            {{ props.status }}
          </v-chip>
        </v-col>
      </v-row>
    </template>

    <v-list :class="['pa-0 bg-transparent d-flex flex-column ga-4 mt-2', props.contentClass]" density="compact" lines="one">
      <v-list-item class="pa-0" min-height="0">
        <template #prepend>
          <v-icon size="18">mdi-cube-outline</v-icon>
        </template>
        <v-list-item-title class="text-title-medium font-weight-bold">
          {{ props.itemsLabel }} ({{ props.items.length }})
        </v-list-item-title>
      </v-list-item>

      <v-list-item v-if="props.requestedAt" class="pa-0" min-height="0">
        <template #prepend>
          <v-icon class="mt-1" size="18">mdi-calendar-blank-outline</v-icon>
        </template>
        <v-list-item-subtitle class="text-body-large">{{ props.requestedAtLabel }}</v-list-item-subtitle>
        <v-list-item-title class="text-body-small font-weight-bold">{{ props.requestedAt }}</v-list-item-title>

      </v-list-item>
      <v-list-item v-if="emprestimoStatus === 'Finalizado' && props.returnedAt" class="pa-0" min-height="0">
        <template #prepend>
          <v-icon class="mt-1" size="18">mdi-calendar-check</v-icon>
        </template>
        <v-list-item-subtitle class="text-body-large">{{ props.returnedAtLabel }}</v-list-item-subtitle>
        <v-list-item-title class="text-body-small font-weight-bold">{{ props.returnedAt }}</v-list-item-title>
      </v-list-item>
    </v-list>

    <template #actions>
      <div class="d-flex align-center justify-center w-100 ga-2">
        <AppButton
          v-if="props.showButton"
          :class="emprestimoStatus === 'Pendente' ? '' : 'd-flex flex-column flex-grow-1'"
          color="primary"
          :disabled="props.buttonDisabled"
          @click="$emit('details')"
        >
          {{ props.buttonText }}
        </AppButton>
        <div v-if="emprestimoStatus === 'Pendente'" class="d-flex ga-2">
          <v-tooltip location="bottom" text="Editar">
            <template #activator="{ props: tooltipProps }">
              <AppButton
                v-bind="tooltipProps"
                class="px-2"
                color="primary"
                icon
                size="small"
                variant="text"
                @click="openEditConfirm"
              >
                <v-icon size="18">mdi-pencil</v-icon>
              </AppButton>
            </template>
          </v-tooltip>
          <v-tooltip location="bottom" text="Excluir">
            <template #activator="{ props: tooltipProps }">
              <AppButton
                v-bind="tooltipProps"
                class="px-2"
                color="error"
                icon
                size="small"
                variant="text"
                @click="openDeleteConfirm"
              >
                <v-icon size="18">mdi-delete</v-icon>
              </AppButton>
            </template>
          </v-tooltip>
        </div>
      </div>
    </template>

    <ConfirmDialog
      v-model="showConfirm"
      cancel-text="Cancelar"
      confirm-text="Excluir"
      message="Tem certeza que deseja excluir este empréstimo?"
      title="Excluir Empréstimo"
      @cancel="showConfirm = false"
      @confirm="handleConfirmDelete"
    />

    <ConfirmDialog
      v-model="showEditConfirm"
      cancel-text="Cancelar"
      confirm-text="Continuar"
      message="Ao editar este empréstimo, o pedido atual será excluído e seus itens voltarão ao carrinho. Deseja continuar?"
      title="Editar Empréstimo"
      @cancel="showEditConfirm = false"
      @confirm="handleEdit"
    />
  </AppCard>
</template>
