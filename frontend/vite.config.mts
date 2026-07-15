import {fileURLToPath, URL} from 'node:url'
import Vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import {defineConfig} from 'vite'

// https://vitejs.dev/config/
const repoRoot = fileURLToPath(new URL('..', import.meta.url))

export default defineConfig({
    envDir: repoRoot,
    plugins: [
        Vue(),
        tailwindcss(),
    ],
    define: {'process.env': {}},
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
