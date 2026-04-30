/**
 * router/index.ts
 *
 * Manual route management
 */

// Composables
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Pages
import HomePage from '@/pages/index.vue'
import LoginPage from '@/pages/auth/login.vue'
import RegistroPage from '@/pages/auth/registro.vue'
import DashboardPage from '@/pages/dashboard/index.vue'
import EstoquePage from '@/pages/estoque/index.vue'
import PedidosPage from '@/pages/pedidos/index.vue'
import RelatoriosPage from '@/pages/relatorios/index.vue'
import RelatoriosViewPage from '@/pages/relatorios/view/index.vue'
import NotFoundPage from '@/pages/[...all].vue'

const routes = [
  {
    path: '/',
    component: HomePage,
    name: 'Home',
    meta: { layout: 'blank' }
  },
  {
    path: '/auth/login',
    component: LoginPage,
    name: 'Login',
    meta: { layout: 'blank' }
  },
  {
    path: '/auth/registro',
    component: RegistroPage,
    name: 'Registro',
    meta: { layout: 'blank' }
  },
  {
    path: '/dashboard',
    component: DashboardPage,
    name: 'Dashboard',
  },
  {
    path: '/estoque',
    component: EstoquePage,
    name: 'Estoque',
  },
  {
    path: '/pedidos',
    component: PedidosPage,
    name: 'Pedidos',
  },
  {
    path: '/relatorios',
    component: RelatoriosPage,
    name: 'Relatorios',
  },
  {
    path: '/relatorios/visualizar',
    component: RelatoriosViewPage,
    name: 'RelatoriosVisualizar',
    meta: { layout: 'blank' }
  },
  {
    path: '/:pathMatch(.*)*',
    component: NotFoundPage,
    name: 'NotFound',
    meta: { layout: 'blank' }
  },
]

const router = createRouter({
  history: createWebHistory(String(import.meta.env.BASE_URL)),
  routes,
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  const isAuthRoute = to.path.startsWith('/auth')
  const isPublicRoot = to.path === '/'

  if (!authStore.isAuthenticated && !isAuthRoute && !isPublicRoot) {
    return '/auth/login'
  } else if (authStore.isAuthenticated && isAuthRoute) {
    return '/'
  }
})

export default router
