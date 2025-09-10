<template>
  <CommonLayout title="分类" :bg-img="bgImg" />
  <div class="bg">
    <div class="page-container">

      <!-- 分类导航 -->
      <div class="categories-nav">
        <div v-for="category in categories" :key="category.categoryName" class="category-tab"
          :class="{ active: activeCategory === category.categoryName }"
          @click="scrollToCategory(category.categoryName)">
          <SvgIcon name="icon-fenlei" />
          <span>{{ category.categoryName }}</span>
        </div>
      </div>

      <!-- 分类列表 -->
      <div v-for="item in categories">
        <div :key="item.categoryName" class="category-group" :data-category="item.categoryName"
          :ref="(el) => { if (el) categoryRefs[item.categoryName] = el as HTMLElement }">

          <h2 class="category-name">
            <SvgIcon name="icon-fenlei" />
            {{ item.categoryName }}
            <span class="post-count">{{ item.posts.length }} 篇文章</span>
          </h2>

          <DividerLine margin="0px" symbol="2"></DividerLine>

          <div class="posts-list">
            <div v-for="post in item.posts" :key="post.id" class="post-item" @click="goToPost(post.id)"
              v-pio="{ text: `${post.title}`, type: 'read' }">
              <div class="post-date">
                <span class="month">{{ getYear(post.createTime) }}.{{ getMonth(post.createTime) }}</span>
                <span class="day">{{ getDate(post.createTime) }}</span>
              </div>
              <div class="post-info">
                <h3 class="post-title">{{ post.title }}</h3>
              </div>
            </div>
          </div>
        </div>
      </div>


      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        加载中...
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onBeforeUnmount, inject, type Ref, type ComponentPublicInstance } from 'vue'
import { useRouter } from 'vue-router'
import CommonLayout from '../Layout/CommonLayout.vue'
import bgImg from '@/assets/images/bg-article.jpg'
import DividerLine from '@/components/Hr/DividerLine.vue'
import { getDate, getMonth, getYear } from '@/utils/common'
import { getArticleListFromCategoryService } from '@/api/article'
import type { Archives } from '@/types/article'


interface Category {
  id: number
  categoryName: string
  posts: Archives[]
}

const router = useRouter()
const categories = reactive<Category[]>([])
const layoutRef = inject<Ref<HTMLElement | null>>('scrollContainer', ref<HTMLElement | null>(null));
const activeCategory = ref<string | null>(null)
const loading = ref(false)
const categoryRefs = reactive<Record<string, HTMLElement>>({})


// 跳转到文章
const goToPost = (id: number) => {
  window.open(`/article/${id}`, '_blank');
}

// 滚动到分类
const scrollToCategory = (categoryName: string) => {

  activeCategory.value = categoryName;

  const element = categoryRefs[categoryName];

  if (!element) return;

  // 计算元素在容器中的位置
  const getScrollPosition = () => {
    if (layoutRef?.value) {
      const container = layoutRef.value;
      const containerRect = container.getBoundingClientRect();
      const elementRect = element.getBoundingClientRect();
      return elementRect.top - containerRect.top + container.scrollTop - 100;
    }
    return element.offsetTop - 100;
  };

  // 执行滚动
  if (layoutRef?.value) {
    // 自定义容器滚动
    layoutRef.value.scrollTo({
      top: getScrollPosition(),
      behavior: 'smooth'
    });
  } else {
    // 默认窗口滚动
    window.scrollTo({
      top: getScrollPosition(),
      behavior: 'smooth'
    });
  }
};

