<script setup lang="ts">
  import { ref } from 'vue'
  import ItemCard from '@/components/inventario/ItemCard.vue'
  import QuantityDialog, { type QuantityDialogItem } from '@/components/inventario/QuantityDialog.vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppSearchBar from '@/components/ui/AppSearchBar.vue'
  import AppSelect from '@/components/ui/AppSelect.vue'
  import { useCartStore } from '@/stores/cart'

  const search = ref('')
  const selectedType = ref('todos')
  const selectedStatus = ref('todos')
  const cartStore = useCartStore()

  const showQuantityModal = ref(false)
  const selectedItemForQuantity = ref<(QuantityDialogItem & { full: any }) | null>(null)

  const typeItems = [
    { label: 'Todos os tipos', value: 'todos' },
    { label: 'Componente', value: 'componente' },
    { label: 'Equipamento', value: 'equipamento' },
  ]

  const statusItems = [
    { label: 'Todos', value: 'todos' },
    { label: 'Ativo', value: 'ativo' },
    { label: 'Inativo', value: 'inativo' },
  ]

  // Componentes Eletrônicos (Resistores e mais)
  const componentesEletronicos = [
    { title: 'Resistor 10kΩ', category: 'Componente Eletrônico', available: 150, total: 300 },
    { title: 'Resistor 1kΩ', category: 'Componente Eletrônico', available: 200, total: 350 },
    { title: 'Resistor 100Ω', category: 'Componente Eletrônico', available: 175, total: 280 },
    { title: 'Resistor 470Ω', category: 'Componente Eletrônico', available: 120, total: 250 },
    { title: 'Resistor 22kΩ', category: 'Componente Eletrônico', available: 90, total: 200 },
    { title: 'Capacitor 100µF', category: 'Componente Eletrônico', available: 85, total: 150 },
    { title: 'Capacitor 10µF', category: 'Componente Eletrônico', available: 110, total: 200 },
    { title: 'LED Vermelho 5mm', category: 'Componente Eletrônico', available: 250, total: 500 },
    { title: 'LED Verde 5mm', category: 'Componente Eletrônico', available: 180, total: 400 },
    { title: 'Transistor 2N2222', category: 'Componente Eletrônico', available: 45, total: 100 },
    { title: 'Diodo 1N4007', category: 'Componente Eletrônico', available: 120, total: 250 },
    { title: 'Protoboard 830 pontos', category: 'Componente Eletrônico', available: 8, total: 15 },
  ]

  const equipamentos = [
    { title: 'Multímetro Digital', category: 'Equipamento de Medição', available: 5, total: 12, icon: 'mdi-devices', iconColor: 'orange' },
    { title: 'Osciloscópio Digital', category: 'Equipamento de Medição', available: 2, total: 5, icon: 'mdi-wave', iconColor: 'blue' },
    { title: 'Fonte de Bancada', category: 'Equipamento de Medição', available: 3, total: 8, icon: 'mdi-power-plug', iconColor: 'red' },
    { title: 'Alicate de Corte', category: 'Ferramenta', available: 6, total: 10, icon: 'mdi-hammer-wrench', iconColor: 'amber' },
    { title: 'Soldador de Estanho', category: 'Ferramenta', available: 4, total: 7, icon: 'mdi-fire', iconColor: 'orange' },
    { title: 'Kit Arduino Uno', category: 'Equipamento', available: 7, total: 15, icon: 'mdi-chip', iconColor: 'teal' },
    { title: 'Sensor Ultrassônico', category: 'Sensor', available: 9, total: 20, icon: 'mdi-radar', iconColor: 'purple' },
    { title: 'Módulo Relé 5V', category: 'Módulo Eletrônico', available: 11, total: 25, icon: 'mdi-electric-switch', iconColor: 'green' },
  ]

  function handleAddComponentToCart (item: typeof componentesEletronicos[0]) {
    selectedItemForQuantity.value = {
      ...item,
      full: item,
    }
    showQuantityModal.value = true
  }

  function handleAddEquipmentToCart (item: typeof equipamentos[0]) {
    selectedItemForQuantity.value = {
      ...item,
      full: item,
    }
    showQuantityModal.value = true
  }

  function handleQuantityConfirm (quantity: number) {
    if (!selectedItemForQuantity.value) return

    const item = selectedItemForQuantity.value.full
    if (item.icon) {
      cartStore.addItem({
        title: item.title,
        category: item.category,
        available: item.available,
        total: item.total,
        icon: item.icon,
        iconColor: item.iconColor,
      }, quantity)
    } else {
      cartStore.addItem({
        title: item.title,
        category: item.category,
        available: item.available,
        total: item.total,
      }, quantity)
    }

    selectedItemForQuantity.value = null
  }
</script>

<template>
  <AppPage subtitle="Consulte itens disponíveis e detalhes de estoque" title="Inventário">
    <v-row class="mb-6" density="comfortable">
      <v-col cols="12" md="6">
        <AppSearchBar
          v-model="search"
          clearable
          hide-details
          placeholder="Buscar por nome ou descrição..."
        />
      </v-col>

      <v-col cols="12" md="3" sm="6">
        <AppSelect
          v-model="selectedType"
          :clearable="false"
          hide-details
          item-title="label"
          item-value="value"
          :items="typeItems"
        />
      </v-col>

      <v-col cols="12" md="3" sm="6">
        <AppSelect
          v-model="selectedStatus"
          :clearable="false"
          hide-details
          item-title="label"
          item-value="value"
          :items="statusItems"
        />
      </v-col>
    </v-row>

    <v-row class="mb-6" density="comfortable">
      <v-col
        v-for="item in componentesEletronicos"
        :key="item.title"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <ItemCard
          :available="item.available"
          :category="item.category"
          :title="item.title"
          :total="item.total"
          @request-quantity="handleAddComponentToCart(item)"
        />
      </v-col>
    </v-row>

    <v-row class="mb-6" density="comfortable">
      <v-col
        v-for="item in equipamentos"
        :key="item.title"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <ItemCard
          :available="item.available"
          :category="item.category"
          :icon="item.icon"
          :icon-color="item.iconColor"
          :title="item.title"
          :total="item.total"
          @request-quantity="handleAddEquipmentToCart(item)"
        />
      </v-col>
    </v-row>

    <QuantityDialog
      v-model="showQuantityModal"
      :item="selectedItemForQuantity"
      @confirm="handleQuantityConfirm"
    />
  </AppPage>
</template>
