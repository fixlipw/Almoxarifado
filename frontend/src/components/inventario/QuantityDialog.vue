<script setup lang="ts">
  import type { QuantityDialogProps } from '@/types'
  import { ref } from 'vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'

  const props = withDefaults(defineProps<QuantityDialogProps>(), {
    item: undefined,
  })

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'confirm': [quantity: number]
  }>()

  const quantity = ref(1)

  function handleConfirm () {
    emit('confirm', quantity.value)
    quantity.value = 1
    emit('update:modelValue', false)
  }

  function handleClose () {
    quantity.value = 1
    emit('update:modelValue', false)
  }

  function decreaseQuantity () {
    if (quantity.value > 1) {
      quantity.value--
    }
  }

  function increaseQuantity () {
    if (props.item && quantity.value < props.item.available) {
      quantity.value++
    }
  }
</script>

<template>
  <v-dialog
    max-width="400"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <AppCard v-if="item" content-class="pa-3" header-class="pb-2">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-title-medium font-weight-bold" style="font-size: 1.1rem;">Quantidade</v-card-title>
          </v-col>
          <v-col cols="auto">
            <v-btn flat icon size="small" @click="handleClose"><v-icon size="18">mdi-close</v-icon></v-btn>
          </v-col>
        </v-row>
      </template>

      <v-divider class="my-2" />

      <v-card-text>
        <div class="d-flex align-center ga-3 mb-4">
          <v-avatar
            :color="item.iconColor || 'primary'"
            rounded="lg"
            size="40"
            variant="tonal"
          >
            <v-icon :color="item.iconColor || 'primary'" size="24">
              {{ item.icon || 'mdi-package-variant-closed' }}
            </v-icon>
          </v-avatar>

          <div class="flex-grow-1">
            <div class="font-weight-bold text-body-medium">{{ item.title }}</div>
            <div class="text-body-small text-medium-emphasis">{{ item.category }}</div>
          </div>
        </div>

        <div class="d-flex flex-column align-center">
          <div class="text-body-small text-medium-emphasis mb-3">Selecione a quantidade</div>

          <div class="d-flex align-center justify-center ga-3">
            <v-btn
              :disabled="quantity <= 1"
              flat
              icon
              size="large"
              @click="decreaseQuantity"
            >
              <v-icon>mdi-minus</v-icon>
            </v-btn>

            <v-card
              class="d-flex align-center justify-center"
              rounded="lg"
              variant="text"
            >
              <v-text-field
                v-model.number="quantity"
                class="text-md-center"
                hide-details
                hide-spin-buttons
                :max="item.available"
                :min="1"
                single-line
                type="number"
                @blur="quantity = Math.max(1, Math.min(quantity, item.available))"
              />
            </v-card>

            <v-btn
              :disabled="quantity >= item.available"
              flat
              icon
              size="large"
              @click="increaseQuantity"
            >
              <v-icon>mdi-plus</v-icon>
            </v-btn>
          </div>

          <div class="text-body-small text-medium-emphasis mt-3">
            Máximo disponível: <span class="font-weight-bold">{{ item.available }}</span> un.
          </div>
        </div>
      </v-card-text>

      <template #actions>
        <AppButton
          block
          variant="text"
          @click="handleClose"
        >
          Cancelar
        </AppButton>
        <AppButton
          block
          color="primary"
          @click="handleConfirm"
        >
          Adicionar ao Carrinho
        </AppButton>
      </template>
    </AppCard>
  </v-dialog>
</template>
