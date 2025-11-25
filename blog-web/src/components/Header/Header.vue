<template>
  <div class="site-header" :class="{ 'flutter': isScrolled }">
    <div class="navbar">
      <!-- 移动端菜单按钮 -->
      <button class="menu-btn" @click="handleOpenMobileMenu">
        <SvgIcon name="icon-list" size="32" />
      </button>
      <div class="nav-left">
        <div @click="router.push('/')" class="logo pointer" v-pio="{ text: `回到首页` }">
          <img :src="blogInfo.logo">
          <span class="logo-text">{{ t('blogName') }}</span>
        </div>
      </div>
      <div class="nav-center">
        <NavMenu />
      </div>
      <div class="nav-right">
        <NavRight v-on:search="handleOpenSearch" />
      </div>
    </div>
  </div>

  <div class="mobile-menu-warp" :class="{ 'is-show-menu': isMobi }">
    <MobilleMenu @close-menu="handleOpenMobileMenu"></MobilleMenu>
  </div>

  <div class="search-warp" :class="{ 'is-show-search': isShowSearch }">
    <Search @close="handleCloseSearch" :show="isShowSearch"></Search>
  </div>

</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBlogStore } from '@/stores'
import Search from '@/components/Search/Search.vue'
import { t } from '@/utils/i18n'
import NavMenu from './NavMenu.vue'
import MobilleMenu from './MobilleMenu.vue'
import NavRight from './NavRight.vue'


const router = useRouter()
const blogStore = useBlogStore()
const blogInfo = blogStore.blogInfo
// 响应式数据
const isScrolled = ref(false)
const isShowSearch = ref(false)
const isMobi = ref(false)


const handleOpenMobileMenu = () => {
  isMobi.value = !isMobi.value
}


const handleCloseSearch = () => {
  isShowSearch.value = false
}

const handleOpenSearch = () => {
  isShowSearch.value = true
}

const SCROLL_THRESHOLD = 200 // 滚动阈值
const handleScroll = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop || document.body.scrollTop
  isScrolled.value = scrollTop > SCROLL_THRESHOLD
}


// 生命周期
onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

//  ==========  工具类  ==========

%glass {
  background: var(--glass-bg);
  backdrop-filter: blur(10px) saturate(180%);
  -webkit-backdrop-filter: blur(10px) saturate(180%);
}

/*  ==========  header  ==========  */
.site-header {
  position: fixed;
  inset: 0 0 auto;
  z-index: $z-nav-header;
  height: 70px;
  padding: 0 30px;
  border: 2px solid transparent;
  border-color: var(--grey-9-a5);
  @extend %glass;
  transition:
    transform $duration-slow ease,
    border-radius $duration-slow ease,
    border-color $duration-slow ease;
  will-change: transform;
  transform: translateY(20px) scale(0.95);
  border-radius: $border-radius-md;

}

/*  滚动后的“卡片”状态  */
.site-header.flutter {
  transform: translateY(0) scale(1);
  border-radius: 0;
  border: 2px solid transparent;
  border-top-color: var(--glass-bg);
  border-left-color: var(--glass-bg);
  border-right-color: var(--glass-bg);
  border-bottom-color: var(--grey-9-a5);
}

.navbar {
  padding: $spacing-sm 0 $spacing-sm 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-btn {
  visibility: hidden;
  padding: $spacing-sm;
  color: var(--color-black);
  transition: color 0.3s ease;
}

.nav-left {
  margin-right: 48px;
}

.logo {
  display: inline-flex;
  align-items: center;
  gap: $spacing-sm;

  [theme='dark'] .logo img {
    filter: brightness(1) !important;
  }

  img {
    height: 40px;
    width: 40px;
    border-radius: 4px;
  }

  .logo-text {
    font-size: 1.2em;
    font-weight: 700;
    background: linear-gradient(135deg, $primary, $secondary);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    max-width: 200px;
    white-space: nowrap;
  }

}

.nav-center {
  display: flex;
  gap: $spacing-md;
  flex: 1;
}

.nav-right {
  display: flex;
  align-items: center;
  margin-left: auto;
}

@media (max-width: $bp-lg) {
  .menu-btn {
    visibility: visible;
  }

  .nav-left {

    margin-left: 50px;
  }

  .nav-center {
    display: none;
  }

}

@media (max-width: $bp-md) {
  .navbar {
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 0;
  }

  .nav-left {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    margin: 0;
    width: auto;
  }

  .logo .logo-text {
    font-size: 1.5em;
  }
}

.mobile-menu-warp,
.search-warp {
  position: fixed;
  transition: all $duration-slow ease;
  will-change: transform;
}

.search-warp {
  inset: 0;
  z-index: $z-modal-search;
  background-color: transparent;
  transform: translateY(-150%);
}

.is-show-search {
  transform: translateY(0) !important;
}

.mobile-menu-warp {
  width: 320px;
  height: 100vh;
  right: 0;
  z-index: $z-modal-drawer;
  background-color: rgba(0, 0, 0, 0.5);
  transform: translateX(100%);
}

.is-show-menu {
  transform: translateX(0) !important;
}
</style>
