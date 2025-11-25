<template>
  <div class="my-button" :style="buttonStyle" @click="$emit('click', $event)">
    <div :style="beforeStyle">{{ info }}</div>
    <div :style="afterStyle">{{ info }}</div>
    <div :style="afterStyle">{{ info }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  info?: string
  before?: string
  after?: string
  width?: string // 添加自定义宽度的 props
}

const props = withDefaults(defineProps<Props>(), {
  info: '确定',
  before: 'var(--primary-color)',
  after: 'var(--hover-color)',
  width: '66px' // 设置默认宽度
})

// 计算按钮和子元素的样式
const buttonStyle = computed(() => ({
  '--btn-width': props.width
}))

const beforeStyle = computed(() => ({
  background: props.before
}))

const afterStyle = computed(() => ({
  background: props.after
}))

defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()
</script>
<style lang="scss" scoped>
.my-button {
  user-select: none;
  position: relative;
  width: var(--btn-width, 66px);
  height: 33px;
  border-radius: 4px;
  color: var(--white);
  font-size: 14px;
  overflow: hidden;
}

.my-button div {
  width: 100%;
  height: 100%;
  line-height: 33px;
  border-radius: 4px;
  text-align: center;
  position: absolute;
}

.my-button div:nth-child(2) {
  width: 100px;
  transition: all 0.3s ease;
  transform: translateX(-120px) skewX(-30deg);
}

.my-button div:nth-child(3) {
  transition: all 0.3s ease;
  transform: translateX(-120px);
}

.my-button:hover div:nth-child(2) {
  transform: translateX(20px) skewX(-30deg);
}

.my-button:hover div:nth-child(3) {
  transform: translateX(0px);
}
</style>
