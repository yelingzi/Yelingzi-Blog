<template>
  <div class="article-bg">

    <div class="article-header">
      <!-- 第一行: 标题 -->
      <h1 class="article-title">{{ article.title }}</h1>

      <!-- 第二行: 分类和标签 -->
      <div class="meta-row category-tag-row">
        <div v-if="article.isOriginal === 1" class="original">
          <el-tag type="success">
            原创
          </el-tag>
        </div>
        <div class="category-group">
          <span class="category">
            <el-tag type="warning">
              <SvgIcon name="icon-fenlei1" size="12" />
              {{ article.category.categoryName }}
            </el-tag>
          </span>
        </div>
        <div class="tag-group">
          <span v-for="tag of article.tagList" class="tag">
            <el-tag type="primary">
              <SvgIcon name="icon-youhuiquan" size="12" />
              {{ tag.tagName }}
            </el-tag>
          </span>
        </div>
      </div>

      <!-- 第三行: 时间信息和字数统计 -->
      <div class="meta-row time-stats-row">
        <div class="stats-group">
          <el-statistic :value="article.content.length">
            <template #title>
              <SvgIcon name="icon-bianji" size="12" />
              字数统计
            </template>
          </el-statistic>
        </div>
        <div class="time-group" v-if="article.updateTime">
          <el-statistic :value="article.readCount">
            <template #title>
              <SvgIcon name="icon-read" size="16" style="vertical-align: -0.3em;" />
              浏览量
            </template>
          </el-statistic>
        </div>
        <div class="time-group" v-if="article.updateTime">
          <el-statistic :value="formatDate(article.updateTime)">
            <template #title>
              <SvgIcon name="icon-rili1" size="12" class="icon" />
              更新于
            </template>
          </el-statistic>
        </div>
      </div>
    </div>

    <bgImage :src="article.articleCover" :transition-duration="1000" />


    <snowHr margin="10px"></snowHr>

    <div class="article-page">

      <!-- 中间主内容区（修改为竖向排列） -->
      <main class="article-main-container" :style="{ marginRight: showSidebar ? '320px' : '60px' }">
        <div>
          <H1 class="mian-title">{{ article.title }}</H1>
          <div class="mian-theme">
            <el-text style="align-items: center;font-size: 16px;">文章展示主题：</el-text>
            <el-select v-model="previewTheme" placeholder="请选择主题" style="width: 200px;margin-right: 20px;">
              <el-option label="默认" value="default" />
              <el-option label="github" value="github" />
              <el-option label="vuepress" value="vuepress" />
              <el-option label="mk-cute" value="mk-cute" />
              <el-option label="smart-blue" value="smart-blue" />
              <el-option label="cyanosis" value="cyanosis" />
            </el-select>
          </div>
        </div>
        <div class="article-main">
          <!-- 文章内容 -->
          <MdPreview :id="'article'" v-model="article.content" ref="preview" @onGetCatalog="getCatalog"
            :previewTheme="previewTheme" :theme="isDark ? 'dark' : 'light'" />
        </div>
        <!-- 文章底部分割线 -->
        <el-divider border-style="dotted" />
        <!-- 文章页脚 -->
        <ArticleFooter :article="article" :show-share="showShareMenu" @like="handleParentLike" @share="toggleShareMenu"
          @copy="copyLink" />
        <!-- 评论 -->
        <Comment :id="props.id" :comment-type="1" :author-id="article.userId" />
      </main>

      <!-- 右侧边栏 -->
      <div class="fixed-right-aside desktop-only" :style="{ width: showSidebar ? '320px' : '60px' }">
        <el-affix :offset="showSidebar ? 90 : 360">
          <div v-if="showSidebar" style="margin-left: 10px;">
            <el-card class="toc-container">
              <div class="toc-header">
                <div class="title-wrapper">
                  <SvgIcon name="icon-list" size="20" color="#49b1f5" class="icon" />
                  <span>目录</span>
                </div>
                <div class="progress-wrapper">
                  <SvgIcon name="icon-read" size="18" style="vertical-align: -0.3em;" class="icon" />
                  <span class="progress-text">{{ readProgress }}</span>
                </div>
              </div>
              <div class="toc-content">
                <MdCatalog v-if="scrollElement" :editorId="'article'" :scrollElement="scrollElement"
                  :scrollElementOffsetTop="100" :offsetTop="120" @onActive="handleActiveHeading" />
              </div>
            </el-card>

            <ArticleAction :position="actionBarLeft" :article="article" @like="handleParentLike"
              @favorite="toggleFavorite" @toggle-sidebar="toggleSidebar" @scroll-comments="scrollToComments">
            </ArticleAction>

            <!-- 推荐文章 -->
            <ArticleRecommend />
          </div>

          <div v-else>

            <el-card class="simple-right-sidear">

              <div class="action-button" @click="toggleSidebar">
                <SvgIcon name="icon-list" size="18" />
              </div>

              <el-badge :value="article.likeCount || 0">
                <div class="action-button" @click="toggleLike">
                  <SvgIcon name="icon-dianzan_kuai" size="18" class="icon-avtive" :class="{ active: article.isLike }" />
                </div>
              </el-badge>

              <el-badge :value="article.starCount || 0">
                <div class="action-button" @click="toggleFavorite">
                  <SvgIcon name="icon-star" size="18" class="icon-avtive" :class="{ active: article.isStar }" />
                </div>
              </el-badge>

              <div class="action-button" @click="toggleSidebar">
                <SvgIcon name="icon-huanyuan" size="18" />
              </div>

              <el-badge :value="article.commentCount || 0">
                <div class="action-button">
                  <SvgIcon name="icon-xiaoxi" size="18" />
                </div>
              </el-badge>

            </el-card>

          </div>

        </el-affix>
      </div>

    </div>

  </div>