// 处理滚动
const handleScroll = () => {
  // 获取当前滚动位置（容器或窗口）
  const scrollPosition = layoutRef?.value
    ? layoutRef.value.scrollTop + 200  // 增加80px偏移量适配导航栏高度
    : window.scrollY + 200;

  // 遍历所有分类检测激活状态
  for (const category of categories) {
    const element = categoryRefs[category.categoryName];
    if (!element) continue;

    // 计算元素位置
    let elementTop = 0;
    let elementBottom = 0;

    if (layoutRef?.value) {
      // 容器内相对位置计算
      const container = layoutRef.value;
      const elementRect = element.getBoundingClientRect();
      const containerRect = container.getBoundingClientRect();
      elementTop = elementRect.top - containerRect.top + container.scrollTop;
      elementBottom = elementTop + element.offsetHeight;
    } else {
      // 窗口位置计算
      const elementRect = element.getBoundingClientRect();
      elementTop = elementRect.top + window.scrollY;
      elementBottom = elementRect.bottom + window.scrollY;
    }

    // 判断当前滚动位置是否在元素可视范围内
    if (scrollPosition >= elementTop && scrollPosition < elementBottom) {
      // 节流处理防止频繁更新
      if (activeCategory.value !== category.categoryName) {
        activeCategory.value = category.categoryName;
      }
      break;
    }
  }
};

// 获取分类数据
const fetchCategories = async () => {
  if (categories.length !== 0) {
    categories.splice(0, categories.length)
  }

  try {
    loading.value = true
    const res = await getArticleListFromCategoryService()
    for (const data of res.data.data) {
      categories.push({
        id: data.id,
        categoryName: data.categoryName,
        posts: data.simpleArticleList
      })
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchCategories();

  // 根据容器类型添加滚动监听
  const scrollElement = layoutRef?.value || window;
  scrollElement.addEventListener('scroll', handleScroll);

  // 初始化检测一次
  handleScroll();
})

onBeforeUnmount(() => {
  const scrollElement = layoutRef?.value || window;
  scrollElement.removeEventListener('scroll', handleScroll);

})
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/variables.scss' as va;

.category-group {
  opacity: 1 !important; // 强制显示初始状态
  transform: none !important;

  // 动画执行前保持隐藏
  &.gsap-animating {
    opacity: 0;
    transform: translateY(50px);
  }
}

.posts-list {
  position: relative;
  z-index: 1;
}


.categories-nav {
  position: sticky;
  top: 80px;
  z-index: 10;
  background: var(--grey-1);
  margin: -2rem;
  margin-bottom: 2rem;
  padding: 1rem 2rem;
  border-radius: va.$border-radius-lg;
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;

  &::-webkit-scrollbar {
    display: none;
  }

  .category-tab {
    padding: va.$spacing-xs va.$spacing-md;
    border-radius: va.$border-radius-lg;
    background: var(--hover-bg);
    color: var(--grey-8);
    transition: all 0.3s ease;
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: va.$spacing-xs;
    font-size: 0.9em;

    &:hover {
      transform: translateY(-2px);
    }

    &.active {
      background: va.$primary;
      color: var(--grey-8);
    }
  }
}

.category-group {
  margin-bottom: 3rem;
  scroll-margin-top: 100px;

  &:last-child {
    margin-bottom: 0;
  }
}

.post-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 1rem;
  margin: 1rem 0;
  transition: all 0.3s ease;
  color: var(--grey-8);

  &:hover {
    transform: translateX(10px);
    background: var(--hover-bg);
    color: var(--color-blue);
  }

  .post-date {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: va.$spacing-xs;
    border: 1px solid var(--grey-8);
    border-radius: va.$border-radius-sm;
    transition: all 0.3s ease;
    min-width: 60px;

    .month {
      font-size: 0.9em;
      color: var(--grey-8);
    }

    .day {
      font-size: 1.2em;
      font-weight: 600;
      color: var(--grey-8);
    }

    .post-info {
      flex: 1;
      min-width: 0;

    }
  }
}

.loading-state {
  text-align: center;
  padding: 2rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .categories-page {
    padding: 1rem;
  }

  .content-card {
    padding: 1rem;
    border-radius: 0;
  }

  .categories-nav {
    top: 64px;
    margin: -1rem;
    margin-bottom: 1rem;
    padding: 0.5rem 1rem;
  }
}

.category-name {
  display: flex;
  align-items: center;
  /* 垂直居中 */
  gap: 8px;
  /* 元素间距 */
  color: var(--color-blue);
}

.post-count {
  margin-left: auto;
  /* 右对齐关键属性 */
  font-size: 0.8em;
  /* 缩小字体 */
  opacity: 0.8;
  /* 降低透明度 */
  font-weight: normal;
  /* 常规字重 */
  color: va.$text-primary;
}
</style>
