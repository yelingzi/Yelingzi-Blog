<template>
  <I18nProvider>
    <RouterView></RouterView>
  </I18nProvider>
</template>

<script lang="ts" setup>
import { RouterView } from 'vue-router'
import { init } from './init';
import I18nProvider from './components/Language/I18nProvider.vue';
import { onMounted } from 'vue'

onMounted(async () => {

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
