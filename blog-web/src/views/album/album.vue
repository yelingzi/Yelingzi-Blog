<template>
  <CommonLayout :title="i18n.album" :bg-img="image" />
  <div class="bg">
    <div class="page-container">
      <div class="album-container">
        <div class="album-item" v-pio="{ text: `${album.albumName}`, type: 'album' }" v-for="album in albumList"
          :key="album.id">
          <ImageWithFallback class="album-cover" :src="album.albumCover" />
          <router-link :to="`/albums/${album.id}`" class="album-info">
            <div class="album-name">{{ album.albumName }}</div>
            <div class="album-desc">{{ album.albumDesc }}</div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommonLayout from "../Layout/CommonLayout.vue";
import ImageWithFallback from "@/components/Image/ImageWithFallback.vue";
import type { Album } from "@/types/album";
import image from "@/assets/images/album-bg.jpg"
import { ref, onMounted } from "vue";
import { getAlbumListService } from "@/api/album";
import { useI18nStore } from "@/stores";

const i18n = useI18nStore().currentConfig
const albumList = ref<Album[]>([]);

const getAlbumList = async () => {
  const res = await getAlbumListService()
  albumList.value = res.data.data
}

onMounted(() => {
  getAlbumList()

})
</script>

<style lang="scss" scoped>
.album-container {
  display: flex;
  flex-wrap: wrap;
}

.album-item {
  position: relative;
  width: calc(50% - 0.5rem);
  aspect-ratio: 16 / 9;
  margin: 0.375rem 0.25rem;
  border-radius: 0.5rem;
  background: #000;
  overflow: hidden;
}

.album-cover {
  position: relative;
  max-width: none;
  width: calc(100% + 1.25rem);
  aspect-ratio: 16 / 9;
  transition: opacity 0.35s, transform 0.35s;
  transform: translate3d(-10px, 0, 0);
  object-fit: cover;
  opacity: 0.8;
}

.album-info {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1.8rem 2rem;
  color: #fff !important;
}

.album-name {
  position: relative;
  font-weight: 700;
  font-size: 1.8rem;
  padding: .7rem 0;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background: #fff;
    transition: transform 0.35s;
    transform: translate3d(-100%, 0, 0);
  }
}

.album-desc {
  padding: 0.4rem 0 0;
  line-height: 1.5;
  font-size: 1.2rem;
  transition: opacity 0.35s, transform 0.35s;
  transform: translate3d(100%, 0, 0);
  opacity: 0;
}

.album-item:hover .album-desc {
  opacity: 1;
  transform: translateZ(0);
}

.album-item:hover .album-name::after {
  transform: translate3d(0, 0, 0);
}

@media (max-width: 567px) {
  .album-item {
    width: calc(100% - 0.5rem);
  }
}
</style>
