<template>
  <div v-slide-in class="article-item" v-for="article of articleList" :key="article.id">
    <!-- 文章缩略图 -->
    <ImageWithFallback v-pio="{ text: `${article.title}`, type: 'read' }" class="article-cover cover"
      :src="article.articleCover" @click="toArticle(article.id)" />
    <!-- 文章信息 -->
    <div class="article-info">
      <div class="article-meta">
        <!-- 置顶 -->
        <span class="top art-pa" v-if="article.isTop == 1">
          <SvgIcon name="icon-paihang" size="14" class="icon" />置顶
        </span>
        <!-- 发表时间 -->
        <span class="meta-item art-pa ml-3.75">
          <SvgIcon name="icon-rili1" size="14" style="color:#f6416c" class="icon" />
          {{ formatDate(article.updateTime) }}
        </span>
        <!-- 文章标签 -->
        <div class="meta-item art-pa tag-hover pointer" v-pio="{ text: `${tag.tagName} 这个标签`, type: 'look' }"
          @click="toTag(tag)" v-for="tag in article.tagList" :key="tag.id">
          <SvgIcon name="icon-youhuiquan" size="14" style="color:#00b8a9" class="icon" />{{ tag.tagName }}
        </div>
      </div>
      <!-- 文章标题 -->
      <h3 class="article-title">
        <div class="title-link pointer" v-pio="{ text: `${article.title}`, type: 'read' }"
          @click="toArticle(article.id)">
          {{ article.title }}
        </div>
      </h3>
      <!-- 文章内容 -->
      <div class="article-content working">{{ article.content }}</div>
      <!-- 文章分类 -->
      <div class="article-category art-pa">
        <SvgIcon name="icon-fenlei1" size="14" style="color:#ffde7d" class="icon" />
        <div @click="toCategory(article.category)" class="tag-hover pointer"
          v-pio="{ text: `${article.category.categoryName} 这个分类`, type: 'look' }">
          {{ article.category.categoryName }}
        </div>
      </div>
      <!-- 阅读按钮 -->
      <div class="article-btn pointer" v-pio="{ text: `${article.title}`, type: 'read' }"
        @click="toArticle(article.id)">
        more...
      </div>
    </div>
  </div>
  <div class="pagination">
    <proButton v-if="loadingArticle" v-loading.fullscreen.lock="loading" :info="i18n.loadMore + '...'" width="120px"
      before="#ed6ea0" after="#9cd0ed" @click="nextPage">
    </proButton>
    <el-card v-else style="width: 100%;">
      <div style="text-align: center;">{{ i18n.loadEnd }}</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import ImageWithFallback from '@/components/Image/ImageWithFallback.vue';
import proButton from '../Button/proButton.vue';
import { reactive, onMounted, ref } from "vue";
import { formatDate } from '@/utils/common';
import type { Article, ArticleList, Category, Tag } from '@/types/article';
import { getArticleListByPageService } from '@/api/article';
import { useRouter } from 'vue-router';
import { useI18nStore, useBlogStore } from '@/stores';

const router = useRouter()
const loading = ref(false)
const i18n = useI18nStore().currentConfig
const total = ref(0)
const page = ref(1)
const pageSize = ref(5)
const loadingArticle = ref(true)
const articleList = ref<Article[]>([]);

const getArticle = async () => {

  const articleData = await getArticleListByPageService(page.value, 5)

  const articles = articleData.data.data;
  total.value = articles.total
  for (const article of articles.data) {
    articleList.value.push(article)
  }
  if (page.value * pageSize.value >= total.value) {
    loadingArticle.value = false
  }
};


const nextPage = () => {
  page.value += 1
  getArticle()
}

const toTag = (tag: Tag) => {
  useBlogStore().tagId = tag.id
  router.push('tag')
}

const toArticle = (id: number) => {
  router.push({ name: 'article', params: { id: id } })
}

const toCategory = (category: Category) => {
  router.push({ name: 'category' })
}

onMounted(() => {
  getArticle()
});


</script>

