import { defineStore } from 'pinia'
import { computed, reactive, ref } from 'vue'
import type { LocalConfig } from '@/types/localConfig'
import { LANG_VERSION_KEY, LANG_VERSION } from '@/constants/i18n'

export const useI18nStore = defineStore(
  'i18n',
  () => {
    // 状态定义
    const currentLang = ref('zh-CN')
    const configs = ref<Record<string, LocalConfig>>({})
    const isLangLoaded = ref(false)
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    // 初始化应用语言
    const initAppLang = async () => {
      try {
        isLoading.value = true
        error.value = null

        // 从本地存储获取上次使用的语言
        const savedLang = localStorage.getItem('preferred_lang') || 'zh-CN'

        // 加载语言
        await loadLang(savedLang)

        isLangLoaded.value = true
      } catch (err) {
        error.value = `初始化语言失败: ${err instanceof Error ? err.message : String(err)}`
        console.error(error.value)
      } finally {
        isLoading.value = false
      }
    }

    // 加载语言
    const loadLang = async (lang: string) => {
      try {
        isLoading.value = true
        error.value = null

        const finalLang = lang || 'zh-CN'
        const cachedVersion = localStorage.getItem(LANG_VERSION_KEY)

        // 检查是否需要重新加载
        const needReload = !configs.value[finalLang] || cachedVersion !== LANG_VERSION

        if (needReload) {
          // 动态导入语言文件
          const module = await import(`@/assets/locales/${finalLang}.json`)
          configs.value[finalLang] = module.default

          // 更新版本信息
          localStorage.setItem(LANG_VERSION_KEY, LANG_VERSION)
        }

        // 更新当前语言
        currentLang.value = finalLang
        localStorage.setItem('preferred_lang', finalLang)

        return true
      } catch (err) {
        error.value = `加载语言失败: ${err instanceof Error ? err.message : String(err)}`
        console.error(error.value)
        return false
      } finally {
        isLoading.value = false
      }
    }

    // 切换语言
    const switchLang = async (lang: string) => {
      const success = await loadLang(lang)
      if (success) {
        // 通知应用语言已切换
        window.dispatchEvent(new CustomEvent('lang-changed', { detail: lang }))
      }
      return success
    }

    // 计算属性
    const currentConfig = computed(() => {
      return configs.value[currentLang.value] || {}
    })

    return {
      // 状态
      currentLang,
      configs,
      isLangLoaded,
      isLoading,
      error,

      // 方法
      initAppLang,
      loadLang,
      switchLang,

      // 计算属性
      currentConfig
    }
  },
  {
    persist: {
      key: 'i18n_store',
      storage: localStorage,
      pick: ['currentLang', 'configs'], // 保存语言和配置
    }
  }
)
