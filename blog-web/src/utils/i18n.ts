// src/utils/i18n.ts
import { useI18nStore } from '@/stores'

export const handleI18n = async (lang: string) => {
  const i18nStore = useI18nStore()

  // 切换语言
  const success = await i18nStore.switchLang(lang)

  if (success) {
    // 不需要刷新整个页面
    // 只需重新渲染需要更新的组件
    return true
  }

  return false
}

// 翻译函数
export const t = (key: string): string => {
  const i18nStore = useI18nStore()
  const config = i18nStore.currentConfig as Record<string, string>
  return config[key] || key
}
