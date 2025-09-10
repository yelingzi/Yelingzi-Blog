<template>
  <svg aria-hidden="true" class="svg-icon" :style="{ width: size, height: size }">
    <use :xlink:href="symbolId" />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps({
  prefix: {
    type: String,
    default: 'icon',
  },
  name: {
    type: String,
    required: true,
  },
  color: {
    type: String,
    default: 'currentColor', // 默认使用当前颜色
  },
  size: {
    type: [String, Number],
    default: '1em',
  },
});

const symbolId = computed(() => `#${props.name}`);
</script>

<style scoped>
.svg-icon {
  display: inline-block;
  vertical-align: middle;
  overflow: hidden;
  outline: none;
}

/* 确保使用 currentColor */
.svg-icon use {
  fill: currentColor;
}
</style>