</template>

<script lang="ts" setup>
import 'md-editor-v3/lib/style.css';
import { ref, reactive, onMounted, onUnmounted, nextTick, watch, inject, type Ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { Article } from '@/types/article'
import { ElMessage } from 'element-plus'
import { formatDate, numberToBoolean } from '@/utils/common'
import { MdCatalog, MdPreview, type HeadList } from 'md-editor-v3'
import snowHr from '@/components/Hr/DividerLine.vue'
import ArticleAction from './ArticleAction.vue'
import ArticleFooter from './ArticleFooter.vue'
import ArticleRecommend from './ArticleRecommend.vue'
import Comment from '@/components/Comment/Comment.vue'
import bgImage from '@/components/Image/BgImage.vue';
import { addArticleLikeService, delArticleLikeService, getArticleByIdService, getArticleLikeService } from '@/api/article'
import { useUserStore, useBlogStore } from '@/stores';

const blogStore = useBlogStore();
const isDark = computed(() => blogStore.blogInfo.isDark);
const userState = useUserStore()
const route = useRoute()
const router = useRouter()
const props = defineProps(['id'])
// 响应式数据
const article = ref<Article>({
  id: 0,
  title: '',
  category: { id: 0, categoryName: '' },
  tagList: [],
  likeCount: 0,
  starCount: 0,
  createTime: '',
  updateTime: '',
  content: '',
  isOriginal: 0,
  userId: 0,
  originalUrl: '',
  nickname: '',
  readCount: 0,
  commentCount: 0,
  userAvatar: '',
  isTop: 0,
  state: 0,
  articleCover: '',
  isLike: false,
  isStar: false
})
const readProgress = ref(0)

const actionBarLeft = ref(0)
const showShareMenu = ref(false)
const loading = ref(false)
const showSidebar = ref(true)
const previewTheme = ref('default')

const layoutRef = inject<Ref<HTMLElement | null>>('scrollContainer', ref<HTMLElement | null>(null));

const scrollElement = computed(() => {
  return layoutRef.value || undefined;
});

const totalHeadings = ref(0);
// 获取文章详情
const getArticle = async () => {
  try {
    const res = await getArticleByIdService(props.id);

    if (res.data.code === 0) {
      // 如果返回的状态码为0，跳转到404页面
      router.push('/404');
      return;
    }

    article.value = res.data.data;

    if (userState.getIsLogin()) {
      getArticleLike();
    }
  } catch (error) {
    // 如果请求失败，跳转到404页面
    console.error('请求文章失败:', error);
    router.push('/404');
  }
};

const getArticleLike = async () => {
  const res = await getArticleLikeService(article.value.id)
  article.value.isLike = numberToBoolean(res.data.data)
}

const getCatalog = (HeadList: HeadList[]) => {
  totalHeadings.value = HeadList.length
};

// 高亮标题变化事件
const handleActiveHeading = (heading: any) => {

  if (totalHeadings.value <= 0) {
    return;
  }
  readProgress.value = Math.round((heading.index / totalHeadings.value) * 100);
};
const handleParentLike = async () => {
  console.log("父组件接收到点赞事件");

  if (isRequesting.value) return;

  try {
    isRequesting.value = true;

    if (!userState.getIsLogin()) {
      ElMessage.warning("请先登录");
      return;
    }

    if (article.value.isLike) {
      await delArticleLikeService(article.value.id);
      article.value.likeCount -= 1;
    } else {
      await addArticleLikeService(article.value.id);
      article.value.likeCount += 1;
    }

    article.value.isLike = !article.value.isLike;
  } finally {
    setTimeout(() => {
      isRequesting.value = false;
    }, 600);
  }
};
// 生命周期
onMounted(() => {
  getArticle()
})

onUnmounted(() => {

})

// 监听路由变化
watch(
  () => props.id,
  (newId) => {
    if (newId) getArticle()
  }
)

// 侧边栏切换
const toggleSidebar = () => {
  showSidebar.value = !showSidebar.value

}

const toggleFavorite = () => {
  ElMessage.warning('暂未开放')
  return
}


const scrollToComments = () => {
  const commentsSection = document.querySelector('.comment-section')
  if (commentsSection) {
    commentsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const like = () => {
  console.log("Parent toggleLike called");

  toggleLike()
}

// 点赞功能
const isRequesting = ref(false)
// 点赞功能
const toggleLike = async () => {
  console.log("Parent toggleLike called");
  if (isRequesting.value) return

  try {
    isRequesting.value = true

    if (!userState.getIsLogin()) {
      ElMessage.warning("请先登录")
      return
    }

    // 执行点赞/取消操作
    if (article.value.isLike) {
      await await delArticleLikeService(article.value.id)
      article.value.likeCount -= 1
    } else {
      await addArticleLikeService(article.value.id)
      article.value.likeCount += 1
    }

    // 状态切换
    article.value.isLike = !article.value.isLike
  } finally {
    setTimeout(() => {
      isRequesting.value = false
    }, 600)
  }
}

/**
 * 分享
 */
const toggleShareMenu = () => {
  showShareMenu.value = !showShareMenu.value
}
/**
 * 关闭分享
 */
const closeShareMenu = () => {
  showShareMenu.value = false
}
const currentUrl = () => {
  return window.location.href
}
/**
 * 复制链接
 */
const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(currentUrl())
    ElMessage.success('链接已复制到剪贴板')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
  closeShareMenu()
}


</script>

<style lang="scss" scoped>
@use '@/assets/styles/mixin.scss' as mi;


.article-bg {
  background-color: var(--article-bg);
  z-index: -1;
}

.article-header {
  height: 25vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding-top: 4rem;
  min-height: 300px;

  .article-title {
    font-size: 2.8rem;
    margin-bottom: 1.5rem;
  }

  .meta-row {
    display: flex;
    align-items: center;
    margin-bottom: 1rem;
    gap: 1rem;

    &.category-tag-row {
      justify-content: center;
      flex-wrap: wrap;

      .original {
        display: flex;
        align-items: center;
        gap: 0.8rem;
      }

      .category-group {
        display: flex;
        align-items: center;
        gap: 0.8rem;
      }

      .tag-group {
        display: flex;
        align-items: center;
        gap: 0.8rem;
      }
    }

    &.time-stats-row {
      display: flex;
      justify-content: center;
      width: 100%;
      gap: 2rem;

      .stats-group,
      .time-group {
        display: flex;
        align-items: center;
      }
    }
  }
}

/* 容器定位修正 */
.article-page {
  position: relative;
  max-width: 1300px;
  margin: 0 auto;
}


/* 中间主内容区容器 */
.article-main-container {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--grey-9-a1);
  border-radius: 12px;
  transition: margin-right 0.5s ease;
  background-color: var(--md-bg);
}

.mian-title {
  text-align: center;
}

.mian-theme {
  display: flex;
  justify-content: flex-end;
}

/* 文章内容样式 */
.article-main {
  position: relative;
  flex: 1;
  box-sizing: border-box;
  overflow: auto;
  padding: 10px 25px;

}

// .md-editor {
//   background-color: var(--grey-1) !important;
// }

/* 右侧边栏样式 */
.article-sidebar {
  width: 300px;
  margin-left: 10px;
}

/* 目录容器样式 */
.toc-container {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  min-height: 160px;
}

.toc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 8px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  margin: 0;
  font-weight: bold;
  font-size: 18px;
  color: var(--color-blue);
}

