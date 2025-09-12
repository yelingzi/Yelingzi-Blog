<template>
  <div class="home-warp" :class="{ 'm-home-warp': isMobi }">

    <!--背景轮播-->
    <BackgroundImages></BackgroundImages>

    <!-- 品牌 -->
    <Brand></Brand>

    <div class="bg">
      <div class="header"></div>
      <div class="main-container">
        <div class="left-container" :class="isMobi ? 'test' : ''">
          <!-- 说说 -->
          <Talk></Talk>
          <!-- 推荐文章 -->
          <Recommend></Recommend>
          <!-- 文章列表 -->
          <ArticleItem></ArticleItem>
        </div>

        <SideBar class="right-container"></SideBar>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
import BackgroundImages from './BackgroundImages.vue';
import Brand from './Brand.vue';
import Talk from './Talk.vue';
import Recommend from './Recommend.vue';
import SideBar from '@/components/SideBar/SideBar.vue';
import ArticleItem from '@/components/Article/ArticleItem.vue';
import { useResize } from '@/utils/common';

import { onMounted, onUnmounted, reactive, ref } from 'vue';

const isMobi = useResize();



onMounted(() => {

});

onUnmounted(() => {
});
</script>

<style lang="scss" scoped>
.home-warp {
  /* 移除 padding-top */
  box-sizing: border-box;
}

.bg {
  width: 100%;
  position: relative;
  z-index: 1;
  transition: margin-top 0.3s ease;
  background-color: var(--grey-1);
}

.header {
  height: 60px;
}

.main-container {
  display: flex;
  position: relative;
  align-items: flex-start;
  justify-content: center;
  width: 1080px;
  margin: 0 auto;
  padding-bottom: 1.75rem;
  animation: slideUpIn 1s;
}

.left-container {
  width: calc(100% - 18.75rem);
  transition: all 0.3s;
}

.right-container {
  position: sticky;
  width: 18rem;
  margin-left: 0.8rem;
  top: 20px;
  align-self: flex-start;
  transition: all 0.2s ease;
}

/* 移动端适配 */
@media (max-width: 767px) {
  .bg {
    margin-top: 60vh;
  }

  .main-container {
    width: 100%;
    padding: 0 15px;
    flex-direction: column;
  }

  .left-container {
    width: 100%;
  }

  .right-container {
    width: 100%;
    margin-left: 0;
    margin-top: 20px;
    position: static;
  }
}
</style>
