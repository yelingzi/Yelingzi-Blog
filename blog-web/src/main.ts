import { createApp } from 'vue'
import { createPinia } from 'pinia'

import '@/assets/styles/index.scss'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import Vue3DraggableResizable from 'vue3-draggable-resizable'
import 'vue3-draggable-resizable/dist/Vue3DraggableResizable.css'
import directive from './utils/directive'
import '@/assets/iconfont/iconfont.js'


import { createPersistedState } from 'pinia-plugin-persistedstate'
const loading = document.getElementById('Loading');
if (loading) loading.style.display = 'flex';

const pinia = createPinia()
pinia.use(createPersistedState({
  storage: localStorage,
  auto: true
}))

const app = createApp(App)
directive(app)
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(Vue3DraggableResizable)

app.mount('#app')

window.addEventListener('load', () => {
  if (loading) loading.style.display = 'none';
});


