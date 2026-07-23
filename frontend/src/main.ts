import {createApp} from 'vue'
import {createPinia} from 'pinia'
import router from '@/router'
import {useAuthStore} from '@/stores/auth'
import {ApiError} from '@/services/api'
import App from './App.vue'
import '@/styles/main.css'
import '@/styles/themes.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)

app.config.errorHandler = (error, _instance, info) => {
    console.error(`Erro não tratado no aplicativo (${info}).`, error)
    if (router.currentRoute.value.name !== 'ServerError') {
        void router.replace({name: 'ServerError'})
    }
}

globalThis.addEventListener('app:api-error', ((event: CustomEvent<ApiError | TypeError>) => {
    const error = event.detail
    const name = error instanceof TypeError
        ? 'Offline'
        : error.status === 401
            ? 'Unauthorized'
            : error.status === 403
                ? 'Forbidden'
                : error.status >= 500
                    ? 'ServerError'
                    : null

    if (name && router.currentRoute.value.name !== name) {
        void router.replace({
            name,
            query: error instanceof ApiError && error.message ? {message: error.message} : undefined,
        })
    }
}) as EventListener)

const AUTH_INIT_TIMEOUT_MS = 8_000

async function bootstrap() {
    const authStore = useAuthStore()
    try {
        await Promise.race([
            authStore.init(),
            new Promise<never>((_, reject) => {
                setTimeout(() => reject(new Error('Tempo limite da autenticação excedido.')), AUTH_INIT_TIMEOUT_MS)
            }),
        ])
    } catch (error) {
        console.error('Não foi possível inicializar a autenticação institucional.', error)
    }
    await router.isReady()
    app.mount('#app')
}

await bootstrap()
