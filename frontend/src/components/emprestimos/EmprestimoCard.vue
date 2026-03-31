<script setup lang="ts">
  import { computed } from 'vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'

  interface EmprestimoItem {
    id?: string | number
    name: string
    quantity: number
    icon?: string
  }

  interface EmprestimoCardProps {
    title?: string
    subtitle?: string
    cardClass?: string
    contentClass?: string
    headerClass?: string
    color?: string
    status?: string
    statusColor?: string
    personIcon?: string
    items?: EmprestimoItem[]
    itemsLabel?: string
    requestedAt?: string
    requestedAtLabel?: string
    returnedAt?: string
    returnedAtLabel?: string
    notes?: string
    notesLabel?: string
    buttonText?: string
    showButton?: boolean
    buttonDisabled?: boolean
    emptyItemsText?: string
  }

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
    (e: 'details'): void
    (e: 'edit'): void
    (e: 'delete'): void
  }>()

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
          <v-icon color="info" size="18">mdi-calendar-check</v-icon>
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
                @click="$emit('edit')"
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
                @click="$emit('delete')"
              >
                <v-icon size="18">mdi-delete</v-icon>
              </AppButton>
            </template>
          </v-tooltip>
        </div>
      </div>
    </template>
  </AppCard>
</template>
