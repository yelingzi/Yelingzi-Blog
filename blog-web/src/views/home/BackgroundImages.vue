<template>
  <div class="imgs">
    <ul>
      <li class="item" v-for="carousel of carouselList" :key="carousel.id" :style="{
        'background-image': 'url(' + carousel.url + ')'
      }">
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">


import { onMounted, ref } from 'vue';

import { getBgListService } from '@/api/album';
/**
 * 轮播图
 */
interface Carousel {
  id: number;
  url: string;
}
const carouselList = ref<Carousel[]>([]);

const getBg = async () => {
  const res = await getBgListService()
  carouselList.value = res.data.data
}

onMounted(() => {
  getBg()
});
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.imgs {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70vh;
  z-index: -9;
  background-color: #363636;
  overflow: hidden;

  .item {
    @include m.absolute;
    width: 100%;
    height: 100%;
    background: no-repeat 50% 50% / cover;
    opacity: 0;
    animation: imageAnimation 30s linear infinite 0s;
    backface-visibility: hidden;
    transform-style: preserve-3d;

    &:nth-child(2) {
      animation-delay: 6s;
    }

    &:nth-child(3) {
      animation-delay: 12s;
    }

    &:nth-child(4) {
      animation-delay: 18s;
    }

    &:nth-child(5) {
      animation-delay: 24s;
    }

  }

  &::before {
    content: '';
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .2);
    transition: all .2s ease-in-out 0s;
  }
}

@keyframes imageAnimation {
  0% {
    opacity: 0;
    animation-timing-function: ease-in;
  }

  2% {
    opacity: 1;
  }

  8% {
    opacity: 1;
    transform: scale(1.05);
    animation-timing-function: ease-out;
  }

  17% {
    opacity: 1;
    transform: scale(1.1);
  }

  25% {
    opacity: 0;
    transform: scale(1.1);
  }

  100% {
    opacity: 0;
  }
}
</style>
