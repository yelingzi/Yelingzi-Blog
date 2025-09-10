<!-- bgImage.vue -->
<template>
  <el-image class="img puffIn" :src="src" :lazy="lazy" fit="cover">
    <template #error>
      <div class="error-fallback">
        <img :src="fallback" alt="404" />
      </div>
    </template>
  </el-image>
</template>

<script setup lang="ts">
import { withDefaults } from 'vue'
import defaultFallback from '@/assets/images/404.gif'

interface Props {
  src: string
  fallback?: string
  lazy?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  fallback: defaultFallback,
  lazy: true
})
</script>

<style lang="scss" scoped>
.img {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw; // 改用视窗单位
  height: 80vh;
  z-index: -9;
  animation-duration: 1s;
  animation-fill-mode: both;
}

.error-fallback {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%; // 继承父容器高度
  display: flex; // 使用 flex 布局
  background: #f5f5f5;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block; // 消除图片默认间隙
    filter: grayscale(80%) blur(1px);
    opacity: 0.9;
  }
}

@keyframes puffIn {
  0% {
    opacity: 0;
    transform-origin: 50% 50%;
    transform: scale(2, 2);
    filter: blur(2px);
  }

  100% {
    opacity: 1;
    transform-origin: 50% 50%;
    transform: scale(1, 1);
    filter: blur(0px);
  }
}

.puffIn {
  animation-name: puffIn;
}
</style>
