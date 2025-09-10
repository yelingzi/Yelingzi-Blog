import { createPinia } from 'pinia'
import persist from 'pinia-plugin-persistedstate'

const pinia = createPinia()

pinia.use(persist)

export default pinia

export * from './modules/blog'
export * from './modules/i18n'
export * from './modules/user'
export * from './modules/page'
export * from './modules/tianqi'
