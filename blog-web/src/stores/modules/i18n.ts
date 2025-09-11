import { defineStore } from 'pinia'
import { computed, reactive, ref } from 'vue'
import type { LocalConfig } from '@/types/localConfig'
import { LANG_VERSION_KEY, LANG_VERSION } from '@/constants/i18n'

export const useI18nStore = defineStore(
  'i18n',
  () => {
    // 状态定义
    const currentLang = ref('zh-CN') // 修正语言代码为标准写法 zh-CN
    const configs = ref<Record<string, LocalConfig>>({}) // 存储多语言配置的键值对
    const isLangLoaded = ref(false)

    // 操作方法
    const loadLang = async (lang: string) => {

      const finalLang = lang || 'zh-CN'

      // 版本比对：缓存版本 ≠ 当前版本 → 强制重载
      const cachedVersion = localStorage.getItem(LANG_VERSION_KEY)
      const needReload = !configs.value[finalLang] || cachedVersion !== LANG_VERSION

      if (needReload) {
        try {
          const module = await import(`@/assets/locales/${finalLang}.json`)
          configs.value[finalLang] = module.default
          localStorage.setItem(LANG_VERSION_KEY, LANG_VERSION) // 更新版本
        } catch (e) {
          console.error(`[i18n] failed to load lang: ${finalLang}`, e)
          throw new Error(`Language ${finalLang} not available`)
        }
      }

      currentLang.value = finalLang
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
    persist: {
      key: 'store-key',
      storage: localStorage,
      pick: ['currentLang'],
    }
  }
)
