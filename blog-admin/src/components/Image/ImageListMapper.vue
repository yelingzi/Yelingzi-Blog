<template>
  <div class="image-list-mapper">
    <div v-for="(value, key) in filteredMap" :key="key" class="image-item pointer" @click="handleClick(key)">
      <ImageWithFallback :lazy="false" :src="value" :alt="key" class="image-preview" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineEmits, onMounted } from 'vue';
import { textToImageMap } from '@/utils/mapping';
import ImageWithFallback from './ImageWithFallback.vue';

const emit = defineEmits<{
  (e: 'select', value: string): void;
}>()

const basePath = '/images/emoji/';

const filteredMap = computed(() => {
  const result: Record<string, string> = {};

  Object.entries(textToImageMap).forEach(([key, value]) => {
    if (key && value && key.trim() && value.trim()) {
      result[key] = `${basePath}${value}.jpg`
    }
  });

  return result;
});


// 点击处理
const handleClick = (key: string): void => {
  emit('select', key);
};
onMounted(() => {

});
</script>

<style scoped>
.image-list-mapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 1rem;
  padding: 1rem;
}

.image-item {
  transition: all 0.2s ease-in-out;
}

.image-item:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview {
  max-width: 80px;
  max-height: 80px;
}

.image-label {
  font-size: 0.9rem;
  color: #333;
}
</style>
