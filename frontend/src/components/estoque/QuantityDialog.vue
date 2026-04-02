<script lang="ts" setup>
import type {QuantityDialogProps} from "@/components/estoque/types.ts";

import {ref} from 'vue'
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

function handleConfirm() {
  emit('confirm', quantity.value)
  quantity.value = 1
  emit('update:modelValue', false)
}

function handleClose() {
  quantity.value = 1
  emit('update:modelValue', false)
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

function increaseQuantity() {
  if (props.item && quantity.value < props.item.available) {
    quantity.value++
  }
}
</script>

<template>
  <v-dialog
      :model-value="modelValue"
      max-width="300"
      @update:model-value="$emit('update:modelValue', $event)"
  >
    <AppCard v-if="item" content-class="pa-3" header-class="pb-2">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-title-medium font-weight-bold" style="font-size: 1.1rem;">Quantidade
            </v-card-title>
          </v-col>
          <v-col cols="auto">
            <v-btn flat icon size="small" @click="handleClose">
              <v-icon size="18">mdi-close</v-icon>
            </v-btn>
          </v-col>
        </v-row>
      </template>

      <v-divider class="my-2"/>

      <v-card-text>
        <div class="d-flex align-center mb-4">
          <v-avatar :color="item.iconColor || 'primary'" rounded size="40" variant="tonal">
            <v-icon :color="item.iconColor || 'primary'" size="24">{{
                item.icon || 'mdi-package-variant-closed'
              }}
            </v-icon>
          </v-avatar>
          <div class="ml-3">
            <div class="font-weight-bold">{{ item.title }}</div>
            <div class="text-body-small text-medium-emphasis">{{ item.category }}</div>
          </div>
        </div>

        <div class="text-center">
          <div class="text-body-small text-medium-emphasis mb-3">Selecione a quantidade</div>

          <div class="d-flex align-center justify-center">
            <v-btn :disabled="quantity <= 1" icon @click="decreaseQuantity">
              <v-icon>mdi-minus</v-icon>
            </v-btn>

            <div class="text-body-medium font-weight-bold" style="min-width: 60px; text-align: center;">
              {{ quantity }}
            </div>

            <v-btn :disabled="quantity >= item.available" icon @click="increaseQuantity">
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
