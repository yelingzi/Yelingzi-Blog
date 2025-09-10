<template>
  <div class="home-warp" :class="{ 'm-home-warp': isMobi }">

    <!--背景轮播-->
    <BackgroundImages></BackgroundImages>

    <!-- 品牌 -->
    <Brand></Brand>

    <div class="bg">
      <div class="main-container mt">
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
.mt {
  padding-top: 3rem;
}

.right-container {
  position: sticky;
  width: 18rem;
  margin-left: 0.8rem;
  top: 20px;
  align-self: flex-start;
  transition: all 0.2s ease;
}

.main-container {
  display: flex;
  position: relative; // 为绝对定位的子元素提供参考
}

// 移动端适配
@media (max-width: 768px) {
  .right-container {
    width: 100%;
    margin-left: 0;

    &.fixed-sidebar {
      right: 0;
    }
  }
}
</style>
