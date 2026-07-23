import {computed, ref} from 'vue'

export type ThemeMode = 'light' | 'dark'
export type ThemePreset =
    | 'modern-minimal'
    | 'violet-bloom'
    | 't3-chat'
    | 'mocha-mousse'
    | 'graphite'
    | 'cosmic-night'
    | 'tangerine'
    | 'nature'
    | 'supabase'
    | 'ocean-breeze'
    | 'claude'
    | 'catppuccin'
    | 'kodama-grove'
    | 'quantum-rose'
    | 'neo-brutalism'

export interface ThemeOption {
    id: ThemePreset
    name: string
    description: string
    colors: string[]
}

export const themeOptions: ThemeOption[] = [
    {
        id: 'modern-minimal',
        name: 'Azul Essencial',
        description: 'Limpo, equilibrado e discreto.',
        colors: ['#3b82f6', '#e0f2fe', '#333333']
    },
    {
        id: 'violet-bloom',
        name: 'Violeta Suave',
        description: 'Violeta expressivo e cantos suaves.',
        colors: ['#7033ff', '#4ac885', '#e2ebff']
    },
    {
        id: 't3-chat',
        name: 'Rosa Digital',
        description: 'Rosa sofisticado com atmosfera digital.',
        colors: ['#a84370', '#d926a2', '#f1c4e6']
    },
    {
        id: 'mocha-mousse',
        name: 'Café Aconchegante',
        description: 'Terroso, elegante e confortável.',
        colors: ['#a37764', '#e4c7b8', '#56453f']
    },
    {
        id: 'graphite',
        name: 'Cinza Profissional',
        description: 'Monocromático, sóbrio e profissional.',
        colors: ['#606060', '#c0c0c0', '#333333']
    },
    {
        id: 'cosmic-night',
        name: 'Noite Violeta',
        description: 'Cósmico, profundo e luminoso.',
        colors: ['#6e56cf', '#9e8cfc', '#2a2a4a']
    },
    {
        id: 'tangerine',
        name: 'Laranja Vibrante',
        description: 'Laranja energético sobre tons frios.',
        colors: ['#e05d38', '#86a7c8', '#1e3a8a']
    },
    {
        id: 'nature',
        name: 'Verde Natural',
        description: 'Verde natural com base terrosa.',
        colors: ['#2e7d32', '#c8e6c9', '#3e2723']
    },
    {
        id: 'supabase',
        name: 'Verde Tecnológico',
        description: 'Tecnológico, limpo e esmeralda.',
        colors: ['#72e3ad', '#3b82f6', '#171717']
    },
    {
        id: 'ocean-breeze',
        name: 'Brisa do Oceano',
        description: 'Fresco, leve e inspirado no oceano.',
        colors: ['#22c55e', '#e0f2fe', '#374151']
    },
    {
        id: 'claude',
        name: 'Terracota Editorial',
        description: 'Quente, editorial e acolhedor.',
        colors: ['#d97757', '#f3eadf', '#3d3929']
    },
    {
        id: 'catppuccin',
        name: 'Pastel Colorido',
        description: 'Pastéis suaves com personalidade.',
        colors: ['#8839ef', '#dc8a78', '#1e66f5']
    },
    {
        id: 'kodama-grove',
        name: 'Bosque Sereno',
        description: 'Natural, sereno e orgânico.',
        colors: ['#5f6f52', '#a9b388', '#fefae0']
    },
    {
        id: 'quantum-rose',
        name: 'Rosa Vibrante',
        description: 'Vibrante, moderno e expressivo.',
        colors: ['#e11d74', '#8b5cf6', '#fce7f3']
    },
    {
        id: 'neo-brutalism',
        name: 'Alto Contraste',
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
