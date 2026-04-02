/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com
 */

// Composables
import { createVuetify } from 'vuetify'
import { aliases, mdi } from 'vuetify/iconsets/mdi-svg'
// Styles
import 'vuetify/styles'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          'primary': '#004B87',
          'secondary': '#424242',
          'info': '#0ea5e9',
          'info-light': '#e0f2fe',
          'warning': '#FFC107',
          'warning-light': '#fef3c7',
          'success': '#10b981',
          'success-light': '#d1fae5',
          'background': '#f4f6f8',
          'surface': '#ffffff',
        },
      },
      dark: {
        dark: true,
        colors: {
          'primary': '#42A5F5',
          'secondary': '#1A1A1A',
          'info': '#0ea5e9',
          'info-light': '#0c4a6e',
          'warning': '#FFC107',
          'warning-light': '#451a03',
          'success': '#10b981',
          'success-light': '#064e3b',
          'background': '#0A0A0A',
          'surface': '#121212',
        },
      },
    },
  },
})
