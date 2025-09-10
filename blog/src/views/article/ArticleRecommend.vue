<template>
  <el-card class="section" v-if="articleRecommend.length > 0">
    <h3>
      <SvgIcon name="icon-star" />
      推荐文章
    </h3>
    <div class="post-list">
      <router-link v-slide-in v-for="article of articleRecommend" :key="article.id" :to="`/article/${article.id}`"
        class="post-item">
        <el-image :src="article.articleCover" class="art-img" />
        <div class="post-meta">
          <div class="post-title">{{ article.title }}</div>
          <div class="post-time">
            <SvgIcon name="icon-rili1" />
            {{ article.createTime }}
          </div>
        </div>
      </router-link>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { getRecommendArticleListService } from '@/api/article';
import ImageWithFallback from '@/components/Image/ImageWithFallback.vue';
import type { ArticleRecommend } from '@/types/article';
import { onMounted, reactive, ref } from 'vue';
const articleRecommend = ref<ArticleRecommend[]>([])

const getArticleRecommend = async () => {
  if (articleRecommend.value.length > 0) {
    articleRecommend.value.splice(0, articleRecommend.value.length)
  }
  const res = await getRecommendArticleListService()
  articleRecommend.value = res.data.data
}


onMounted(() => {
  getArticleRecommend()
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as va;

.section {
  margin-bottom: va.$spacing-lg;
  margin-top: 20px;
  background: var(--grey-1);
  border-radius: va.$border-radius-lg;

  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #6366f1;
    margin: 0px 0px 12px 0px;
    padding-bottom: 8px;
    border-bottom: 2px solid rgba(99, 102, 241, 0.1);
    display: flex;
    align-items: center;
    gap: 5px;

    &::before {
      content: '';
      display: inline-block;
      width: 4px;
      height: 18px;
      background: linear-gradient(to bottom, #6366f1, #8b5cf6);
      margin-right: 8px;
      border-radius: 2px;
      vertical-align: middle;
      transform: translateY(-1px);
    }
  }

  .post-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
    counter-reset: post-counter;

    .post-item {
      display: flex;
      gap: 16px;
      text-decoration: none;
      transition: all 0.3s ease;
      position: relative;
      align-items: center;

      /* 添加垂直居中 */
      &:hover {
        transform: translateX(4px);

        .post-title {
          color: #f06f81 !important;
        }

      }

      .art-img {
        width: 100px;
        height: 70px;
        border-radius: 6px;
        object-fit: cover;
        transition: transform 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

        &.fallback {
          opacity: 0.7;
        }

        &:hover {
          transform: scale(1.06) !important;
        }
      }

      .post-meta {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .post-title {
          font-size: 0.95rem;
          height: 50px;
          font-weight: 500;
          color: var(--grey-9);
          margin-bottom: 6px;
          word-wrap: break-word;
          white-space: normal;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .post-time {
          font-size: 0.7rem;
          /* 调小字体大小 */
          color: #8b8b8b;
          display: flex;
          align-items: center;
          gap: 4px;

        }
      }
    }
  }
}
</style>
