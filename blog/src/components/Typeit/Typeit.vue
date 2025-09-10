<script setup lang="ts">
import { ref, watch, onUnmounted } from 'vue'

const props = defineProps({
  data: {
    type: Array as () => string[],
    default: () => ['请输入内容'],
    validator: (value: string[]) => value.length > 0
  },
  speed: {
    type: Number,
    default: 150
  },
  deleteSpeed: {
    type: Number,
    default: 50
  },
  pauseTime: {
    type: Number,
    default: 2000
  }
})

// 状态管理
const flag = ref(true)
const currentIndex = ref(0)
const currentText = ref('')
let timeoutId: number | null = null

// 获取当前文本（循环访问数组）
const getCurrentText = () => {
  if (props.data.length >= 10) {
    return props.data[currentIndex.value]
  }
  return props.data[props.data.length - 1]
}

// 清空状态
const resetState = () => {
  currentText.value = ''
}

// 打字效果
const typing = () => {
  flag.value = false
  resetState()
  const targetText = getCurrentText()
  let charIndex = 0

  const type = () => {
    if (charIndex < targetText.length) {
      currentText.value += targetText[charIndex]
      charIndex++
      timeoutId = setTimeout(type, props.speed) as unknown as number
    } else {
      timeoutId = setTimeout(startDelete, props.pauseTime) as unknown as number
    }
  }

  type()
}

// 开始删除
const startDelete = () => {
  const deleteChar = () => {
    if (currentText.value.length > 0) {
      currentText.value = currentText.value.slice(0, -1)
      timeoutId = setTimeout(deleteChar, props.deleteSpeed) as unknown as number
    } else {
      moveToNext()
    }
  }
  deleteChar()
}

// 切换到下一句
const moveToNext = () => {
  currentIndex.value = (currentIndex.value + 1) % props.data.length
  flag.value = true
}

// 生命周期管理
onUnmounted(() => {
  if (timeoutId) clearTimeout(timeoutId)
})

// 监听 flag 变化重新开始
watch(flag, (newVal) => {
  if (newVal) {
    timeoutId = setTimeout(typing, 500) as unknown as number
  }
})

// 初始启动
setTimeout(typing, 500)
</script>

<template>
  <div class="type-container">
    <span class="type-content">{{ currentText }}</span>
    <span class="type-cursor">|</span>
  </div>
</template>

<style scoped>
.type-container {
  min-height: 24px;
  font-size: 2.5rem;
  color: #f15b5b;
}

.type-content {
  padding-right: 4px;
}

.type-cursor {
  animation: cursor-blink 0.8s infinite;
}

@keyframes cursor-blink {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }
}
</style>
