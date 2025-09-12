<!-- src/providers/I18nProvider.vue -->
<template>
  <slot v-if="i18nStore.isLangLoaded"></slot>
  <div v-else-if="i18nStore.isLoading" class="loading-container">
    <el-icon class="is-loading" :size="30">
      <Loading />
    </el-icon>
    <span>加载语言资源中...</span>
  </div>
  <div v-else class="error-container">
    <el-alert title="语言加载失败" type="error" :description="i18nStore.error || '未知错误'" show-icon closable />
    <el-button type="primary" @click="retry">重试</el-button>
  </div>
</template>

<script lang="ts" setup>
import { useI18nStore } from '@/stores'

const i18nStore = useI18nStore()

const retry = async () => {
  await i18nStore.initAppLang()
}
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 20px;
}

.error-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 20px;
}
</style>
