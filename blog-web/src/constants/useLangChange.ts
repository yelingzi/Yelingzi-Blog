// src/composables/useLangChange.ts
import { onMounted, onUnmounted } from 'vue'
import { useI18nStore } from '@/stores'

export const useLangChange = (callback: () => void) => {
  const i18nStore = useI18nStore()

  // 监听语言变化事件
  const handleLangChange = () => {
    callback()
  }

  onMounted(() => {
    window.addEventListener('lang-changed', handleLangChange)
    // 初始调用一次
    callback()
  })

  onUnmounted(() => {
    window.removeEventListener('lang-changed', handleLangChange)
  })

  // 直接监听 store 变化
  i18nStore.$subscribe(() => {
    callback()
  })
}
