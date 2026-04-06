<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import type { EstoqueEditDialogProps, EstoqueFormValues } from '@/components/estoque/types'
import AppButton from '@/components/ui/AppButton.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppSelect from '@/components/ui/AppSelect.vue'

const props = withDefaults(defineProps<EstoqueEditDialogProps>(), {
  item: undefined,
  isLoading: false,
  mode: 'edit',
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [payload: EstoqueFormValues]
}>()

const tipoItems = [
  { label: 'Componente', value: 'COMPONENTE' },
  { label: 'Equipamento', value: 'EQUIPAMENTO' },
]

const form = reactive<EstoqueFormValues>({
  nome: '',
  quantidade: 0,
  tipo: 'COMPONENTE',
  ativo: true,
})

const title = computed(() => props.mode === 'create' ? 'Novo item' : 'Editar item')
const submitText = computed(() => props.mode === 'create' ? 'Salvar item' : 'Salvar alterações')
const isValid = computed(() => form.nome.trim().length > 0 && Number.isFinite(form.quantidade) && form.quantidade >= 0)

function syncForm() {
  form.nome = props.item?.nome ?? ''
  form.quantidade = props.item?.quantidade ?? 0
  form.tipo = props.item?.tipo ?? 'COMPONENTE'
  form.ativo = props.item?.is_ativado ?? props.item?.ativo ?? true
}

watch(
  () => [props.modelValue, props.item, props.mode],
  ([isOpen]) => {
    if (isOpen) {
      syncForm()
    }
  },
  { immediate: true },
)

function handleClose() {
  emit('update:modelValue', false)
}

function handleSubmit() {
  if (!isValid.value) return

  emit('submit', {
    nome: form.nome.trim(),
    quantidade: Number(form.quantidade),
    tipo: form.tipo,
    ativo: form.ativo,
  })
}
</script>

<template>
  <v-dialog
    max-width="520"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <AppCard content-class="pa-3" header-class="pb-2">
      <template #header>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-flex align-center" cols="auto">
            <v-card-title class="pa-0 text-title-medium font-weight-bold" style="font-size: 1.1rem;">
              {{ title }}
            </v-card-title>
          </v-col>
          <v-col cols="auto">
            <v-btn
              :disabled="props.isLoading"
              flat
              icon
              size="small"
              @click="handleClose"
            >
              <v-icon size="18">mdi-close</v-icon>
            </v-btn>
          </v-col>
        </v-row>
      </template>

      <v-divider class="my-2" />

      <v-card-text class="px-0 pb-0">
        <v-row density="comfortable">
          <v-col cols="12">
            <v-text-field
              v-model="form.nome"
              :disabled="props.isLoading"
              label="Nome do item"
              rounded="lg"
              variant="outlined"
            />
          </v-col>

          <v-col cols="12" sm="6">
            <v-text-field
              v-model.number="form.quantidade"
              :disabled="props.isLoading"
              label="Quantidade"
              min="0"
              rounded="lg"
              type="number"
              variant="outlined"
            />
          </v-col>

          <v-col cols="12" sm="6">
           <AppSelect
              v-model="form.tipo"
              :clearable="false"
              :disabled="props.isLoading"
              :items="tipoItems"
              item-title="label"
              item-value="value"
              label="Tipo"
            />
          </v-col>
        </v-row>
      </v-card-text>

      <v-divider v-if="props.mode === 'create'" class="my-5" />

      <v-checkbox v-if="props.mode === 'create'"
        v-model="form.ativo"
        :disabled="props.isLoading"
        label="Item ativo e disponível para empréstimo"
        class="mt-0 mb-2"
      />

      <template #actions>
        <AppButton
          block
          :disabled="props.isLoading"
          variant="text"
          @click="handleClose"
        >
          Cancelar
        </AppButton>
        <AppButton
          block
          color="primary"
          :disabled="!isValid"
          :loading="props.isLoading"
          @click="handleSubmit"
        >
          {{ submitText }}
        </AppButton>
      </template>
    </AppCard>
  </v-dialog>
</template>
