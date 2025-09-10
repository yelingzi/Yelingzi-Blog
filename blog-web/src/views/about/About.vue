<!-- about -->
<template>
  <el-image class="img" :src="image" lazy fit="cover">
    <template #error>
      <div class="error-fallback">
        <el-image :src="defaultFallback" alt="404" />
      </div>
    </template>
  </el-image>
  <div class="tiele-container">
    <div class="tiele">
      <!-- 标题 -->
      <p class="arttiele">{{ i18n.about }}</p>
    </div>
  </div>
  <div class="page-container bg">
    <!-- 头像 -->
    <div class="avatar">
      <el-avatar class="about-avatar" :size="160" :src="ava" />
    </div>
    <div class="about-content">
      <p>大家好！我是叶玲子</p>
      <p>欢迎光临次元裂缝中的小栈</p>
      <p>这里堆满像素泡面与魔法阵奶茶</p>
      <p>我会用左手的函数编织异世界攻略</p>
      <p>用右手的画笔记录动漫星轨的秘语</p>
      <p>当数据洪流淹没黄昏的服务器时</p>
      <p>请收下这串治愈代码：</p>
      <p>while(true){ console.log("要开心哦~") }</p>
      <p>在次元裂缝中漂泊的每个灵魂</p>
      <p>都有二进制写不完的传说</p>
      <p>魔法少女的裙摆藏着死循环</p>
      <p>机甲骑士的剑锋闪烁递归星辰</p>
      <p>--------------------------------------------------</p>
      <p>忘词了</p>
    </div>
    <snowHr />

    <FriendInfoSection :title="i18n.aboutMe" :items="aboutme" />

    <FriendInfoSection :title="i18n.aboutBlog" :items="aboutBlog" />

  </div>
</template>

<script setup lang="ts">
import defaultFallback from '@/assets/images/404.gif'
import snowHr from '@/components/Hr/DividerLine.vue';
import FriendInfoSection from '../friend/FriendInfoSection.vue';
import image from '@/assets/images/about-me.jpg'
import ava from '@/assets/images/avatar2.jpg'
import { ref } from 'vue';
import { useI18nStore } from '@/stores';

const aboutBlog = ref(['本网站基于Vue3+TypeScript前端架构与Spring Boot后端技术栈构建，将长期维护和持续更新。',
  '客户端页面参考借鉴了一些优秀的开源项目，并对使用开源项目代码做了大量修改以适配vue3和TypeScript。',
  '管理后台和服务端为我从零开始学习，设计和开发，包括架构设计、数据库建模到核心功能实现的全过程。'])
const aboutme = ref(['一位正在寻找工作的苦逼大学生...'])
const i18n = useI18nStore().currentConfig
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.tiele-container {
  @include m.flex;
  flex-direction: column;
  position: relative;
  width: 100%;
  height: 42vh;
  color: var(--color-pink);
}

.tiele {
  @include m.flex;
  flex-direction: column;
  position: fixed;
  z-index: -1;

  .arttiele {
    margin-top: 10vh; // 向下移动 10 视口高度
    font-family: "Fredericka the Great", Mulish, -apple-system, "PingFang SC", "Microsoft YaHei",
      sans-serif;
    font-size: 3.5em;
    line-height: 1.2;
    animation: titleScale 1s;
  }

  .title {
    letter-spacing: 0.1em;
  }
}

.img {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw; // 改用视窗单位
  height: 42vh;
  z-index: -9;
}

.error-fallback {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%; // 继承父容器高度
  display: flex; // 使用 flex 布局
  background: #f5f5f5;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block; // 消除图片默认间隙
    filter: grayscale(80%) blur(1px);
    opacity: 0.9;
  }
}

.avatar {
  margin-top: 3rem;
  text-align: center;
}

.about-avatar {
  transition: all 0.5s;
}

.about-avatar:hover {
  transform: rotate(360deg);
}

.about-content {
  word-break: break-word;
  line-height: 2.4;
  font-size: 1.5rem;
  text-align: center;
}
</style>
