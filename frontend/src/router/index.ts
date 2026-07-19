/**
 * router/index.ts
 *
 * Manual route management
 */

// Composables
import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

// Pages
import HomePage from '@/pages/index.vue'
import DashboardPage from '@/pages/dashboard/index.vue'
import EstoquePage from '@/pages/estoque/index.vue'
import PedidosPage from '@/pages/pedidos/index.vue'
import RelatoriosPage from '@/pages/relatorios/index.vue'
import UsuariosPage from '@/pages/usuarios/index.vue'
import RelatoriosViewPage from '@/pages/relatorios/view/index.vue'
import PerfilPage from '@/pages/perfil/index.vue'
import ConfiguracoesPage from '@/pages/configuracoes/index.vue'
import NotFoundPage from '@/pages/[...all].vue'
import ErrorPage from '@/pages/erro/index.vue'

const routes = [
    {
        path: '/',
        component: HomePage,
        name: 'Home',
        meta: {layout: 'blank'}
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
        path: '/usuarios',
        component: UsuariosPage,
        name: 'Usuarios',
        meta: {roles: ['ADMIN']},
    },
    {
        path: '/relatorios',
        component: RelatoriosPage,
        name: 'Relatorios',
        meta: {roles: ['ADMIN']},
    },
    {
        path: '/relatorios/visualizar',
        component: RelatoriosViewPage,
        name: 'RelatoriosVisualizar',
        meta: {layout: 'blank', roles: ['ADMIN']}
    },
    {
        path: '/perfil',
        component: PerfilPage,
        name: 'Perfil',
    },
    {
        path: '/configuracoes',
        component: ConfiguracoesPage,
        name: 'Configuracoes',
    },
    {
        path: '/erro/401',
        component: ErrorPage,
        name: 'Unauthorized',
        meta: {layout: 'blank', public: true, errorCode: '401'}
    },
    {
        path: '/erro/403',
        component: ErrorPage,
        name: 'Forbidden',
        meta: {layout: 'blank', public: true, errorCode: '403'}
    },
    {
        path: '/erro/500',
        component: ErrorPage,
        name: 'ServerError',
        meta: {layout: 'blank', public: true, errorCode: '500'}
    },
    {
        path: '/erro/offline',
        component: ErrorPage,
        name: 'Offline',
        meta: {layout: 'blank', public: true, errorCode: 'offline'}
    },
    {
        path: '/:pathMatch(.*)*',
        component: NotFoundPage,
        name: 'NotFound',
        meta: {layout: 'blank', public: true, errorCode: '404'}
    },
]

const router = createRouter({
    history: createWebHistory(String(import.meta.env.BASE_URL)),
    routes,
})

router.beforeEach(async (to) => {
    const authStore = useAuthStore()
    const isPublicRoute = to.path === '/' || to.meta.public === true

    if (to.path.startsWith('/auth')) {
        return '/'
    }

    if (!authStore.ready) {
        try {
            await authStore.init()
        } catch (error) {
            console.error('Não foi possível verificar a autenticação.', error)
        }
    }

    if (!authStore.isAuthenticated && !isPublicRoute) {
        return {
            name: 'Unauthorized',
            query: {redirect: to.fullPath},
        }
    }

    const allowedRoles = to.meta.roles as string[] | undefined
    if (allowedRoles?.length && !allowedRoles.includes(authStore.userRole)) {
        return {
            name: 'Forbidden',
            query: {redirect: to.fullPath},
        }
    }
})

router.onError((error) => {
    console.error('Erro de navegação.', error)
    if (router.currentRoute.value.name !== 'ServerError') {
        void router.replace({name: 'ServerError'})
    }
})

export default router
