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
// Components and directives
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import {VTabs} from "vuetify/components";

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  components: {
    ...components,
    VTabs,
  },
  directives,
  defaults: {
    global: {
      ripple: true,
      density: 'comfortable',
    }
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#2563eb',
          secondary: '#64748b',
          accent: '#38bdf8',
          info: '#0ea5e9',
          'info-light': '#e0f2fe',
          warning: '#fbbf24',
          'warning-light': '#fef3c7',
          success: '#10b981',
          'success-light': '#d1fae5',
          error: '#ef4444',
          background: '#f8fafc',
          surface: '#ffffff',
          'on-primary': '#ffffff',
          'on-secondary': '#1e293b',
          'on-background': '#1e293b',
          'on-surface': '#1e293b',
        },
      },
      dark: {
        dark: true,
        colors: {
          primary: '#f97316',
          accent: '#fb923c',
          secondary: '#262626',
          info: '#60a5fa',
          'info-light': '#1e3a8a',
          warning: '#f97316',
          'warning-light': '#7c2d12',
          success: '#10b981',
          'success-light': '#064e3b',
          error: '#ef4444',
          background: '#0b0b0b',
          surface: '#111111',
          'on-primary': '#0b0b0b',
          'on-secondary': '#e6eef8',
          'on-background': '#e6eef8',
          'on-surface': '#e6eef8',
        },
      },
    },
  },
})
