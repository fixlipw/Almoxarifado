/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Composables
import { createVuetify } from 'vuetify'
// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          'primary': '#1976D2', // Azul UFC ou similar
          'secondary': '#424242',
          'info': '#0ea5e9',
          'info-light': '#e0f2fe',
          'warning': '#FFC107', // Amarelo presente na hero
          'warning-light': '#fef3c7',
          'success': '#10b981',
          'success-light': '#d1fae5',
          'background': '#f9f9fb',
          'surface': '#ffffff',
        },
      },
      dark: {
        dark: true,
        colors: {
          'primary': '#1976D2',
          'secondary': '#424242',
          'info': '#0ea5e9',
          'info-light': '#0c4a6e',
          'warning': '#FFC107',
          'warning-light': '#451a03',
          'success': '#10b981',
          'success-light': '#064e3b',
          'background': '#121212',
          'surface': '#1e1e1e',
        },
      }
    },
  },
})
