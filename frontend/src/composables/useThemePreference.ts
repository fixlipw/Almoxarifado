import { computed } from 'vue'
import { useTheme } from 'vuetify'

const STORAGE_KEY = 'almoxarifado-theme'

type AlmoxarifadoTheme = 'light' | 'dark'

export function useThemePreference() {
  const theme = useTheme()

  const currentTheme = computed<AlmoxarifadoTheme>(() => (
    theme.global.name.value === 'dark' ? 'dark' : 'light'
  ))

  const isDark = computed(() => theme.global.current.value.dark)

  function applyTheme(nextTheme: AlmoxarifadoTheme) {
    theme.change(nextTheme)
    localStorage.setItem(STORAGE_KEY, nextTheme)
  }

  function initializeTheme() {
    const savedTheme = localStorage.getItem(STORAGE_KEY)

    if (savedTheme === 'light' || savedTheme === 'dark') {
      applyTheme(savedTheme)
      return
    }

    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    applyTheme(prefersDark ? 'dark' : 'light')
  }

  function toggleTheme() {
    applyTheme(isDark.value ? 'light' : 'dark')
  }

  return {
    currentTheme,
    isDark,
    initializeTheme,
    toggleTheme,
  }
}
