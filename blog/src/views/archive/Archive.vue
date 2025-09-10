<template>
  <CommonLayout title="归档" :bgImg="bgImg" />
  <div class="bg">
    <div class="page-container">
      <div class="timeline">
        <div v-for="(item, index) in archives" :key="index" class="year-group" v-animate-on-scroll>
          <div class="year-header pointe" @click="toggleYear(item.year)">
            <span class="year">{{ item.year }}</span>
            <span class="toggle-icon" :class="{ 'is-open': !collapsedYears[item.year] }">
              <SvgIcon name="icon-xiala" size="24" />
            </span>
          </div>
          <Transition name="expand" @enter="startTransition" @leave="endTransition">
            <div v-if="!collapsedYears[item.year]" class="posts-list">
              <div v-for="post in item.posts" :key="post.id" class="post-item" @click="goToPost(post.id)">
                <div class="post-date">
                  <span class="month">{{ getMonth(post.createTime) }}</span>
                  <span class="day">{{ getDate(post.createTime) }}</span>
                </div>
                <div class="post-info" v-pio="{ text: `${post.title}`, type: 'read' }">
                  <h3 class="post-title">{{ post.title }}</h3>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'
import ScrollTrigger from 'gsap/ScrollTrigger'
import CommonLayout from '../Layout/CommonLayout.vue'
import bgImg from '@/assets/images/bg-article.jpg'
import { getDate, getMonth } from '@/utils/common'
import { getArticleListService } from '@/api/article'
import type { Archives } from '@/types/article'
gsap.registerPlugin(ScrollTrigger)



interface ArchivesItem {
  year: number
  posts: Archives[]
}

const router = useRouter()
const archives = ref<ArchivesItem[]>([])
const collapsedYears = reactive<Record<number, boolean>>({})

const goToPost = (id: number) => {
  window.open(`/article/${id}`, '_blank');
}

const toggleYear = (year: number) => {
  collapsedYears[year] = !collapsedYears[year]
}

const startTransition = (element: Element) => {
  const el = element as HTMLElement
  el.style.height = 'auto'
  const height = el.scrollHeight
  el.style.height = '0px'
  el.offsetHeight // Trigger reflow
  el.style.height = `${height}px`
}

const endTransition = (element: Element) => {
  const el = element as HTMLElement
  el.style.height = `${el.scrollHeight}px`
  el.offsetHeight // Trigger reflow
  el.style.height = '0px'
}

const getArchives = async () => {
  try {
    const res = await getArticleListService()
    const temp = reactive<Archives[]>([])

    // 首先将数据按年份分组
    const yearMap = new Map<number, Archives[]>()
    for (const article of res.data.data) {
      const year = new Date(article.createTime).getFullYear()
      if (!yearMap.has(year)) {
        yearMap.set(year, [])
      }
      yearMap.get(year)?.push(article)
    }

    yearMap.forEach((posts, year) => {
      archives.value.push({
        year,
        posts
      })
    })

    archives.value.forEach(item => {
      collapsedYears[item.year] = false
    })

    // GSAP 动画初始化
    gsap.utils.toArray('.year-group').forEach((element: any) => {
      gsap.from(element, {
        scrollTrigger: {
          trigger: element,
          start: 'top center+=100'
        },
        opacity: 0,
        y: 50,
        duration: 0.8
      })
    })
  } catch (error) {
    console.error('Failed to load archives:', error)
  }
}

onMounted(async () => {
  getArchives()
})
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/variables.scss' as va;

.timeline {
  position: relative;
  padding-left: va.$spacing-xl * 2;
  margin-top: va.$spacing-lg;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 2px;
    background: linear-gradient(to bottom,
        va.$primary,
        va.$secondary );

  }
}

.year-group {
  margin-bottom: va.$spacing-xl * 2;

  &:last-child {
    margin-bottom: 0;
  }
}

.year-header {
  margin-bottom: va.$spacing-xl;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: va.$spacing-md;

  &::before {
    content: '';
    position: absolute;
    left: -(va.$spacing-xl * 2);
    top: 50%;
    width: va.$spacing-lg;
    height: 2px;
    background: va.$primary;
  }

  .year {
    font-size: 1.8em;
    font-weight: 700;
    color: var(--grey-8);
  }

  .toggle-icon {
    transition: transform 0.3s ease;

    &.is-open {
      transform: rotate(-180deg);
    }
  }

  &:hover {
    .toggle-icon i {
      color: var(--grey-8);
    }
  }
}

.post-item {
  display: flex;
  align-items: center;
  gap: va.$spacing-xl;
  padding: va.$spacing-md;
  border-radius: va.$border-radius-md;
  transition: all 0.3s ease;
  color: var(--grey-8);

  &:hover {
    background: var(--hover-bg);
    transform: translateX(10px);
    color: va.$primary;

    .post-date {
      border-color: va.$primary;
      background: rgba(va.$primary, 0.1);
    }
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
  }

  .post-info {
    flex: 1;
    min-width: 0;

    .post-title {

      font-size: 1em;
      margin-bottom: 0;
      transition: color 0.3s ease;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

    }
  }
}


.posts-list {
  overflow: hidden;
  transition: height 0.3s ease-in-out;
}
</style>
