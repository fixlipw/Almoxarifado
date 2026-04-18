/**
 * plugins/index.ts
 *
 * Automatically included in `./src/main.ts`
 */

// Types
import type { App } from 'vue'

// Plugins
import vuetify from './vuetify'
import router from "@/router";
import pinia from "@/stores";

export function registerPlugins (app: App) {
  app.use(vuetify)
  app.use(router)
  app.use(pinia)
}
