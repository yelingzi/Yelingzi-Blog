<template>
  <!-- 移动端 进度条 -->
  <div class="m-progress-warp" v-show="isMobi">
    <div class="progress" :style="{ width: `${currentScrollProgress}%` }"></div>
  </div>

  <!-- PC 进度条 -->
  <div class="progress-warp" ref="progressRef" v-show="showPcProgress">
    <div class="progress" :style="{ height: `calc(${currentScrollProgress}%)` }"></div>
    <div ref="progressIconRef" class="progress-icon pointer" :style="{
      opacity: currentScrollProgress !== 0 ? 1 : 0,
      bottom: `calc(${currentScrollProgress}% - ${Math.min(currentScrollProgress * 0.4, 20)}px)`
    }" @click="handleTop">
      <span class="progress-text">{{ Math.round(currentScrollProgress) }}%</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted, reactive, inject, type Ref } from 'vue'
import { useRoute } from 'vue-router'
import { useResize } from '@/utils/common'

const route = useRoute()
const isMobi = useResize();
const props = defineProps({
  disabledSmooth: {
    type: Boolean,
    required: true,
  },
});

// DOM Refs
const progressRef = ref<HTMLElement | null>(null)
const progressIconRef = ref<HTMLElement | null>(null)
// 在子组件中获取引用
const layoutRef = inject<Ref<HTMLElement | null>>('scrollContainer', ref<HTMLElement | null>(null));

// 响应式状态
const currentScrollProgress = ref(0)
const state = reactive({
  isDragging: false,
  dragTimer: -1,
})

// 计算属性
const showPcProgress = computed(() => {
  return !isMobi && !['/start', '/preview'].includes(route.path)
})

// 处理置顶
const handleTop = () => {
  if (state.isDragging) return
  layoutRef.value?.scrollTo({
    top: 0,
    behavior: props.disabledSmooth ? 'auto' : 'smooth'
  })
}

// 滚动处理
const handleScroll = () => {
  if (!layoutRef.value) return

  const { scrollTop, scrollHeight, clientHeight } = layoutRef.value
  const progress = (scrollTop / (scrollHeight - clientHeight)) * 100

  currentScrollProgress.value = Math.min(100, Math.max(0, progress))
}

// 拖动处理
const handleProgressDrag = (emit: any) => {
  const progressEl = progressRef.value
  const iconEl = progressIconRef.value
  if (!progressEl || !iconEl) return

  const handleMouseDown = (e: MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()

    const handleMouseMove = (moveE: MouseEvent) => {
      if (!state.isDragging) {
        state.isDragging = true
        emit('update:disabledSmooth', !props.disabledSmooth);
      }

      const rect = progressEl.getBoundingClientRect()
      const yPos = moveE.clientY - rect.top
      const progress = Math.min(100, Math.max(0, (yPos / rect.height) * 100))

      currentScrollProgress.value = progress
      layoutRef.value?.scrollTo(0, (progress / 100) * (layoutRef.value.scrollHeight - layoutRef.value.clientHeight))
    }

    const handleMouseUp = () => {
      emit('update:disabledSmooth', !props.disabledSmooth);
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseup', handleMouseUp)
      state.dragTimer = window.setTimeout(() => {
        state.isDragging = false
      }, 10)
    }

    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mouseup', handleMouseUp)
  }

  iconEl.addEventListener('mousedown', handleMouseDown)
}

onMounted(() => {
  if (!layoutRef?.value) return

  layoutRef.value.addEventListener('scroll', handleScroll)
  handleProgressDrag((emit: any) => emit)
})

onUnmounted(() => {
  layoutRef.value?.removeEventListener('scroll', handleScroll)
  if (state.dragTimer) clearTimeout(state.dragTimer)
})

</script>

<style lang="scss" scoped>
.progress-warp {
  position: fixed;
  top: 0px;
  right: 6px;
  width: 50px;
  height: 80vh;
  z-index: 998;
  pointer-events: none;

  .progress {
    background-image: url('data:image/jpeg;base64,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');
    width: 100%;
    background-size: 100% 115%;
  }

  .progress-icon {
    width: 50px;
    height: 50px;
    pointer-events: all;
    background-image: url('data:image/jpeg;base64,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');
    background-size: 100% 870%;
    background-position: 100% 100%;
    transition: opacity 1s ease;
    display: flex;
    justify-content: center;

    // 添加底部边界限制
    &::after {
      content: '';
      position: absolute;
      bottom: -30px; // 底部保留30px空间
      height: 30px;
      width: 1px;
      background: transparent;
    }

    .progress-text {
      background-color: #ffffffd6;
      transform: scale(0.9);
      margin-top: 4px;
      display: inline-block;
      text-align: center;
      line-height: 14px;
      color: #7d4842;
      text-shadow: var(--text-shadow);
      user-select: none;
      font-size: 12px;
      height: 14px;
      border-radius: var(--border-radius-1);
    }
  }
}

@keyframes animation {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}
</style>
