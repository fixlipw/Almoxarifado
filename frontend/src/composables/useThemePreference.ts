import {computed, ref} from 'vue'

export type ThemeMode = 'light' | 'dark'
export type ThemePreset = 'modern-minimal' | 'claude' | 'catppuccin' | 'kodama-grove' | 'quantum-rose' | 'neo-brutalism'

export interface ThemeOption {
    id: ThemePreset
    name: string
    description: string
    colors: string[]
}

export const themeOptions: ThemeOption[] = [
    {
        id: 'modern-minimal',
        name: 'Modern Minimal',
        description: 'Limpo, equilibrado e discreto.',
        colors: ['#26739a', '#e7ecf2', '#161a1d']
    },
    {
        id: 'claude',
        name: 'Claude',
        description: 'Quente, editorial e acolhedor.',
        colors: ['#d97757', '#f3eadf', '#3d3929']
    },
    {
        id: 'catppuccin',
        name: 'Catppuccin',
        description: 'Pastéis suaves com personalidade.',
        colors: ['#8839ef', '#dc8a78', '#1e66f5']
    },
    {
        id: 'kodama-grove',
        name: 'Kodama Grove',
        description: 'Natural, sereno e orgânico.',
        colors: ['#5f6f52', '#a9b388', '#fefae0']
    },
    {
        id: 'quantum-rose',
        name: 'Quantum Rose',
        description: 'Vibrante, moderno e expressivo.',
        colors: ['#e11d74', '#8b5cf6', '#fce7f3']
    },
    {
        id: 'neo-brutalism',
        name: 'Neo Brutalism',
        description: 'Contraste alto e formas marcantes.',
        colors: ['#ffdd00', '#ff6b6b', '#111111']
    },
]

const storedPreset = localStorage.getItem('theme-preset') as ThemePreset | null
const storedMode = localStorage.getItem('theme-mode') as ThemeMode | null
const preset = ref<ThemePreset>(themeOptions.some(option => option.id === storedPreset) ? storedPreset! : 'modern-minimal')
const mode = ref<ThemeMode>(storedMode === 'dark' ? 'dark' : 'light')

function applyTheme() {
    document.documentElement.dataset.theme = preset.value
    document.documentElement.classList.toggle('dark', mode.value === 'dark')
}

function setPreset(value: ThemePreset) {
    preset.value = value
    localStorage.setItem('theme-preset', value)
    applyTheme()
}

function setMode(value: ThemeMode) {
    mode.value = value
    localStorage.setItem('theme-mode', value)
    localStorage.setItem('theme', value)
    applyTheme()
}

function toggleMode() {
    setMode(mode.value === 'dark' ? 'light' : 'dark')
}

applyTheme()

export function useThemePreference() {
    return {
        preset,
        mode,
        isDark: computed(() => mode.value === 'dark'),
        setPreset,
        setMode,
        toggleMode,
    }
}
