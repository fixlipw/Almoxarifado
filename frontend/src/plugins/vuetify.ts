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
        colors: {
          'primary': '#0066cc',
          'info': '#0ea5e9',
          'info-light': '#e0f2fe',
          'warning': '#f59e0b',
          'warning-light': '#fef3c7',
          'success': '#10b981',
          'success-light': '#d1fae5',
          'background': '#f9f9fb',
          'surface': '#ffffff',
        },
      },
    },
  },
})