<style lang="scss" scoped>
.art-pa {
  padding-right: 1.25rem;
}

.article-item {
  display: flex;
  height: 14rem;
  margin: 1.25rem 0.5rem 0;
  border-radius: 0.5rem;
  box-shadow: 0 0.625rem 1.875rem -0.9375rem var(--box-bg-shadow);
  animation-duration: 0.5s;
  transition: all 0.2s ease-in-out 0s;
  visibility: visible;

  &:hover {
    box-shadow: 0 0 1.5rem var(--box-bg-shadow);

    .cover {
      transform: scale(1.05) rotate(1deg);
    }
  }

  &:nth-child(even) {
    flex-direction: row-reverse;

    .article-cover {
      margin-right: auto;
      margin-left: 1.5rem;
      -webkit-clip-path: polygon(0 0, 100% 0, 100% 100%, 8% 100%);
      clip-path: polygon(0 0, 100% 0, 100% 100%, 8% 100%);
      border-radius: 0 0.625rem 0.625rem 0;
    }

    .article-info {
      padding: 1rem 0 3rem 1.5rem;

      .article-meta {
        justify-content: flex-start;
      }
    }

    .article-btn {
      left: 0;
      right: auto;
      border-radius: 0 1rem;
      background-image: linear-gradient(to right, var(--color-orange) 0, var(--color-pink) 100%);

      &:hover {
        transform: translateZ(2rem);
      }
    }

    .article-category {
      right: 0.5rem;
      justify-content: flex-start;
    }
  }
}

.article-cover {
  width: 50%;
  margin-right: 1.5rem;
  clip-path: polygon(0 0, 92% 0, 100% 100%, 0 100%);
  border-radius: 0.625rem 0 0 0.625rem;
  overflow: hidden;

  .cover {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: all 0.2s ease-in-out 0s;
  }
}

.icon {
  margin-right: 5px;
}

.tag-hover {
  &:hover {
    color: var(--color-blue);
  }
}

.article-info {
  position: relative;
  width: 50%;
  padding: 1rem 1.5rem 3rem 0;
  perspective: 62.5rem;

  .article-meta {
    display: flex;
    justify-content: flex-end;
    font-size: 0.8125rem;
    color: var(--grey-5);
  }

  .top {
    color: var(--color-orange);
  }

  .meta-item {
    display: flex;
    align-items: center;
  }

  .ml:not(:first-child) {
    margin-left: 0.625rem;
  }

  .article-title {
    position: relative; // 关键定位
    margin: 0.625rem 0;

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

  .article-content {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    max-height: 5rem;
    font-size: 14px;
    overflow: hidden;
  }
}

.article-title:hover .article-title::after {
  transform: translate3d(0, 0, 0);
}

.article-category {
  position: absolute;
  display: flex;
  align-items: center;
  bottom: 0.5rem;
  font-size: 0.8125em;
  color: var(--grey-5);
}

.article-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  padding: 0.3rem 1rem;
  border-radius: 1rem 0;
  color: var(--grey-0);
  background-image: linear-gradient(to right, var(--color-pink) 0, var(--color-orange) 100%);

  &:hover {
    transform: translateZ(2rem);
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
  padding: 0;
}

@media (max-width: 767px) {
  .article-item {
    flex-direction: column;
    height: fit-content;

    .article-cover {
      width: 100%;
      height: 14rem;
      margin: auto;
      clip-path: polygon(0 0, 100% 0, 100% 92%, 0 100%);
      border-radius: 0.625rem 0.625rem 0 0;
    }

    .article-info {
      width: 100%;
      height: 14rem;
      padding: 0 1rem 3rem;
    }

    &:nth-child(even) {
      flex-direction: column;

      .article-cover {
        width: 100%;
        margin: auto;
        clip-path: polygon(0 0, 100% 0, 100% 100%, 0 92%);
        border-radius: 0.625rem 0.625rem 0 0;
      }

      .article-info {
        padding: 0 1rem 3rem;
      }
    }
  }
}
</style>
