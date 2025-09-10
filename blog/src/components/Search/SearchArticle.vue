<template>
  <div class="container">
    <div v-if="articleList.length > 0" v-slide-in class="article-item" v-for="article of articleList" :key="article.id">
      <div class="item-left">
        <ImageWithFallback class="image" :src="article.articleCover" />
      </div>
      <div class="item-right">
        <div class="title">
          <div class="title-link pointer" v-pio="{ text: `${article.title}`, type: 'read' }"
            @click="handleClick(article.id)">
            {{ article.title }}
          </div>
        </div>
        <div class="item-content">
          {{ article.content }}
        </div>
        <div class="item-footer">
          <el-tag>
            <font-awesome-icon icon="fa-solid fa-folder-closed" style="color: #FFD43B;margin-right: 0.15rem" />
            {{ article.category.categoryName }}
          </el-tag>
          <div>
            <font-awesome-icon icon="fa-solid fa-calendar-days" style="margin-right: 0.15rem" />
            {{ formatDate(article.updateTime) }}
          </div>
        </div>
      </div>
    </div>
    <div v-else v-slide-in class="empty">
      <el-empty description="搜索内容为空"></el-empty>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { searchArticleListService } from '@/api/article';
import type { Article } from '@/types/article';
import { ref, watch } from 'vue';
import ImageWithFallback from '../Image/ImageWithFallback.vue';
import { formatDate } from '@/utils/common';

interface Props {
  search?: string
}
const props = withDefaults(defineProps<Props>(), {
  search: ''
})
const articleList = ref<Article[]>([])
const emit = defineEmits(['navigate'])

const handleClick = (id: number) => {
  emit('navigate', id)  // 触发导航事件
}
const fetchData = async (search: string) => {
  const res = await searchArticleListService(search)
  articleList.value = res.data.data
}

watch(
  () => props.search,
  (newValue) => {
    if (newValue) {
      fetchData(newValue)
    }
  },
  { immediate: true }
)

</script>

<style lang="scss" scoped>
@use '/src/assets/styles/transition.scss' as tr;

.container {
  max-width: 100%;
  padding: 0px !important;
}

.article-item {
  display: flex;
  width: 100%;
  gap: 12px;
  border-radius: 10px;
  background-color: antiquewhite;
  height: 150px;
  animation: slideUpBigIn 0.5s ease-out forwards;
}

.item-left {
  width: 240px;
}

.item-right {
  margin-right: 5px;
}

.image {
  width: 240px;
  height: 150px;
  border-radius: 10px 0 0 10px;
}

.item-content {
  height: 75px;
}

.title {
  position: relative; // 关键定位
  margin: 6px 0;
  font-size: 24px;

  .title-link {
    position: relative;
    display: inline-block;
    color: var(--primary-color);
    width: 100%;

    &::after {
      content: '';
      position: absolute;
      bottom: -3px; // 调整下划线位置
      left: 0;
      width: 100%;
      height: 2px;
      background: var(--primary-color);
      transform: scaleX(0);
      transform-origin: right;
      transition: transform 0.5s ease;
    }

    &:hover::after {
      transform: scaleX(1);
      transform-origin: left;
    }
  }
}

.item-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 6px;
}

.empty {
  animation: slideUpBigIn 0.5s ease-out forwards;
}

@keyframes slideUpBigIn {
  0% {
    opacity: 0;
    transform: translateY(80px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
