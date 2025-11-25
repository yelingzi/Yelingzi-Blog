<!-- ImageWithFallback.vue -->
<template>
  <el-image :src="props.src" :lazy="props.lazy" fit="cover" :alt="props.alt">
    <template #placeholder>
      <div class="image-slot">Loading<span class="dot">...</span></div>
    </template>
    <template #error>
      <div class="error-fallback">
        <img class="img" :src="props.fallback" alt="404" fit="cover" />
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
  alt?: string
}

const props = withDefaults(defineProps<Props>(), {
  fallback: defaultFallback,
  lazy: true,
  alt: 'image'
})

</script>

<style lang="scss" scoped>
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

.error-fallback .img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
