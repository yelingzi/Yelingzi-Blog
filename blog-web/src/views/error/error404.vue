<template>
  <div class="container">
    <div class="image">
      <el-image :src="img" class="carousel-image"></el-image>
    </div>
    <div class="text">
      <h1>页面找不到了!!!</h1>
    </div>
    <div class="text">
      <el-button size="large" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import img from '@/assets/images/404.jpg';

const router = useRouter();

const goHome = () => {
  router.push('/')
};
</script>

<style scoped lang="scss">
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  /* 确保容器占据整个视口高度 */
  text-align: center;
  /* 文本居中 */
}

.image {
  margin-bottom: 20px;
}

.carousel-image {
  max-width: 100%;
  max-height: 300px;
  /* 限制图片高度 */
}

.text {
  margin-bottom: 20px;
}
</style>
