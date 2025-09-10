<template>
  <el-carousel class="swiper-container" :pagination="{ clickable: true }">
    <el-carousel-item v-if="albumList.length > 0" v-for="alubum in albumList" :key="alubum.id"
      @click="toAlbum(alubum.id)">
      <div class="slide-content" :style="articleCover(alubum.albumCover)">
        <div :to="`/albums/${alubum.id}`" class="slide-title">{{ alubum.albumName }}</div>
        <span class="slide-time">简介：{{ alubum.albumDesc }}</span>
      </div>
    </el-carousel-item>
  </el-carousel>
</template>

<script setup lang="ts">
import { getSimpleAlbumOfPhotoCountService } from "@/api/album";
import type { Album } from "@/types/album";
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()
const articleCover = computed(() => (cover: string) => 'background:url(' + cover + ')');
const albumList = ref<Album[]>([]);

const getAlbumList = async () => {
  const res = await getSimpleAlbumOfPhotoCountService()
  albumList.value = res.data.data
}

const toAlbum = (id: number) => {
  console.log('pp')
  router.push({ name: 'albumById', params: { id: id } })
}

onMounted(() => {
  getAlbumList()

})
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.swiper-container {
  aspect-ratio: 16 / 9;
  margin: 1rem 0.5rem;
  border-radius: 0.75rem;
  position: relative;
  /* 添加定位以确保伪元素正确工作 */

  &::before {
    content: '相册';
    position: absolute;
    z-index: 2;
    color: var(--grey-0);
    background: linear-gradient(90deg, var(--color-yellow), var(--color-orange));
    top: 0;
    letter-spacing: 0.1875rem;
    left: 0.625rem;
    font-size: 0.9375rem;
    width: 4.0625rem;
    display: flex;
    justify-content: center;
    border-radius: 0 0 0.75rem 0.75rem;
  }
}

.slide-content {
  @include m.flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  padding: 0;
  background-position: center !important;
  background-size: cover !important;

  .slide-title {
    font-size: 2rem;
  }

  &::after {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.08);
    left: 0;
    top: 0;
  }
}

.slide-title,

.slide-time {
  text-align: center;
  line-height: 1.5;
  margin: 0.125rem 0;
  color: #fff;
  z-index: 1;
  position: relative;
}

// 调整轮播项容器
:deep(.el-carousel__container) {
  height: 100% !important; // 强制容器高度
}

:deep(.el-carousel-item) {
  height: 100%;
  display: flex !important; // 启用flex布局
}

:deep(.el-carousel__indicator) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

:deep(.el-carousel__button) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

:deep(.el-carousel__arrow) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

:deep(.el-carousel__arrow i) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}
</style>
