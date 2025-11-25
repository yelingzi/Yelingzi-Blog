// main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPersistedState } from 'pinia-plugin-persistedstate'
import '@/assets/styles/main.scss'

const pinia = createPinia()
pinia.use(createPersistedState({
  storage: localStorage, 
  auto: true
}))
const app = createApp(App)

app.use(ElementPlus, {
  locale: zhCn,
})
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(pinia)
import { useUserStore } from './stores'
useUserStore().addRoute()

app.use(router)

app.mount('#app')
