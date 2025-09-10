<template>
  <snowHr :symbol="'5'" />
  <h2 class="category-title">{{ props.title }}</h2>
  <div class="card-container" v-if="props.friendList">
    <div v-for="(resourcePath, index) in props.friendList" :key="index" class="card-item shadow-box "
      @click="clickResourcePath(resourcePath)" v-pio="{ text: '想要前往这个友链吗？' }">
      <div class="card-image">
        <img :src="resourcePath.cover" class="pointer" />
      </div>
      <div class="card-body pointer">
        <div class="card-title pointer">
          <span v-if="resourcePath.recommendStatus">🔥</span>
          {{ resourcePath.title }}
        </div>
        <div class="card-desc pointer">
          {{ resourcePath.introduction }}
        </div>
        <div class="card-time pointer">
          <SvgIcon name="icon-rili1" class="icon-time" />
          添加于 {{ getDateDiff(resourcePath.createTime) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Friend } from '@/types/friend'
import { getDateDiff } from '@/utils/common'
import snowHr from '@/components/Hr/DividerLine.vue';

const props = defineProps(['friendList', 'title'])

const emit = defineEmits<{
  (e: 'click-resource-path', url: string): void
}>()

const clickResourcePath = (resourcePath: Friend) => {
  emit('click-resource-path', resourcePath.url)
}
</script>

<style lang="scss" scoped>
.card-container {
  display: flex;
  flex-wrap: wrap;
}

.card-item {
  position: relative;
  border-radius: 10px;
  overflow: hidden;
  background-color: var(--grey-1-a3);
  margin: 10px;
  height: 300px;
  flex-shrink: 0;
  width: calc(100% / 3 - 20px);
  animation: zoomIn 0.8s ease-in-out;
}

.shadow-box {
  box-shadow: 0 1px 20px -6px rgb(0, 0, 0, 0.5);
  transition: all 0.3s ease;
}

.card-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block; // 避免图片底部留白
    transition: all 1s;

    &:hover {
      transform: scale(1.2);
    }
  }

}

.card-body {
  padding: 10px 20px 0 20px;
  color: var(--grey-9);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  transition: all .2s ease-in-out;
}

.card-title:hover {
  color: var(--color-pink);
}

.card-desc {
  height: 40px;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  letter-spacing: 1px;
}

.card-time {
  display: flex;
  justify-content: flex-end;
  gap: 3px; // 图标与文字间距
  font-size: 12px;
  align-items: center;

  span {
    flex-shrink: 0;
  }

  .icon-time {
    vertical-align: 0.5rem;
  }
}

.icon-container {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;

}

@media screen and (max-width: 700px) {
  .card-item {
    width: calc(100% / 2 - 20px);
  }
}

@media screen and (max-width: 500px) {
  .card-item {
    width: calc(100% - 20px);
  }
}
</style>
