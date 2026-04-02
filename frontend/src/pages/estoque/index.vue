<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppSearchBar from '@/components/ui/AppSearchBar.vue'
  import AppSelect from '@/components/ui/AppSelect.vue'
  import ItemCard from "@/components/estoque/ItemCard.vue";
  import QuantityDialog from "@/components/estoque/QuantityDialog.vue";
  import { useCartStore } from '@/stores/cart'

  const search = ref('')
  const selectedType = ref('todos')
  const selectedStatus = ref('todos')

  const showQuantityModal = ref(false)
  const items = ref<any[]>([])
  const loading = ref(false)
  const selectedItemForQuantity = ref<any | undefined>()

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

  onMounted(async () => {
    loading.value = true
    try {
      const { getEstoques } = await import('@/services/estoque')
      const data = await getEstoques()
      const raw = data as any[]
      items.value = (raw || []).map((i: any) => ({
        id: i.id,
        title: (i.nome as string) || i.title || 'Sem nome',
        description: (i.descricao as string) || '',
        category: (i.tipo === 'EQUIPAMENTO' ? 'Equipamento' : 'Componente'),
        total: typeof (i.quantidade) === 'number' ? i.quantidade : (i.total || 0),
        available: typeof (i.quantidade) === 'number' ? i.quantidade : (i.available || 0),
        status: i.is_ativado === false ? 'Inativo' : 'Ativo',
        statusColor: i.is_ativado === false ? 'error' : 'success',
        icon: i.icon || (i.tipo === 'EQUIPAMENTO' ? 'mdi-desktop-tower' : 'mdi-chip'),
        iconColor: i.iconColor || 'primary',
        raw: i,
      }))
    } catch (err) {
      console.error('Erro carregando estoques', err)
    } finally {
      loading.value = false
    }
  })

  const itemsFiltrados = computed(() => {
    const q = (search.value || '').trim().toLowerCase()
    return items.value.filter(it => {
      if (selectedType.value !== 'todos') {
        const wanted = selectedType.value === 'componente' ? 'Componente' : 'Equipamento'
        if (it.category !== wanted) return false
      }

      if (selectedStatus.value !== 'todos') {
        const wantActive = selectedStatus.value === 'ativo'
        if ((it.status === 'Ativo') !== wantActive) return false
      }

      if (!q) return true
      return (it.title || '').toLowerCase().includes(q) || (it.description || '').toLowerCase().includes(q)
    })
  })

  function openQuantity (item: any) {
    selectedItemForQuantity.value = item
    showQuantityModal.value = true
  }

  const cartStore = useCartStore()

  function handleQuantityConfirm (quantity: number) {
    if (selectedItemForQuantity.value) {
      cartStore.addItem({
        id: selectedItemForQuantity.value.id,
        title: selectedItemForQuantity.value.title,
        category: selectedItemForQuantity.value.category,
        available: selectedItemForQuantity.value.available,
        quantity,
        icon: selectedItemForQuantity.value.icon,
        iconColor: selectedItemForQuantity.value.iconColor,
      })
    }
    showQuantityModal.value = false
    selectedItemForQuantity.value = undefined
  }
</script>

<template>
  <AppPage subtitle="Consulte itens disponíveis e detalhes de estoque" title="Estoque">
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
          @request-quantity="openQuantity(item)"
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
