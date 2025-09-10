import { defineStore } from 'pinia'
import { computed, reactive, ref } from 'vue'
import type { LocalConfig } from '@/types/localConfig'

export const useI18nStore = defineStore(
  'i18n',
  () => {
    // 状态定义
    const currentLang = ref('zh-CN') // 修正语言代码为标准写法 zh-CN
    const configs = ref<Record<string, LocalConfig>>({}) // 存储多语言配置的键值对
    const isLangLoaded = ref(false)

    // 操作方法
    const loadLang = async (lang: string) => {

      const defaultLang = 'zh-CN'
      lang = lang || defaultLang

      // 避免重复加载
      if (!configs.value[lang]) {
        try {
          // 动态导入语言文件（Vite 特性）
          const module = await import(`@/assets/locales/${lang}.json`)
          configs.value[lang] = module.default
        } catch (error) {
          console.error(`Failed to load language file: ${lang}`, error)
          throw new Error(`Language ${lang} not available`)
        }
      }
      currentLang.value = lang
    }

    // Getter 风格的计算属性
    const currentConfig = computed(() => {
      return configs.value[currentLang.value] || {}
    })

    return {
      // 状态
      currentLang,
      configs,
      isLangLoaded,

      // 方法
      loadLang,

      // 计算属性
      currentConfig
    }
  },
  {
    persist: true
  }
)
