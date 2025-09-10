import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'
import axios from 'axios'
import type { Visit } from '@/types/home'

export const useTianqiStore = defineStore(
  'tianqiStore',
  () => {
    /* ------------ 状态 ------------ */
    const visit = reactive<Visit>({
      success: false,
      time: '',
      week: '未知',
      ip: '127.0.0.1',
      location: '未知',
      browser: '未知',
      system: '未知',
      low: '未知',
      high: '未知',
      tq: '未知',
      fl: '未知',
      fengxiang: '未知',
      tip: '未知'
    })
    // 记录“今天”是否已经**成功**拿到数据
    const lastSuccessDate = ref('')

    /* ------------ 工具方法 ------------ */
    const today = () => new Date().toISOString().slice(0, 10)

    /* ------------ 获取天气 ------------ */
    const fetchWeather = async () => {

      if (lastSuccessDate.value === today()) return

      try {
        const { data } = await axios.get<Visit>('https://api.vvhan.com/api/visitor.info')
        if (data.success) {
          Object.assign(visit, data)

          lastSuccessDate.value = today()
        }
      } catch {

      }
    }


    const checkAndUpdateWeather = () => {
      fetchWeather()
    }

    return {
      visit,
      lastSuccessDate,
      checkAndUpdateWeather
    }
  },
  { persist: true }
)
