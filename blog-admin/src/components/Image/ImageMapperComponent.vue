<template>
  <ImageWithFallback :src="imagePath" :alt="text" class="image-mapper" />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { textToImageMap } from '@/utils/mapping';
import ImageWithFallback from './ImageWithFallback.vue';

// 定义组件属性
interface Props {
  text: string;
  basePath?: string;
}

const props = withDefaults(defineProps<Props>(), {
  basePath: '/images/emoji/',
});

// 解析图片路径
const imagePath = computed(() => {
  const imageName = textToImageMap[props.text];
  if (imageName) {
    return `${props.basePath}${imageName}.jpg`;
  }
  return ""

});
</script>



<style scoped>
.image-mapper {
  max-width: 100%;
  height: auto;
  display: inline-block;
}
</style>
