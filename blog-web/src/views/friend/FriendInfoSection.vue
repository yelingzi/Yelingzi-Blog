<template>
  <div class="message-container">
    <div class="header">
      <div>{{ props.title }}</div>
      <div v-if="isCopy" class="copy-btn pointer" @click="copyItems">{{ i18n.copy }}</div>
    </div>
    <div class="main">
      <div class="blockquote">
        <div v-if="isCopy" class="user-select" v-for="(item, index) in props.items" :key="index">
          {{ item }}
        </div>
        <div v-else v-for="(item, index) in props.items">
          {{ item }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { useI18nStore } from '@/stores'

const i18n = useI18nStore().currentConfig
const props = defineProps(['items', 'title', 'isCopy'])

const copyItems = async () => {
  try {
    const text = props.items.join('\n')
    await navigator.clipboard.writeText(text)
    ElMessage.success('已成功复制到剪贴板!')
  } catch (err) {
    ElMessage.warning('复制失败，请手动选择文本复制')
  }

}
</script>
<style lang="scss" scoped>
.header {
  font-size: 20px;
  font-weight: bold;
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .copy-btn {
    padding: 5px 12px;
    border-radius: 4px;
    background: #ffe6fa;
    transition: all 0.3s;
    font-size: 14px;

    &:hover {
      background: #ed6ea0;
      color: white;
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

.main {
  border-left: 0.2rem solid #ed6ea0;
  padding: 10px 1rem;
  margin: 20px auto;
  background-color: #ffe6fa;
  border-radius: 4px;
}

.blockquote {
  line-height: 2;
  color: #000;
}
</style>
