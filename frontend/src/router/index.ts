/**
 * router/index.ts
 *
 * Automatic routes for ./src/pages/*.vue
 */

// Composables
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from 'vue-router/auto-routes'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(String(import.meta.env.BASE_URL)),
  routes,
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthRoute = to.path.startsWith('/auth')
  
  if (!authStore.isAuthenticated && !isAuthRoute) {
    next('/auth/login')
  } else if (authStore.isAuthenticated && isAuthRoute) {
    next('/')
  } else {
    next()
  }
})

export default router
