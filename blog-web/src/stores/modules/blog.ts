import { defineStore } from 'pinia'
import { reactive, ref, watch } from 'vue'
import logo from '@/assets/images/logo.jpg'
import avatar from '@/assets/images/avatar2.jpg'

/**
 * 博客
 */
interface BlogState {
  /**
   * 博客信息
   */
  nickname: string
  avatar: string
  logo: string
  isDark: boolean
}

export const useBlogStore = defineStore(
  'useBlogStore',
  () => {
    const blogInfo = ref<BlogState>({
      nickname: '叶玲子',
      avatar: avatar,
      logo: logo,
      isDark: false
    })

    const tagId = ref(0)

    const toggleTheme = (isDark?: boolean) => {
      const newTheme = isDark ?? !blogInfo.value.isDark
      blogInfo.value.isDark = newTheme

      // 统一处理主题切换
      const html = document.documentElement
      if (newTheme) {
        html.setAttribute('theme', 'dark')
        html.classList.add('dark')
      } else {
        html.removeAttribute('theme')
        html.classList.remove('dark')
      }

      // 使用相同的 localStorage key
      localStorage.setItem('theme', newTheme ? 'dark' : 'light')
      localStorage.setItem('isDark', String(newTheme))
    }


    // 监听 Live2D 的自定义事件
    if (typeof window !== 'undefined') {
      document.addEventListener('live2d-theme-change', (e) => {
        if (e instanceof CustomEvent && e.detail && typeof e.detail.isDark === 'boolean') {
          toggleTheme(e.detail.isDark)
        }
      })
    }

    watch(
      () => blogInfo.value.isDark,
      (newVal) => {
        localStorage.setItem('theme', newVal ? 'dark' : 'light')
        toggleTheme(newVal)
      },
      { immediate: true }
    )

    return {
      blogInfo,
      toggleTheme,
      tagId
    }
  },
  {
    persist: true,
  },
)