.title-wrapper .icon {
  margin-right: 8px;
  color: var(--color-blue);
}

.progress-wrapper {
  display: flex;
  align-items: center;
  font-size: 0.9em;
  border-radius: 12px;
  background: rgba(73, 177, 245, 0.1);
  padding: 4px 8px;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  color: var(--color-aqua);

  .icon {
    margin-right: 8px;
    color: var(--color-blue);
  }
}

.progress-text {
  font-variant-numeric: tabular-nums;
  min-width: 3em;
  text-align: right;
}

.progress-text::after {
  content: '%';
  margin-left: 2px;
  opacity: 0.7;
}


/* 推荐文章样式 */
.article-recommend {
  margin-top: 24px;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}


.fixed-right-aside {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 100;
  height: 100%;
  transition: width 0.5s ease;
}

.fixed-right-aside-no {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 100;
  height: 100%;
  width: 60px;
  transition: width 0.3s ease;
}

.simple-right-sidear {
  width: 60px;
  margin-left: 10px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-button {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-bottom: 20px;

  &:hover {
    color: var(--color-pink);
  }
}

:deep(.md-editor-catalog-link span) {
  cursor: url('/src/assets/cursors/up.cur'), pointer
}

@keyframes slideInRight {
  from {
    transform: translate3d(100%, 0, 0);
    visibility: visible;
  }

  to {
    transform: translate3d(0, 0, 0);
  }
}

@keyframes like-animation {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.4);
    /* 放大到140% */
  }

  100% {
    transform: scale(1.1);
    /* 最终保持110%大小 */
  }
}

.active {
  color: var(--color-red);
  animation: like-animation 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
  display: inline-block;
}


.icon-avtive {
  transition: transform 0.3s ease-out;
}
</style>
