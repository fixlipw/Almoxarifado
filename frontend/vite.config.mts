import {fileURLToPath, URL} from 'node:url'
import Vue from '@vitejs/plugin-vue'
import {defineConfig} from 'vite'
import Vuetify, {transformAssetUrls} from 'vite-plugin-vuetify'

// https://vitejs.dev/config/
const repoRoot = fileURLToPath(new URL('..', import.meta.url))

export default defineConfig({
  envDir: repoRoot,
  plugins: [
    Vue({
      template: { transformAssetUrls },
    }),
    // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
    Vuetify({
      autoImport: true,
      styles: {
        configFile: 'src/styles/settings.scss',
      },
    }),
  ],
  define: { 'process.env': {} },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('src', import.meta.url)),
    },
    extensions: [
      '.js',
      '.json',
      '.jsx',
      '.mjs',
      '.ts',
      '.tsx',
      '.vue',
    ],
  },
  server: {
    port: 3000,
    proxy: {
      '/sigaa-api': {
        target: 'http://almoxarifadoec.quixada.ufc.br:3000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/sigaa-api/, '')
      }
    }
  },
})
