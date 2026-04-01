import { createClient } from '@supabase/supabase-js'

function normalizeEnvValue (value?: string): string {
  if (!value) {
    return ''
  }

  // Handles values copied with surrounding quotes in CI/CD variables.
  const trimmed = value.trim()
  return trimmed.replace(/^['"]|['"]$/g, '')
}

function isValidHttpUrl (value: string): boolean {
  try {
    const parsedUrl = new URL(value)
    return parsedUrl.protocol === 'http:' || parsedUrl.protocol === 'https:'
  } catch {
    return false
  }
}

const supabaseUrl = normalizeEnvValue(import.meta.env.VITE_SUPABASE_URL)
const supabaseKey = normalizeEnvValue(
  import.meta.env.VITE_SUPABASE_PUBLISHABLE_DEFAULT_KEY
    ?? import.meta.env.VITE_SUPABASE_ANON_KEY,
)

if (!supabaseUrl || !supabaseKey) {
  throw new Error(
    'Defina VITE_SUPABASE_URL e VITE_SUPABASE_PUBLISHABLE_DEFAULT_KEY (ou VITE_SUPABASE_ANON_KEY) no arquivo .env',
  )
}

if (!isValidHttpUrl(supabaseUrl)) {
  throw new Error('VITE_SUPABASE_URL invalida. Use uma URL HTTP/HTTPS, por exemplo: https://<project-ref>.supabase.co')
}

export const supabase = createClient(supabaseUrl, supabaseKey)
