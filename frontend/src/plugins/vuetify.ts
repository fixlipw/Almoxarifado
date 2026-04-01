/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com
 */

// Composables
import { createVuetify } from 'vuetify'
// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  theme: {
    defaultTheme: 'light', // Você pode alterar para 'dark' se quiser que o dashboard siga a mesma pegada noturna do Hero
    themes: {
      light: {
        dark: false,
        colors: {
          'primary': '#004B87', // Azul institucional da UFC para o sistema
          'secondary': '#424242',
          'info': '#0ea5e9',
          'info-light': '#e0f2fe',
          'warning': '#FFC107', // Dourado/Amarelo usado no botão e ícones do Hero
          'warning-light': '#fef3c7',
          'success': '#10b981',
          'success-light': '#d1fae5',
          'background': '#f4f6f8', // Fundo levemente acinzentado para o modo claro
          'surface': '#ffffff',
        },
      },
      dark: {
        dark: true,
        colors: {
          'primary': '#42A5F5', // Azul mais claro para garantir contraste no modo noturno
          'secondary': '#1A1A1A',
          'info': '#0ea5e9',
          'info-light': '#0c4a6e',
          'warning': '#FFC107', // Dourado/Amarelo vibrante mantido no modo escuro
          'warning-light': '#451a03',
          'success': '#10b981',
          'success-light': '#064e3b',
          'background': '#0A0A0A', // Fundo bem escuro (combina com o gradiente do Hero)
          'surface': '#121212', // Superfícies (cards, modais) usando um tom de cinza super profundo
        },
      },
    },
  },
})
