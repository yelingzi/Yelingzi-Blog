<template>
  <el-row :gutter="gutter">
    <el-col class="col" :span="props.imageLayout" v-for="(img, index) in props.imageList" :key="index"
      :ref="(el: HTMLElement | null) => setItemRef(el, index)">
      <el-image class="card" :class="{ slideInUp: isVisible[index] }" :src="img" :preview-src-list="imageList"
        :initial-index="index" lazy fit="cover" :preview-teleported="true" @load="handleImageLoad(index)">
        <template #placeholder>
          <div class="image-slot">Loading<span class="dot">...</span></div>
        </template>
        <template #error>
          <div class="error-fallback">
            <img :src="defaultFallback" alt="404" />
          </div>
        </template>
      </el-image>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>
import { ref, withDefaults, onMounted } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'
import defaultFallback from '@/assets/images/404.gif'

interface Props {
  imageLayout?: number
  imageList?: string[]
  gutter?: number
}

const props = withDefaults(defineProps<Props>(), {
  imageLayout: 8,
  imageList: () => [],
  gutter: 20
})

const cardRefs = ref<(HTMLElement | null)[]>([])
const isVisible = ref<boolean[]>([])
const loadedImages = ref<boolean[]>([])
const stopHandlers = ref<(() => void)[]>([])

// 设置元素引用
const setItemRef = (el: HTMLElement | null, index: number) => {
  cardRefs.value[index] = el
}

// 图片加载完成回调
const handleImageLoad = (index: number) => {
  loadedImages.value[index] = true
  setupObserver(index)
}

// 设置观察器
const setupObserver = (index: number) => {
  const el = cardRefs.value[index]
  if (!el || isVisible.value[index] || stopHandlers.value[index]) return

  const { stop } = useIntersectionObserver(
    el,
    ([{ isIntersecting }]) => {
      if (isIntersecting) {
        isVisible.value[index] = true
        stop()
      }
    },
    {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px'
    }
  )

  stopHandlers.value[index] = stop
}

onMounted(() => {
  // 初始化数组
  isVisible.value = new Array(props.imageList.length).fill(false)
  loadedImages.value = new Array(props.imageList.length).fill(false)
  stopHandlers.value = new Array(props.imageList.length)

  // 为已加载的图片设置观察器
  props.imageList.forEach((_, index) => {
    // 模拟图片加载完成检查
    setTimeout(() => {
      if (!loadedImages.value[index]) {
        setupObserver(index)
      }
    }, 300)
  })
})
</script>

<style lang="scss" scoped>
.card {
  position: relative;
  width: 100%;
  opacity: 0;
  transform: translateY(200px);
  transition:
    opacity 0.6s ease-out,
    transform 0.6s ease-out,
    box-shadow 0.3s ease;
  will-change: transform, opacity;
  aspect-ratio: 3 / 2;

  &:hover {
    box-shadow: 0 2px 12px rgba(237, 110, 160, 0.9);
  }

  &.slideInUp {
    opacity: 1;
    transform: translateY(0);
  }
}

.col {
  padding-bottom: 20px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.dot {
  animation: dot 2s infinite steps(3, start);
  overflow: hidden;
}

@keyframes dot {
  0% {
    width: 0;
  }

  100% {
    width: 3ch;
  }
}

.error-fallback {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  background: #f5f5f5;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    opacity: 0.9;
  }
}
</style>
