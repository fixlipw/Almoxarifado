/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Composables
import { createApp } from 'vue'

// Plugins
import { registerPlugins } from '@/plugins'
import router from '@/router'

// Components
import App from './App.vue'

// Styles
import '@fontsource/poppins/300.css'
import '@fontsource/poppins/400.css'
import '@fontsource/poppins/500.css'
import '@fontsource/poppins/600.css'
import '@fontsource/poppins/700.css'

const app = createApp(App)

registerPlugins(app)

async function bootstrap () {
  await router.isReady()
  app.mount('#app')
}

bootstrap()
