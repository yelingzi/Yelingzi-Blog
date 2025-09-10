<template class="custom-hr">
  <hr />
  <div class="hr-text">{{ content }}</div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

// 定义 props
const props = defineProps({
  margin: {
    type: String,
    default: '40px'
  },
  symbol: {
    type: String,
    default: '1'
  },
  content: {
    type: String,
    default: ''
  }
});


const symbolMap = {
  '1': '❄',
  '2': '❀',
  '3': '☯',
  '4': '♕',
  '5': '𝄞'
};

const dynamicSymbol = computed(() => {
  const symbolKey = props.symbol as keyof typeof symbolMap;
  return `"${symbolMap[symbolKey] || symbolMap['1']}"`;
});
</script>

<style lang="scss" scoped>
hr {
  position: relative;
  margin-top: v-bind('margin');
  margin-bottom: 15px;
  border: 2px dashed var(--color-blue);
  overflow: visible;
}

hr:before {
  position: absolute;
  top: -14px;
  left: 5%;
  color: var(--color-blue);
  content: v-bind('dynamicSymbol');
  font-size: 30px;
  line-height: 1;
  transition: all 1s ease-in-out;
}

hr:hover:before {
  left: calc(95% - 20px);
}

.custom-hr {
  display: flex;
  // align-items: center;
  // margin: v-bind('margin') 0;
}


.hr-text {
  margin-left: 5%;
  color: var(--color-blue);
  font-size: 30px;
  line-height: 1;
  transition: all 1s ease-in-out;
  font-style: italic;
  font-weight: bold;
  margin-bottom: v-bind('margin');
}
</style>
