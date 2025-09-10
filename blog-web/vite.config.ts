import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import svgLoader from 'vite-svg-loader'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import compression from 'vite-plugin-compression'
import vueJsx from '@vitejs/plugin-vue-jsx';

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools(), svgLoader(), vueJsx(),
  AutoImport({
    resolvers: [ElementPlusResolver()],
  }),
  Components({
    resolvers: [ElementPlusResolver()],
  }),
  compression({
    algorithm: 'gzip',
    threshold: 51200,
    deleteOriginFile: false,
  }),
  ],
  resolve: {
    alias: {
      '~': '/src', // 假设你的项目结构需要使用 `~` 作为别名
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
    commonjsOptions: {
      transformMixedEsModules: true  // 关键修复项
    },
    assetsInlineLimit: 0 // 防止小文件被内联
  },
  optimizeDeps: {
    include: ['lodash-es']
  }
})
