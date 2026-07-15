import {defineStore} from 'pinia'

export interface CartItem {
    id: string
    title: string
    category?: string
    available: number
    quantity: number
    icon?: string
    iconColor?: string
    emprestimoId?: string | null
}

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: [] as CartItem[],
        checkedOut: false as boolean,
    }),

    getters: {
        count: (state) => state.items.reduce((s, i) => s + i.quantity, 0),
        isEmpty: (state) => state.items.length === 0,
    },

    actions: {
        addItem(item: CartItem) {
            const existing = this.items.find(i => i.id === item.id)
            if (existing) {
                existing.quantity = Math.min(existing.available, existing.quantity + item.quantity)
            } else {
                const quantity = Math.max(1, Math.min(item.quantity || 1, item.available))
                this.items.push({...item, quantity})
            }
        },

        removeItem(id: string) {
            this.items = this.items.filter(i => i.id !== id)
        },

        updateQuantity(id: string, quantity: number) {
            const item = this.items.find(i => i.id === id)
            if (!item) return
            item.quantity = Math.max(1, Math.min(quantity, item.available))
        },

        setCheckout(payload: boolean) {
            this.checkedOut = payload
        },

        clearCart() {
            this.items = []
        },

        setItems(items: CartItem[]) {
            this.items = items.map(i => ({...i, quantity: Math.max(1, Math.min(i.quantity || 1, i.available))}))
        },
    },
})

export default useCartStore

