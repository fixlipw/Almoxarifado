<script setup lang="ts">
  import { computed, ref } from 'vue'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppCard from '@/components/ui/AppCard.vue'
  import useCartStore from "@/stores/cart.ts";

  interface CartDialogProps {
    modelValue: boolean
  }

  defineProps<CartDialogProps>()

  const emit = defineEmits<{
    'update:modelValue': [value: boolean]
    'checkout': []
    'update-loan': []
  }>()

  const cartStore = useCartStore()

  const showConfirm = ref(false)
  const itemIdToRemove = ref<string | undefined>()
  const showClearConfirm = ref(false)

  const totalItems = computed(() =>
    cartStore.items.reduce((sum: any, item: { quantity: any }) => sum + item.quantity, 0),
  )

  const hasPendingLoan = computed(() =>
    cartStore.items.some((item: any) => item.category === 'Empréstimo' && item.emprestimoId),
  )

  function closeDialog () {
    emit('update:modelValue', false)
  }

  function openRemoveConfirm (itemId: string) {
    itemIdToRemove.value = itemId
    showConfirm.value = true
  }

  function handleConfirmRemove () {
    if (itemIdToRemove.value) {
      cartStore.removeItem(itemIdToRemove.value)
      showConfirm.value = false
      itemIdToRemove.value = undefined
      window.location.reload()
    }
  }

  function handleUpdateLoan () {
    emit('update-loan')
    closeDialog()
  }

  function handleClearCartConfirm () {
    cartStore.clearCart()
    showClearConfirm.value = false
  }

  function handleCheckout () {
    emit('checkout')
    closeDialog()
  }
</script>

<template>
  <v-dialog
    max-width="500"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <AppCard content-class="pa-3" header-class="pb-2">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-title-medium font-weight-bold" style="font-size: 1.1rem;">Carrinho de Itens</v-card-title>
            <v-chip class="ml-2" color="primary" size="x-small" style="font-size: 0.8rem;">{{ totalItems }}</v-chip>
          </v-col>
          <v-col cols="auto">
            <v-btn flat icon size="small" @click="closeDialog"><v-icon size="18">mdi-close</v-icon></v-btn>
          </v-col>
        </v-row>
      </template>

      <v-divider class="my-2" />

      <v-card-text style="overflow-y: auto; max-height: 55vh; padding-bottom: 0;">
        <div v-if="cartStore.items.length === 0" class="d-flex flex-column align-center justify-center py-8">
          <v-icon class="mb-2" color="medium-emphasis" size="48">mdi-cart-outline</v-icon>
          <div class="text-body-medium text-medium-emphasis text-center">Seu carrinho está vazio</div>
        </div>

        <v-list v-else class="pa-0" density="compact">
          <template v-for="(item, index) in cartStore.items" :key="item.id">
            <v-list-item class="pa-0">
              <v-row align="center" class="w-100 ga-3" no-gutters>
                <v-col cols="auto">
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
                </v-col>

                <v-col class="flex-grow-1">
                  <div class="font-weight-bold text-body-medium">{{ item.title }}</div>
                  <div class="text-body-small text-medium-emphasis">{{ item.category }}</div>
                  <div class="text-body-small text-medium-emphasis mt-1">Disponível: {{ item.available }} un.</div>
                </v-col>

                <v-col cols="auto">
                  <v-btn
                    flat
                    icon
                    size="x-small"
                    @click="openRemoveConfirm(item.id)"
                  >
                    <v-icon size="16">mdi-delete-outline</v-icon>
                  </v-btn>
                </v-col>

                <v-col cols="auto">
                  <div class="d-flex align-center ga-1">
                    <v-btn
                      :disabled="item.quantity <= 1"
                      flat
                      icon
                      size="x-small"
                      @click="cartStore.updateQuantity(item.id, item.quantity - 1)"
                    >
                      <v-icon size="16">mdi-minus</v-icon>
                    </v-btn>
                    <div class="text-body-medium font-weight-bold" style="min-width: 28px; text-align: center;">
                      {{ item.quantity }}
                    </div>
                    <v-btn
                      :disabled="item.quantity >= item.available"
                      flat
                      icon
                      size="x-small"
                      @click="cartStore.updateQuantity(item.id, item.quantity + 1)"
                    >
                      <v-icon size="16">mdi-plus</v-icon>
                    </v-btn>
                  </div>
                </v-col>
              </v-row>
            </v-list-item>

            <v-divider v-if="index < cartStore.items.length - 1" class="my-2" />
          </template>
        </v-list>
      </v-card-text>

      <template #actions>
        <v-row class="ga-2" no-gutters>
          <!-- Botões para carrinho normal -->
          <template v-if="!hasPendingLoan">
            <v-col cols="12">
              <AppButton
                block
                color="primary"
                :disabled="cartStore.items.length === 0"
                @click="handleCheckout"
              >
                Finalizar Pedido
              </AppButton>
            </v-col>
            <v-col cols="12">
              <AppButton
                block
                class="text-error"
                :disabled="cartStore.items.length === 0"
                @click="showClearConfirm = true"
              >
                Limpar Carrinho
              </AppButton>
            </v-col>
          </template>

          <!-- Botões para empréstimo pendente -->
          <template v-else>
            <v-col cols="12">
              <AppButton
                block
                color="primary"
                @click="handleUpdateLoan"
              >
                Atualizar Pedido
              </AppButton>
            </v-col>
            <v-col cols="12">
              <AppButton
                block
                color="warning"
                @click="handleCheckout"
              >
                Finalizar Atualização
              </AppButton>
            </v-col>
            <v-col cols="12">
              <AppButton
                block
                class="text-error"
                @click="showClearConfirm = true"
              >
                Cancelar Pedido
              </AppButton>
            </v-col>
          </template>
        </v-row>
      </template>

      <ConfirmDialog
        v-model="showConfirm"
        cancel-text="Cancelar"
        confirm-text="Remover"
        message="Tem certeza que deseja remover este item do carrinho?"
        title="Remover Item"
        @cancel="showConfirm = false"
        @confirm="handleConfirmRemove"
      />

      <ConfirmDialog
        v-model="showClearConfirm"
        cancel-text="Voltar"
        :confirm-text="hasPendingLoan ? 'Cancelar' : 'Limpar'"
        :message="hasPendingLoan
          ? 'Tem certeza que deseja cancelar este pedido de empréstimo? Todos os itens serão removidos.'
          : 'Tem certeza que deseja remover TODOS os itens do carrinho? Esta ação não pode ser desfeita.'"
        :title="hasPendingLoan ? 'Cancelar Pedido' : 'Limpar Carrinho'"
        @cancel="showClearConfirm = false"
        @confirm="handleClearCartConfirm"
      />
    </AppCard>
  </v-dialog>
</template>
