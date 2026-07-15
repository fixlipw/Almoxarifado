/**
 * src/plugins/supabase.ts
 * Supabase client initialization using Vite env variables.
 */
import type {App} from 'vue'
import {createClient} from '@supabase/supabase-js'

const env: any = import.meta.env || {}
const SUPABASE_URL = String(env.VITE_SUPABASE_URL ?? '')
const SUPABASE_KEY = String(env.VITE_SUPABASE_PUBLISHABLE_DEFAULT_KEY ?? '')

export const supabase = createClient(SUPABASE_URL, SUPABASE_KEY)

export function registerSupabase(app: App) {
    app.provide('supabase', supabase)
}

export default supabase

