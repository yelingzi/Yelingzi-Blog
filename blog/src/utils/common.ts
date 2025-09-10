import { dayjs } from 'element-plus'
import { onMounted, onUnmounted, ref } from 'vue'

// 判断当前视口是否小于等于 w 像素
const isMobile = (w = 920): boolean => {
  return window.innerWidth <= w
}

export const useResize = (): boolean => {
  const isMobi = ref(isMobile())

  // 监听屏幕大小变化
  const resizeCallback = () => {
    isMobi.value = isMobile()
  }

  onMounted(() => {
    window.addEventListener('resize', resizeCallback)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', resizeCallback)
  })

  return isMobi.value
}

export const formatDate = (date: string | Date, format = 'YYYY-MM-DD') => {
  return dayjs(date).format(format)
}
export const getYear = (date: string | Date): number => {
  return dayjs(date).year()
}
export const getMonth = (date: string | Date): number => {
  return dayjs(date).month() + 1 // 因为 dayjs 的月份是从 0 开始，所以需要加 1
}
export const getDate = (date: string | Date): number => {
  return dayjs(date).date()
}
export const removeTFrontDateString = (date: string | Date): string => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
export const getDateDiff = (date: string | Date) => {
  // 获取当前日期
  const now = dayjs()
  // 将传入的日期转换为 dayjs 对象
  const targetDate = dayjs(date)

  const diffInHours = now.diff(targetDate, 'hour')

  if (diffInHours < 0) {
    // 未来时间，直接返回完整日期
    return targetDate.format('YYYY-MM-DD HH:mm:ss')
  } else if (diffInHours === 0) {
    // 刚刚
    return '刚刚'
  } else if (diffInHours < 24) {
    // 24小时内，返回"X小时前"
    return `${diffInHours}小时前`
  }

  // 计算两个日期之间的天数差
  const diffDays = now.diff(targetDate, 'day')

  // 如果天数差小于等于 30 天，返回 "N天前"
  if (diffDays <= 30) {
    return `${diffDays}天前`
  } else {
    // 如果天数差大于 30 天，返回格式化后的日期
    return targetDate.format('YYYY-MM-DD')
  }
}

export const formatDateWithRelativeTime = (date: string | Date): string => {
  const now = dayjs()
  const targetDate = dayjs(date)
  const diffInHours = now.diff(targetDate, 'hour')

  if (diffInHours < 0) {
    // 未来时间，直接返回完整日期
    return targetDate.format('YYYY-MM-DD HH:mm:ss')
  } else if (diffInHours === 0) {
    // 刚刚
    return '刚刚'
  } else if (diffInHours < 24) {
    // 24小时内，返回"X小时前"
    return `${diffInHours}小时前`
  } else {
    // 超过24小时，返回具体日期
    return targetDate.format('YYYY-MM-DD HH:mm:ss')
  }
}

// 时间格式化函数
export const formatChatDisplayTime = (timeString: string | number | Date | dayjs.Dayjs | null | undefined) => {
  const now = dayjs();
  const targetTime = dayjs(timeString);

  // 当天：只显示时间 (HH:mm)
  if (targetTime.isSame(now, 'day')) {
    return targetTime.format('HH:mm');
  }

  // 其他情况：显示完整日期时间 (YYYY-MM-DD HH:mm)
  return targetTime.format('YYYY-MM-DD HH:mm');
};

// 时间格式化函数
export const formatChatListDisplayTime = (
  timeString: string | number | Date | dayjs.Dayjs | null | undefined
): string => {
  const now = dayjs()
  const targetTime = dayjs(timeString)

  // 当天
  if (targetTime.isSame(now, 'day')) return targetTime.format('HH:mm')

  // 当年
  if (targetTime.isSame(now, 'year')) return targetTime.format('MM-DD')

  // 其他年份
  return targetTime.format('YYYY-MM-DD')
}


export const booleanToNumber = (bool: boolean) => {
  if (bool) {
    return 1
  } else {
    return 0
  }
}
export const numberToBoolean = (num: number) => {
  if (num === 0) {
    return false
  } else {
    return true
  }
}

export default {
  useResize,
  formatDate,
  getYear,
  getMonth,
  getDate,
  removeTFrontDateString,
  getDateDiff,
  booleanToNumber,
  numberToBoolean
}
