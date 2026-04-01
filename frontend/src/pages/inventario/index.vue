<script setup lang="ts">
  import type { EstoqueResponse, QuantityDialogItem } from '@/types'
  import { computed, onMounted, ref } from 'vue'
  import { api } from '@/api'
  import ItemCard from '@/components/inventario/ItemCard.vue'
  import QuantityDialog from '@/components/inventario/QuantityDialog.vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppSearchBar from '@/components/ui/AppSearchBar.vue'
  import AppSelect from '@/components/ui/AppSelect.vue'
  import { useCartStore } from '@/stores/cart'

  const search = ref('')
  const selectedType = ref('todos')
  const selectedStatus = ref('todos')
  const cartStore = useCartStore()

  const showQuantityModal = ref(false)
  const selectedItemForQuantity = ref<(QuantityDialogItem & { full: any }) | undefined>(undefined)

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

  const estoque = ref<EstoqueResponse[]>([])

  onMounted(async () => {
    try {
      estoque.value = await api.getEstoque()
    } catch (error) {
      console.error('Erro ao buscar estoque:', error)
    }
  })

  // Listas computadas filtradas
  const itemsFiltrados = computed(() => {
    return estoque.value.filter(item => {
      let matcType = true
      if (selectedType.value !== 'todos') {
        matcType = item.tipo === selectedType.value
      }

      let matchStatus = true
      if (selectedStatus.value !== 'todos') {
        matchStatus = selectedStatus.value === 'ativo' ? item.isAtivado : !item.isAtivado
      }

      let matchSearch = true
      if (search.value) {
        matchSearch = item.nome.toLowerCase().includes(search.value.toLowerCase())
      }

      return matcType && matchStatus && matchSearch
    }).map(i => ({
      ...i,
      title: i.nome,
      category: i.tipo === 'COMPONENTE' ? 'Componente Eletrônico' : 'Equipamento',
      available: i.quantidade,
      total: i.quantidade,
      icon: i.tipo === 'EQUIPAMENTO' ? 'mdi-devices' : 'mdi-resistor',
      iconColor: i.tipo === 'EQUIPAMENTO' ? 'blue' : 'orange',
    }))
  })

  function handleAddToCart (item: any) {
    selectedItemForQuantity.value = {
      ...item,
      full: item,
    }
    showQuantityModal.value = true
  }

  function handleQuantityConfirm (quantity: number) {
    if (!selectedItemForQuantity.value) return

    const item = selectedItemForQuantity.value.full
    cartStore.addItem({
      id: item.id,
      quantity,
      title: item.title,
      category: item.category,
      available: item.available,
      total: item.total,
      icon: item.icon,
      iconColor: item.iconColor,
    }, quantity)

    selectedItemForQuantity.value = undefined
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
        v-for="item in itemsFiltrados"
        :key="item.id"
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
          @request-quantity="handleAddToCart(item)"
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
