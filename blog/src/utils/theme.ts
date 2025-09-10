/*
 * @Description: Manage theme settings
 * @Author: snows_l snows_l@163.com
 * @Date: 2024-08-08 09:34:27
 * @LastEditors: snows_l snows_l@163.com
 * @LastEditTime: 2025-01-20 12:55:47
 * @FilePath: \BLOG\src\utils\theme.ts
 */

import { useBlogStore } from "@/stores"

// 获取当前主题
export const getDark = (): string => {
  return Array.from(document.documentElement.classList).includes('dark') ? 'dark' : 'light'
}

// 设置主题
export const setDark = (isDark: boolean): void => {
  if (isDark) {
    document.documentElement.classList.add('dark')
    console.log("dark")
    useBlogStore().blogInfo.isDark = true
  } else {
    document.documentElement.classList.remove('dark')
    useBlogStore().blogInfo.isDark = false
  }
}

// 处理主题样式
export function setPrimaryColor(theme: string): void {
  document.documentElement.style.setProperty('--theme-color', theme)

  for (let i = 1; i <= 9; i++) {
    document.documentElement.style.setProperty(
      `--theme-light-color-${i}`,
      `${getLightColor(theme, i / 10)}`,
    )
  }

  for (let i = 1; i <= 9; i++) {
    document.documentElement.style.setProperty(
      `--el-color-primary-dark-${i}`,
      `${getDarkColor(theme, i / 10)}`,
    )
  }
}

// 处理字体
export function setFontFamily(font: string): void {
  document.documentElement.style.setProperty('--font-family', font)
}

// hex颜色转rgb颜色
export function hexToRgb(str: string): number[] {
  str = str.replace('#', '')
  const hexs = str.match(/../g) || []
  return hexs.map((hex) => parseInt(hex, 16))
}

// rgb颜色转Hex颜色
export function rgbToHex(r: number, g: number, b: number): string {
  const hexs = [r.toString(16), g.toString(16), b.toString(16)]
  return `#${hexs.map((hex) => (hex.length === 1 ? `0${hex}` : hex)).join('')}`
}

// 变浅颜色值
export function getLightColor(color: string, level: number): string {
  const rgb = hexToRgb(color)
  return rgb
    .map((val, index) => Math.floor((255 - val) * level + val))
    .reduce((acc, val) => {
      acc.push(val)
      return acc
    }, [] as number[])
    .map((val) => rgbToHex(val, val, val))[0]
}

// 变深颜色值
export function getDarkColor(color: string, level: number): string {
  const rgb = hexToRgb(color)
  return rgb
    .map((val) => Math.floor(val * (1 - level)))
    .reduce((acc, val) => {
      acc.push(val)
      return acc
    }, [] as number[])
    .map((val) => rgbToHex(val, val, val))[0]
}

// 产生混合背景色
export function getBgColor(color: string, level: number): string {
  const rgb = hexToRgb(color)
  return rgb
    .map((val) => Math.floor((val + level) % 255))
    .reduce((acc, val) => {
      acc.push(val)
      return acc
    }, [] as number[])
    .map((val) => rgbToHex(val, val, val))[0]
}

// // 产生随机背景色
export function getRandomColor(): string {
  const r = Math.floor(Math.random() * 256)
  const g = Math.floor(Math.random() * 256)
  const b = Math.floor(Math.random() * 256)
  return rgbToHex(r, g, b)
}

