import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export interface CartItem {
  id: string
  title: string
  category: string
  quantity: number
  available: number
  total: number
  icon?: string
  iconColor?: string
  emprestimoId?: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

  const itemCount = computed(() =>
    items.value.reduce((total, item) => total + item.quantity, 0),
  )

  const totalItems = computed(() => items.value.length)

  function addItem (item: Omit<CartItem, 'id'> & { id?: string }, quantity = 1) {
    // Gera ID baseado em title e category para agrupar itens duplicados
    const id = `${item.title}-${item.category}`
    // Se for empréstimo, inclui o emprestimoId na busca para não agrupar empréstimos diferentes
    const existingItem = items.value.find(i =>
      i.id === id && i.emprestimoId === item.emprestimoId,
    )

    if (existingItem) {
      // Se item já existe, incrementa quantidade respeitando máximo
      const newQuantity = existingItem.quantity + quantity
      existingItem.quantity = Math.min(newQuantity, item.available)
    } else {
      // Se é novo item, adiciona com quantidade especificada
      items.value.push({
        ...item,
        id,
        quantity: Math.min(quantity, item.available),
      } as CartItem)
    }
  }

  function removeItem (id: string) {
    items.value = items.value.filter(item => item.id !== id)
  }

  function updateQuantity (id: string, quantity: number) {
    const item = items.value.find(i => i.id === id)
    if (item && quantity > 0 && quantity <= item.available) {
      item.quantity = quantity
    }
  }

  function clearCart () {
    items.value = []
  }

  return {
    items,
    itemCount,
    totalItems,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
  }
})
