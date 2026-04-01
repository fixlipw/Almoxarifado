<script setup lang="ts">
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'

  interface ConfirmDialogProps {
    modelValue: boolean
    title?: string
    message?: string
    confirmText?: string
    cancelText?: string
    isLoading?: boolean
  }

  const _props = withDefaults(defineProps<ConfirmDialogProps>(), {
    title: 'Confirmação',
    message: 'Tem certeza que deseja prosseguir?',
    confirmText: 'Confirmar',
    cancelText: 'Cancelar',
    isLoading: false,
  })

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'confirm': []
    'cancel': []
  }>()

  function handleConfirm () {
    emit('confirm')
  }

  function handleCancel () {
    emit('cancel')
    emit('update:modelValue', false)
  }
</script>

<template>
  <v-dialog
    max-width="400"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <AppCard content-class="pa-3" header-class="pb-2">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-subtitle-1 font-weight-bold" style="font-size: 1.1rem;">{{ title }}</v-card-title>
          </v-col>
          <v-col cols="auto">
            <v-btn
              :disabled="isLoading"
              flat
              icon
              size="small"
              @click="handleCancel"
            ><v-icon size="18">mdi-close</v-icon></v-btn>
          </v-col>
        </v-row>
      </template>

      <v-divider class="my-2" />

      <v-card-text class="py-6">
        <div class="d-flex flex-column align-center text-center">
          <v-icon class="mb-3" color="warning" size="64">mdi-alert-circle-outline</v-icon>
          <div class="text-body-large">{{ message }}</div>
        </div>
      </v-card-text>

      <template #actions>
        <AppButton
          block
          :disabled="isLoading"
          variant="text"
          @click="handleCancel"
        >
          {{ cancelText }}
        </AppButton>
        <AppButton
          block
          color="warning"
          :loading="isLoading"
          @click="handleConfirm"
        >
          {{ confirmText }}
        </AppButton>
      </template>
    </AppCard>
  </v-dialog>
</template>
