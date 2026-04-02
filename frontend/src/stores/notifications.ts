import { defineStore } from 'pinia'

export interface NotificationAction {
  label: string
  handler: () => void
}

export interface Notification {
  id: number
  message: string
  color: 'success' | 'error' | 'info' | 'warning'
  timeout?: number
}

let nextId = 0

export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    notifications: [] as Notification[]
  }),

  actions: {
    addNotification(message: string, color: 'success' | 'error' | 'info' | 'warning' = 'info', timeout = 3000) {
      const id = ++nextId
      this.notifications.push({ id, message, color, timeout })

      setTimeout(() => {
        this.removeNotification(id)
      }, timeout)
    },

    info(message: string, timeout = 3000) {
      this.addNotification(message, 'info', timeout)
    },

    success(message: string, timeout = 3000) {
      this.addNotification(message, 'success', timeout)
    },

    error(message: string, timeout = 5000) {
      this.addNotification(message, 'error', timeout)
    },

    removeNotification(id: number) {
      this.notifications = this.notifications.filter(n => n.id !== id)
    }
  }
})

export default useNotificationStore

