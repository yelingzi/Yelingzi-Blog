<template>
  <div ref="brandRef" class="brand-container">
    <div class="brand">
      <!-- 标题 -->
      <p class="artboard">{{ blog.nickname }}</p>
      <!-- 打字机 -->
      <Typeit v-if="yiYan.hitokotoList.length > 0" :data="yiYan.hitokotoList" :speed="100" :delete-speed="50"
        :pause-time="3000" />
      <div v-else class="loading">正在加载每日一言...</div>
    </div>
    <!-- 波浪 -->
    <WaveOne></WaveOne>
    <!-- 向下按钮 -->
    <SvgIcon name="icon-xiala" class="arrow-down pointer" size="120" @click="scrollDown" />

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, inject, type Ref } from 'vue'
import { useI18nStore } from "@/stores"
import Typeit from "@/components/Typeit/Typeit.vue"
import WaveOne from "@/components/Waves/WaveOne.vue"
import { useYiYanStore } from '@/stores/modules/yiyan'

const yiYan = useYiYanStore()
const blog = useI18nStore().currentConfig
const brandRef = ref<HTMLElement>()
const layoutRef = inject<Ref<HTMLElement | null>>('scrollContainer', ref<HTMLElement | null>(null));

// 生命周期
// 初始加载和定期刷新
onMounted(async () => {
  await yiYan.fetchHitokoto()

  // 每15秒尝试获取一次
  const interval = setInterval(async () => {
    await yiYan.fetchHitokoto()

    // 当天达到10条或已失败时清除定时器
    if (yiYan.dailyCount >= 10 || yiYan.failedToday) {
      clearInterval(interval)
    }
  }, 15000)
})

const scrollDown = () => {
  if (!brandRef.value) return

  // 获取组件底部位置
  const element = brandRef.value
  const elementRect = element.getBoundingClientRect()
  const absoluteBottom = elementRect.bottom + window.scrollY

  // 判断是否存在自定义滚动容器
  if (layoutRef?.value) {
    // 自定义容器的滚动逻辑
    const container = layoutRef.value
    const containerScrollTop = container.scrollTop
    const elementBottomRelative = elementRect.bottom + containerScrollTop - container.offsetTop

    container.scrollTo({
      top: elementBottomRelative,
      behavior: 'smooth'
    })
  } else {
    // 默认窗口滚动逻辑
    window.scrollTo({
      behavior: "smooth",
      top: absoluteBottom
    })
  }
}
</script>





<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.brand-container {
  @include m.flex;
  flex-direction: column;
  position: relative;
  width: 100%;
  height: 70vh;
  min-height: 10rem;
  color: var(--header-text-color);
}

.brand {
  @include m.flex;
  flex-direction: column;
  position: fixed;
  z-index: -1;

  .artboard {
    font-family: "Fredericka the Great", Mulish, -apple-system, "PingFang SC", "Microsoft YaHei",
      sans-serif;
    font-size: 4em;
    line-height: 1.2;
    animation: titleScale 1s;
  }

  .title {
    letter-spacing: 0.1em;
  }
}

.easy-typed-cursor {
  margin-left: 0.625rem;
  opacity: 1;
  -webkit-animation: blink 0.7s infinite;
  -moz-animation: blink 0.7s infinite;
  animation: blink 0.7s infinite;
}

.arrow-down {
  position: absolute;
  bottom: 70px;
  -webkit-animation: arrow-shake 1.5s ease-out infinite;
  animation: arrow-shake 1.5s ease-out infinite;
  z-index: 8;
  color: var(--lightRed);
}

@media (max-width: 767px) {
  .brand-container {
    padding: 3rem 0.5rem 0;
  }
}

@media (min-width: 760px) {
  .title {
    font-size: 1.5rem;
  }
}

@keyframes arrow-shake {
  0% {
    opacity: 1;
    transform: translateY(0);
  }

  30% {
    opacity: 0.5;
    transform: translateY(25px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes blink {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}
</style>
