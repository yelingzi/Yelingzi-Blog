<template>
  <div class="layout-warp">

    <div class="layout-content-warp">
      <!-- 头部菜单 -->
      <MyHeader></MyHeader>
      <!-- main -->
      <main style="min-height: 100vh" id="main">
        <router-view></router-view>
      </main>
    </div>

  </div>
</template>

<script lang="ts" setup>
import { onMounted, provide, ref, watch } from 'vue';
import MyHeader from '@/components/Layout/Header/Header.vue';

onMounted(() => {
});


</script>

<style lang="scss" scoped>
.layout-warp {
  overflow: hidden;
  position: relative;


  @keyframes animation {
    0% {
      background-position: 0% 50%;
    }

    50% {
      background-position: 100% 50%;
    }

    100% {
      background-position: 0% 50%;
    }
  }


  .layout-content-warp {
    user-select: none;
    position: relative;
    width: 100%;
    height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
    left: 0;
    transition: left 0.8s ease;


    /* WebKit内核浏览器 */
    ::-webkit-scrollbar {
      width: 0px;
      height: 0px;
    }

    scrollbar-width: none;
    -ms-overflow-style: none;

    .header-warp {
      width: 100%;
      height: 60px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;
      background: var(--bg-warp-light-color-2);
      border-bottom: 1px solid var(--theme-light-color-5);
      transition: all 0.8s ease;
      position: fixed;
      left: 0;
      top: 0;
      z-index: 999;
      border-radius: 0px;
      font-weight: 600;

    }

  }
}
</style>
