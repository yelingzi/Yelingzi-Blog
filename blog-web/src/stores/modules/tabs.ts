import type { Tab } from '@/type/tabs'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTabsStore = defineStore(
  'tabStore',
  () => {
    /* ------------ 状态 ------------ */
    const tabList = ref<Tab[]>([
      {
        route: '/home',
        name: '首页',
      },
    ])

    /* 添加标签 */
    const addTab = (tab: Tab) => {
      const exists = tabList.value.find((t) => t.route === tab.route)
      if (!exists) {
        tabList.value.push(tab)
      }
    }

    /* 删除标签 */
    const removeTab = (route: string) => {
      // 保留至少一个标签
      if (tabList.value.length <= 1) return
      
      tabList.value = tabList.value.filter((tab) => tab.route !== route)
    }

    /* 清空标签 */
    const clearTabs = () => {
      tabList.value = []
    }

    return {
      tabList,
      addTab,
      removeTab,
      clearTabs,
    }
  },
  { persist: true }
)
