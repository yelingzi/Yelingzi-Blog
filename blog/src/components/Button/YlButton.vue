<template>
  <button class="my-button" :class="[focus ? 'pointer' : 'user-select-none', { disabled: !focus }]"
    :style="[buttonStyle, beforeStyle]" @click="$emit('click', $event)" :disabled="!focus">
    {{ info }}
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  info?: string
  defaultColor?: string
  focusColor?: string
  width?: string,
  focus: boolean
}

const props = withDefaults(defineProps<Props>(), {
  info: '确定',
  defaultColor: 'var(--color-cyan-light)',
  focusColor: 'var(--primary-color)',
  width: '66px',
  focus: false
})

// 计算按钮和子元素的样式
const buttonStyle = computed(() => ({
  '--btn-width': props.width
}))

const beforeStyle = computed(() => ({
  background: props.focus ? props.focusColor : props.defaultColor
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

  div {
    cursor: inherit;
  }
}

.my-button div {
  width: 100%;
  height: 100%;
  line-height: 33px;
  border-radius: 4px;
  text-align: center;
  position: absolute;

  &.disabled {
    pointer-events: none;
    opacity: 0.7;
  }
}
</style>
