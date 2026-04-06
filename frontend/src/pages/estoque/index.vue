<script setup lang="ts">
  import { computed, onMounted, ref } from 'vue'
  import type { Estoque } from '@/types/entities'
  import type { EstoqueEditDialogItem, EstoqueFormValues } from '@/components/estoque/types'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppButton from '@/components/ui/AppButton.vue'
  import AppSearchBar from '@/components/ui/AppSearchBar.vue'
  import AppSelect from '@/components/ui/AppSelect.vue'
  import ConfirmDialog from '@/components/common/ConfirmDialog.vue'
  import EstoqueEditDialog from '@/components/estoque/EstoqueEditDialog.vue'
  import ItemCard from '@/components/estoque/ItemCard.vue'
  import QuantityDialog from '@/components/estoque/QuantityDialog.vue'
  import { createEstoque, getEstoques, updateEstoque } from '@/services/estoque'
  import { useCartStore } from '@/stores/cart'
  import { useAuthStore } from '@/stores/auth'

  const search = ref('')
  const selectedType = ref('todos')
  const selectedStatus = ref('todos')

  const showQuantityModal = ref(false)
  const showEditDialog = ref(false)
  const showLockDialog = ref(false)
  const items = ref<any[]>([])
  const loading = ref(false)
  const savingItem = ref(false)
  const togglingLock = ref(false)
  const selectedItemForQuantity = ref<any | undefined>()
  const selectedItemForEdit = ref<EstoqueEditDialogItem | undefined>()
  const selectedItemForLock = ref<any | undefined>()
  const editMode = ref<'create' | 'edit'>('edit')
  const snackbar = ref({
    show: false,
    text: '',
    color: 'success',
  })

  const typeItems = [
    { label: 'Todos os tipos', value: 'todos' },
    { label: 'Componente', value: 'componente' },
    { label: 'Equipamento', value: 'equipamento' },
  ]

  const statusItems = [
    { label: 'Todos', value: 'todos' },
    { label: 'Ativo', value: 'ativo' },
    { label: 'Bloqueado', value: 'inativo' },
  ]

  function normalizeItem (item: Estoque) {
    const isBlocked = item.is_ativado === false

    return {
      id: item.id,
      title: item.nome || 'Sem nome',
      description: '',
      category: item.tipo === 'EQUIPAMENTO' ? 'Equipamento' : 'Componente',
      total: item.quantidade,
      available: item.quantidade,
      status: isBlocked ? 'Bloqueado' : 'Ativo',
      statusColor: isBlocked ? 'warning' : 'success',
      icon: item.tipo === 'EQUIPAMENTO' ? 'mdi-desktop-tower' : 'mdi-chip',
      iconColor: 'primary',
      isBlocked,
      raw: item,
    }
  }

  function showFeedback (text: string, color = 'success') {
    snackbar.value = {
      show: true,
      text,
      color,
    }
  }

  async function loadItems () {
    loading.value = true
    try {
      const data = await getEstoques()
      items.value = (data || []).map(normalizeItem)
    } catch (err) {
      console.error('Erro carregando estoques', err)
      showFeedback('Nao foi possivel carregar o estoque.', 'error')
    } finally {
      loading.value = false
    }
  }

  onMounted(loadItems)

  const itemsFiltrados = computed(() => {
    const q = (search.value || '').trim().toLowerCase()

    return items.value.filter(item => {
      if (selectedType.value !== 'todos') {
        const wanted = selectedType.value === 'componente' ? 'Componente' : 'Equipamento'
        if (item.category !== wanted) return false
      }

      if (selectedStatus.value !== 'todos') {
        const wantActive = selectedStatus.value === 'ativo'
        if ((item.status === 'Ativo') !== wantActive) return false
      }

      if (!q) return true
      return (item.title || '').toLowerCase().includes(q) || (item.description || '').toLowerCase().includes(q)
    })
  })

  function openQuantity (item: any) {
    if (item.isBlocked) {
      showFeedback('Este item esta bloqueado e nao pode ser solicitado.', 'warning')
      return
    }

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

  function openCreateDialog () {
    editMode.value = 'create'
    selectedItemForEdit.value = undefined
    showEditDialog.value = true
  }

  function openEditDialog (item: any) {
    editMode.value = 'edit'
    selectedItemForEdit.value = {
      id: item.id,
      nome: item.raw.nome,
      quantidade: item.raw.quantidade,
      tipo: item.raw.tipo,
      is_ativado: item.raw.is_ativado,
      ativo: item.raw.is_ativado !== false,
    }
    showEditDialog.value = true
  }

  function openLockDialog (item: any) {
    selectedItemForLock.value = item
    showLockDialog.value = true
  }

  function upsertItem (estoque: Estoque) {
    const normalized = normalizeItem(estoque)
    const existingIndex = items.value.findIndex(item => item.id === estoque.id)

    if (existingIndex >= 0) {
      items.value.splice(existingIndex, 1, normalized)
      return
    }

    items.value.unshift(normalized)
  }

  async function handleSaveItem (payload: EstoqueFormValues) {
    savingItem.value = true

    try {
      const { ativo, ...restPayload } = payload

      if (editMode.value === 'create') {
        const created = await createEstoque({
          id: crypto.randomUUID(),
          ...restPayload,
          is_ativado: ativo,
        })
        upsertItem(created)
        showFeedback('Item criado com sucesso.')
      } else if (selectedItemForEdit.value) {
        const updated = await updateEstoque(selectedItemForEdit.value.id, {
          ...restPayload,
          is_ativado: ativo,
        })
        upsertItem(updated)
        showFeedback('Item atualizado com sucesso.')
      }

      showEditDialog.value = false
      selectedItemForEdit.value = undefined
    } catch (err) {
      console.error('Erro ao salvar item', err)
      showFeedback('Nao foi possível salvar o item.', 'error')
    } finally {
      savingItem.value = false
    }
  }

  async function handleToggleLock () {
    if (!selectedItemForLock.value) return

    togglingLock.value = true
    try {
      const raw = selectedItemForLock.value.raw as Estoque
      const updated = await updateEstoque(selectedItemForLock.value.id, {
        nome: raw.nome,
        quantidade: raw.quantidade,
        tipo: raw.tipo,
        is_ativado: raw.is_ativado === false,
      })

      upsertItem(updated)
      showFeedback(updated.is_ativado === false ? 'Item bloqueado com sucesso.' : 'Item desbloqueado com sucesso.')
      showLockDialog.value = false
      selectedItemForLock.value = undefined
    } catch (err) {
      console.error('Erro ao alterar bloqueio do item', err)
      showFeedback('Nao foi possivel alterar o bloqueio do item.', 'error')
    } finally {
      togglingLock.value = false
    }
  }

  const authStore = useAuthStore()
  const isAdministrador = computed(() => authStore.userRole === 'ADMIN' || authStore.userRole === 'BOLSISTA')
</script>

<template>
  <AppPage
    empty-text="Ajuste os filtros ou tente uma nova busca."
    empty-title="Nenhum item encontrado"
    icon="mdi-package-variant-closed-remove"
    :is-empty="!loading && itemsFiltrados.length === 0"
    :loading="loading"
    subtitle="Consulte itens disponiveis e detalhes de estoque"
    title="Estoque"
  >
    <template v-if="isAdministrador" #actions>
      <AppButton
        class="text-none"
        color="primary"
        prepend-icon="mdi-plus"
        rounded="md"
        variant="flat"
        @click="openCreateDialog"
      >
        Novo Item
      </AppButton>
    </template>

    <v-row class="mb-6" density="comfortable">
      <v-col cols="12" md="6">
        <AppSearchBar
          v-model="search"
          clearable
          hide-details
          placeholder="Buscar por nome..."
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
          :is-blocked="item.isBlocked"
          :status="item.status"
          :status-color="item.statusColor"
          :title="item.title"
          :total="item.total"
          @edit="openEditDialog(item)"
          @request-quantity="openQuantity(item)"
          @toggle-lock="openLockDialog(item)"
        />
      </v-col>
    </v-row>

    <QuantityDialog
      v-model="showQuantityModal"
      :item="selectedItemForQuantity"
      @confirm="handleQuantityConfirm"
    />

    <EstoqueEditDialog
      v-model="showEditDialog"
      :is-loading="savingItem"
      :item="selectedItemForEdit"
      :mode="editMode"
      @submit="handleSaveItem"
    />

    <ConfirmDialog
      v-model="showLockDialog"
      :confirm-text="selectedItemForLock?.isBlocked ? 'Desbloquear' : 'Bloquear'"
      :is-loading="togglingLock"
      :message="selectedItemForLock?.isBlocked
        ? `Deseja desbloquear o item ${selectedItemForLock?.title}?`
        : `Deseja bloquear o item ${selectedItemForLock?.title}?`"
      :title="selectedItemForLock?.isBlocked ? 'Desbloquear item' : 'Bloquear item'"
      @confirm="handleToggleLock"
    />

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      location="top right"
      timeout="3000"
    >
      {{ snackbar.text }}
    </v-snackbar>
  </AppPage>
</template>
