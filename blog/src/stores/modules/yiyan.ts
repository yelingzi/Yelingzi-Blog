import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useYiYanStore = defineStore(
  'yiYanStore',
  () => {
    const hitokotoList = ref<string[]>([])
    const lastUpdateDate = ref<string>(new Date().toDateString())
    const failedToday = ref<boolean>(false)
    const dailyCount = ref<number>(0)

    const today = () => new Date().toISOString().slice(0, 10)

    // 获取每日一言
    const fetchHitokoto = async () => {
      const curDate = today()

      // 日期变化 ⇒ 重置
      if (curDate !== lastUpdateDate.value) {
        hitokotoList.value = []
        lastUpdateDate.value = curDate
        failedToday.value = false
        dailyCount.value = 0
      }


      // 检查是否满足条件
      if (failedToday.value || dailyCount.value >= 10) return

      try {
        const response = await fetch("https://v1.hitokoto.cn")

        const data = await response.json()
        hitokotoList.value.push(data.hitokoto)

        // 控制队列长度
        if (hitokotoList.value.length > 10) {
          hitokotoList.value.shift()
        }

        dailyCount.value += 1
      } catch (error) {
        console.error('获取每日一言失败:', error)
        hitokotoList.value.push('纸上得来终觉浅，绝知此事要躬行。')
        failedToday.value = true
      }
    }

    return {
      hitokotoList,
      fetchHitokoto,
      lastUpdateDate,
      dailyCount,
      failedToday
    }
  },
  {
    persist: true,
  }
)
