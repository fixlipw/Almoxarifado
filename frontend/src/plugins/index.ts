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
import { registerSupabase } from './supabase'

export function registerPlugins (app: App) {
  app.use(vuetify)
  app.use(router)
  app.use(pinia)
  registerSupabase(app)
}
