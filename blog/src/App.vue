<template>
  <div v-if="i18nStore.isLangLoaded">
    <RouterView></RouterView>

  </div>
</template>

<script lang="ts" setup>
import { RouterView } from 'vue-router'
import { init } from './init';
// 在组件中
import { useI18nStore } from './stores';
import { onMounted } from 'vue'

const i18nStore = useI18nStore()


onMounted(async () => {
  try {
    if (!i18nStore.isLangLoaded) {
      await i18nStore.loadLang('zh-CN')
    }
  } finally {
    i18nStore.isLangLoaded = true
  }
  init()
  console.log(
    "   __     ________ _      _____ _   _  _____ ___________ \n" +
    "   \\ \\   / /  ____| |    |_   _| \\ | |/ ____|___  /_   _|\n" +
    "    \\ \\_/ /| |__  | |      | | |  \\| | |  __   / /  | |  \n" +
    "     \\   / |  __| | |      | | | . ` | | |_ | / /   | |  \n" +
    "      | |  | |____| |____ _| |_| |\\  | |__| |/ /__ _| |_ \n" +
    "      |_|  |______|______|_____|_| \\_|\\_____/_____|_____|"
  );
})

</script>

<style lang="scss">
/** 用于加载全局变量 */
.svg-icon use {
  fill: currentColor !important;
}
</style>
