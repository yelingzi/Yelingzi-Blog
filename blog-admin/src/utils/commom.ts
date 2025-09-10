import { dayjs } from "element-plus"

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

export const formatDate = (date: string | Date, format = 'YYYY-MM-DD') => {
    return dayjs(date).format(format)
}
export const removeTFrontDateString = (date: string | Date): string => {
    // 处理字符串类型的时间
    if (typeof date === 'string') {
        // 处理高精度时间（保留3位毫秒）
        const normalized = date
            .replace('T', ' ')          // 替换T为空格
            .replace(/\.\d{3}\d+/, (match) => match.substring(0, 4)) // 截断到3位小数
            .replace(/-\d{2}:\d{2}$/, ''); // 移除时区部分（如果有）
        return dayjs(normalized).format('YYYY-MM-DD HH:mm:ss');
    }
    // 处理Date类型直接转换
    return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
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
export default {
    booleanToNumber, formatDate, removeTFrontDateString, numberToBoolean, formatChatDisplayTime, formatChatListDisplayTime
}