import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import compression from 'vite-plugin-compression'
import dayjs from 'dayjs'
import svgLoader from 'vite-svg-loader'



// https://vite.dev/config/
export default defineConfig(({ mode }) => {

  const env = loadEnv(mode, process.cwd(), '')

  const appFullVersion = `${env.VUE_APP_NAME}_${env.VUE_APP_VERSION}_${dayjs().format(
    'YYYYMMDDHHmmss'
  )}`

  return {
    define: {
      __APP_FULL_VERSION__: JSON.stringify(appFullVersion),
      'import.meta.env.VITE_APP_FULL_VERSION': JSON.stringify(appFullVersion),
    },

    plugins: [
      vue(),
      svgLoader(),
      vueDevTools(),
      AutoImport({ resolvers: [ElementPlusResolver()] }),
      Components({ resolvers: [ElementPlusResolver()] }),
      compression({
        algorithm: 'gzip',
        threshold: 12800,
        deleteOriginFile: false,
      }),
    ],
    resolve: {
      alias: {
        '~': '/src',
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `
            @use "@/assets/styles/main.scss";
            @use "@/assets/styles/mixin.scss";
          `,
        },
      },
    },
    build: {
      target: 'es2022',
      commonjsOptions: { transformMixedEsModules: true },
      assetsInlineLimit: 0,
    },
    optimizeDeps: { include: ['lodash-es'] },
  }
})
