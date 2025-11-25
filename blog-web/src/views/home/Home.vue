<template>
  <div class="home-warp">
    <!--背景轮播-->
    <BackgroundImages></BackgroundImages>
    <!-- 品牌 -->
    <Brand></Brand>

    <div class="bg">
      <div class="header"></div>
      <div class="main-container">
        <div class="left-container">
          <!-- 说说 -->
          <Talk></Talk>
          <!-- 推荐相册 -->
          <Recommend></Recommend>
          <!-- 文章列表 -->
          <ArticleItem></ArticleItem>
        </div>
        <div class="right-container">
          <SideBar></SideBar>
        </div>
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
</script>

<style lang="scss" scoped>
.home-warp {
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
  justify-content: space-between;
  align-items: flex-start;
  width: 1080px;
  margin: 0 auto;
  padding-bottom: 1.75rem;
}

.left-container {
  width: calc(100% - 18.75rem);
}

.right-container {
  position: sticky;
  top: 74px;
  width: 18rem;
  margin-left: 10px;
  align-self: flex-start;
}

/* 移动端适配 */
@media (max-width: 767px) {

  .main-container {
    width: 100%;
    flex-direction: column;
  }

  .left-container {
    width: 100%;
  }

  .right-container {
    position: absolute;
    left: -9999px;
    visibility: hidden;
    pointer-events: none;
    width: 18rem;
    height: 100%;
    overflow: hidden;
  }
}
</style>
