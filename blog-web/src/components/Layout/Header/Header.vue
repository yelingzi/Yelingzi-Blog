<template>
  <div class="site-header" :class="{ 'flutter': isScrolled }">
    <div class="navbar">
      <!-- 移动端菜单按钮 -->
      <button class="menu-btn" @click="handleOpenMobileMenu">
        <SvgIcon name="icon-list" size="32" />
      </button>

      <div class="nav-left">
        <div @click="router.push('/')" class="logo pointer">
          <img :src="blogInfo.logo">
          <span class="logo-text">{{ i18n.blogName }}</span>
        </div>
      </div>

      <div class="nav-center">
        <div v-for="item in filteredMenuItems" :key="item.path" class="nav-item" @mouseleave="handleMouseLeave">
          <div @click="handleDropdownItemClick(item)" class="nav-link pointer" :class="{
            'has-dropdown': item.children,
            'active': isActive(item),
            [item.colorClass]: true
          }" @mouseenter="handleMouseEnter(item)">
            <SvgIcon :name="item.icon" class="svg-icon" />
            {{ item.name }}
            <SvgIcon name="icon-xiala" v-if="item.children" class="dropdown-icon svg-icon" />
          </div>

          <div v-if="item.children" class="dropdown-menu" :class="{ active: activeDropdown === item.name }">
            <a href="javascript:void(0)" v-for="child in item.children" :key="child.path" class="dropdown-item"
              :class="{ 'active': isChildActive(child) }" @click="handleDropdownItemClick(child)">
              <SvgIcon :name="child.icon" class="svg-icon" />
              {{ child.name }}
            </a>
          </div>
        </div>
      </div>



      <div class="nav-right">


        <!-- <LanguageSwitcher></LanguageSwitcher> -->

        <div v-if="!isMobi" v-pio="{ text: `这题可以切换语言哦` }" class="i18n" @mouseleave="handleMouseLeaveI18n"
          @mouseenter="handleMouseEnterI18n">

          <component :is="i18nIcon" color="#000" class="i18n-icon pointer" v-if="i18nIcon" />
          <span class="current-lang">
            {{currentLangList.find(lang => lang.id === useI18n.currentLang)?.text}}
          </span>

          <div v-if="currentLangList" class="i18n-menu" :class="{ active: isDropdownOpen }">
            <div v-for="currentLang in currentLangList" :key="currentLang.id" class="i18n-item pointer"
              @click="handleSwitch(currentLang.id)">
              {{ currentLang.text }}
            </div>
          </div>
        </div>

        <el-tooltip v-if="!isMobi" content="切换主题" placement="bottom" effect="light">
          <SvgIcon v-if="blogInfo.isDark" size="32" style="color:#fbc90d;" name="icon-Sun" v-pio="{ text: `这里可以切换主题哦` }"
            class="i18n-icon" @click="handleToggleTheme" />
          <SvgIcon v-else name="icon-Moon-Star" size="32" style="color:#c2d7e9;" v-pio="{ text: `这里可以切换主题哦` }"
            class="i18n-icon" @click="handleToggleTheme" />
        </el-tooltip>

        <!-- <div class="icon-container" @click="openSet">
          <font-awesome-icon icon="fa-solid fa-gear" size="xl" />
        </div> -->
        <a class="search-btn" @click="handleSearch">
          <SvgIcon name="icon-Search" />
          <span v-if="!isMobi" class="search-text">{{ t('search') }}</span>
        </a>

        <!-- 用户信息 -->
        <div v-if="!isMobi" class="user-container" @mouseleave="handleMouseLeaveUser"
          @mouseenter="handleMouseEnterUser">

          <div class="user-info">
            <div v-if="userState.getIsLogin()" class="user-section">
              <div class="avatar" @mouseenter="showDropdown = true">
                <YlAvatar :src="userInfo.avatar"></YlAvatar>
              </div>
              <div class="user-menu" :class="{ active: isDropdownOpenUser }">
                <div class="dropdown-header">
                  <ImageWithFallback class="user-avatar" :src="userInfo.avatar" />
                  <div class="user-details text">
                    <span class="username text">{{ userInfo.nickname }}</span>
                  </div>
                </div>
                <router-link to="/user/info" class="dropdown-item pointer">
                  <SvgIcon name="icon-geren" />
                  {{ i18n.personalCenter }}
                </router-link>
                <div class="dropdown-item pointer" @click="handleLogout">
                  <SvgIcon name="icon-chexiao" />
                  {{ i18n.logOut }}
                </div>
              </div>
            </div>
            <div v-else class="avatar pointer" @click="handleLogin">
              <el-avatar>{{ i18n.login }}</el-avatar>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>



  <div class="search-home-warp" :class="{ 'is-show-search': isShowSearch }">
    <Search @close="handleCloseSearch" :show="isShowSearch"></Search>
  </div>

</template>

<script lang="ts" setup>
import { ElMessage } from 'element-plus'
import { ref, reactive, computed, onMounted, onBeforeUnmount, inject, type Ref, defineAsyncComponent, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { MenuItem } from '@/types/home'
import { useUserStore, useI18nStore, useBlogStore } from '@/stores'
import Search from '@/components/Search/Search.vue'
import { useResize } from '@/utils/common'
import { useChatStore } from '@/stores/modules/chat'
import YlAvatar from '@/components/Image/YlAvatar.vue'
import ImageWithFallback from '@/components/Image/ImageWithFallback.vue'
import LanguageSwitcher from '@/components/Language/LanguageSwitcher.vue'
import { handleI18n } from '@/utils/i18n'
import { useLangChange } from '@/constants/useLangChange'

const i18nIcon = defineAsyncComponent(() =>
  import('@/assets/icons/i18n.svg')
);

const router = useRouter()
const route = useRoute()
const blogStore = useBlogStore()
const blogInfo = blogStore.blogInfo
const useI18n = useI18nStore()
const i18n = useI18n.currentConfig
const userState = useUserStore()
const userInfo = userState.getUserState()
const chatStore = useChatStore()
// 响应式数据
const searchQuery = ref<string>('')
const showMobileSearch = ref(false)
const activeDropdown = ref<string | null>(null)
const showDropdown = ref(false)
const isScrolled = ref(true)
const isDropdownOpen = ref(false);
const hoverTimer = ref(0);
const isShowSearch = ref(false)
const hoverTimerUser = ref(0);
const isDropdownOpenUser = ref(false)
const isMobi = useResize()

const t = (key: string) => {
  return useI18n.currentConfig[key] || key
}
// 定义菜单项
const menuItems = computed<MenuItem[]>(() => {

  return [
    {
      name: t('home'),
      path: '/',
      icon: 'icon-zhuye',
      colorClass: 'home-link'
    },
    {
      name: t('article'),
      path: '/archive',
      icon: 'icon-shu',
      colorClass: 'archive-link',
      children: [
        {
          name: t('archive'),
          path: '/archive',
          icon: 'icon-jishi',
          colorClass: 'clock-link'
        },
        {
          name: t('category'),
          path: '/category',
          icon: 'icon-fenlei',
          colorClass: 'category-link'
        },
        {
          name: t('tag'),
          path: '/tag',
          icon: 'icon-biaoqian',
          colorClass: 'tag-link'
        }
      ]
    },
    {
      name: t('talk'),
      path: '/talks',
      icon: 'icon-xiaoxi',
      colorClass: 'talk-link'
    },
    {
      name: t('album'),
      path: '/album',
      icon: 'icon-zhaopian',
      colorClass: 'photos-link'
    },
    {
      name: t('messageBorad'),
      path: '/message',
      icon: 'icon-xinfeng',
      colorClass: 'message-link',
      children: [
        {
          name: t('linkMe'),
          path: '/chat/linkme',
          icon: 'icon-fasong',
          colorClass: 'about-link-me'
        },
        {
          name: t('chatRoom'),
          path: '/chat/chatroom',
          icon: 'icon-liaotianshi',
          colorClass: 'home-link'
        },
      ]
    },
    {
      name: t('friend'),
      path: '/friend',
      icon: 'icon-tuandui',
      colorClass: 'home-link'
    },
    {
      name: t('aboutBlog'),
      path: '/about',
      icon: 'icon-tishi1',
      colorClass: 'about-link',
      children: [
        {
          name: t('about'),
          path: '/about',
          icon: 'icon-geren',
          colorClass: 'about-me'
        },
        {
          name: t('backManagement'),
          path: 'https://blog.yeling.top/',
          icon: 'icon-diannao',
          colorClass: 'admin-link',
          external: true
        },
        {
          name: t('openSource'),
          path: 'https://github.com/yelingzi/Yelingzi-Blog',
          icon: 'icon-github',
          colorClass: 'admin-link',
          external: true
        }
      ]
    }
  ]
})

const currentLangList = ref([
  { id: 'zh-CN', text: '简体中文' },
  { id: 'en-US', text: 'English' }
])

// 计算属性
const filteredMenuItems = computed(() =>
  menuItems.value.map(item => ({
    ...item,
    path:
      item.children && item.children.length && item.path === ''
        ? item.children[0].path
        : item.path
  }))
)

// 方法
const handleOpenMobileMenu = () => {
  // store.commit('SET_MOBILE_MENU_VISIBLE', true)
}

const handleSearch = () => {
  isShowSearch.value = !isShowSearch.value
}
const handleCloseSearch = () => {
  isShowSearch.value = false
}
const handleLogin = () => {
  userState.setLastShowWeb(route.path)
  router.push('/login')
}

const closeAllPanels = () => {
  showMobileSearch.value = false
}

const closeMobileSearch = () => {
  showMobileSearch.value = false
  searchQuery.value = ''
}

const handleMouseEnter = (item: MenuItem) => {
  if (item.children) {
    activeDropdown.value = item.name
  }
}

const handleMouseLeave = () => {
  activeDropdown.value = null
}

const isActive = (item: MenuItem): boolean => {
  if (item.children) {
    return item.children.some(child => isChildActive(child))
  }
  return route.path === item.path
}

const isChildActive = (child: MenuItem): boolean => {
  return route.path === child.path
}

const handleDropdownItemClick = (item: MenuItem) => {
  console.log(item.path)
  activeDropdown.value = null
  if (item.external) {
    const newWindow = window.open(item.path, '_blank')
    newWindow!.opener = null;
    return
  }
  router.push(item.path)
}

const handleLogout = () => {
  userState.removeUserState()
  chatStore.removeChat()
  ElMessage.success('已退出登录')
  showDropdown.value = false
}
// 在子组件中获取引用
const layoutRef = inject<Ref<HTMLElement | null>>('scrollContainer', ref<HTMLElement | null>(null));
const SCROLL_THRESHOLD = 200 // 滚动阈值
const handleScroll = () => {
  if (!layoutRef.value) return

  isScrolled.value = !(layoutRef.value.scrollTop > SCROLL_THRESHOLD)
}

//打开设置
const openSet = () => {

}

const handleToggleTheme = () => {
  blogStore.toggleTheme()
}

const handleMouseLeaveI18n = () => {
  hoverTimer.value = setTimeout(() => {
    isDropdownOpen.value = false;
  }, 300); // 延迟关闭防止鼠标移出时立即消失
}

const handleMouseEnterI18n = () => {
  clearTimeout(hoverTimer.value);
  isDropdownOpen.value = true;
}

const handleMouseLeaveUser = () => {
  hoverTimerUser.value = setTimeout(() => {
    isDropdownOpenUser.value = false;
  }, 300); // 延迟关闭防止鼠标移出时立即消失
}

const handleMouseEnterUser = () => {
  clearTimeout(hoverTimerUser.value);
  isDropdownOpenUser.value = true;
}

const handleSwitch = async (lang: string) => {
  await handleI18n(lang)
}

useLangChange(() => {
  // 当语言变化时，这里会重新执行
  console.log('语言已切换至', useI18n.currentLang)
})

// 生命周期
onMounted(() => {
  if (!layoutRef?.value) return

  layoutRef.value.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
  layoutRef.value?.removeEventListener('scroll', handleScroll)

})
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;
@use '/src/assets/styles/variables.scss' as va;

.site-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
  background: rgba(240, 255, 253, 0.65);
  backdrop-filter: blur(10px) saturate(180%);
  -webkit-backdrop-filter: blur(10px) saturate(180%);
  transition: all 0.8s ease;
  padding: 0 30px;
  margin: 0;
  border: 2px solid var(--color-pink);
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: 2px solid var(--grey-9-a5);
  padding: 0 30px;

}

.flutter {
  margin: 20px 20px 0 20px;
  top: 0;
  position: fixed;
  padding: 0 30px;
  transition: all 0.8s ease !important;
  z-index: 999;
  border-radius: va.$border-radius-md;
  word-break: keep-all;
  border: 2px solid var(--grey-9-a5);
  -webkit-transition: all 0.8s ease !important;
}

.navbar {
  padding: va.$spacing-sm va.$spacing-xl;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  transition: all 0.3s ease;
  position: relative;
}



.nav-link {
  padding: 1rem 1.5rem;
  transition: all 0.3s ease;
}

.nav-left {
  margin-right: 50px;

  .logo {
    display: flex;
    align-items: center;
    text-decoration: none;
    gap: va.$spacing-sm;

    [theme='dark'] .logo img {
      filter: brightness(1) !important;
      /* 重置为正常亮度 */
    }

    img {
      height: 38px;
      width: 40px;
      border-radius: 5px;
    }

    .logo-text {
      font-size: 1.2em;
      font-weight: 700;
      background: linear-gradient(135deg, va.$primary, va.$secondary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      max-width: 200px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    @media screen and (max-width: 1400px) {
      .logo-text {
        max-width: 160px;
        font-size: 1.1em;
      }
    }

    @media screen and (max-width: 1200px) {
      .logo-text {
        max-width: 140px;
        font-size: 1em;
      }
    }

    @media screen and (max-width: 1000px) {
      .logo-text {
        max-width: 120px;
        font-size: 0.9em;
      }
    }
  }
}

.nav-center {
  display: flex;
  gap: va.$spacing-md;
  flex: 1;

  .nav-item {
    position: relative;
    white-space: nowrap;


    &:hover {
      .dropdown-menu {
        opacity: 1;
        visibility: visible;
        transform: translateX(-50%) translateY(0);
        pointer-events: auto;
      }

    }
  }


  .nav-link {
    color: var(--color-black);
    text-decoration: none;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: va.$spacing-sm;
    padding: va.$spacing-sm;
    border-radius: va.$border-radius-md;
    font-size: 16px;
    position: relative;
    height: 40px;
    line-height: normal;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    .svg-icon {
      flex: 0 0 16px; // 固定宽度，不伸缩
      width: 16px;
      height: 16px;
      display: inline-flex;
      align-items: center;
      justify-content: center;

      // 如果仍有微小偏差，可以添加微调
      position: relative;
      top: 0.5px; // 根据实际情况调整
    }

    &:hover,
    &.active {
      color: va.$primary;
      background: var(--hover-bg);
    }

    &.has-dropdown {
      .dropdown-icon {
        transition: transform 0.3s ease;
      }

      &:hover .dropdown-icon {
        transform: rotate(180deg);
      }
    }

    .menu-item {
      padding: 12px 20px;
      display: flex;
      align-items: center;
      line-height: normal;
      gap: 12px;
      color: var(--grey-1);
      text-decoration: none;
      transition: all 0.2s;

      &:hover {
        background: var(--hover-bg);
        color: va.$primary;
      }
    }

  }

  @media screen and (max-width: 1400px) {
    .nav-link {
      font-size: 0.85em;
      padding: va.$spacing-sm va.$spacing-sm;
      gap: va.$spacing-xs;
    }
  }

  @media screen and (max-width: 1200px) {
    gap: va.$spacing-sm;

    .nav-link {
      font-size: 0.8em;
      padding: va.$spacing-xs va.$spacing-sm;

      i {
        font-size: 1em;
      }

      .dropdown-icon {
        display: none;
      }
    }
  }

  @media screen and (max-width: 1000px) {
    .nav-link {
      i {
        display: none;
      }

      padding: va.$spacing-xs va.$spacing-xs;
      font-size: 0.75em;
    }
  }
}

.i18n-icon {
  width: 24px;
  height: 24px;
  color: var(--color-black);
}

.i18n {
  position: relative;
  display: flex;
  align-items: center;
  margin-right: 20px;

  .i18n-icon {
    width: 24px;
    height: 24px;
    color: var(--color-black);
  }

  .i18n-menu {
    position: absolute;
    top: calc(100% + 14px);
    left: 50%;
    transform: translateX(-50%) translateY(5px);
    background: var(--color-white);
    border-radius: va.$border-radius-md;
    box-shadow: va.$shadow-lg;
    width: max-content;
    min-width: 120px;
    opacity: 0;
    visibility: hidden;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
    padding: va.$spacing-xs 0;
  }

  .i18n-menu.active {
    opacity: 1;
    visibility: visible;
    transform: translateX(-50%) translateY(0);
    pointer-events: auto;
  }

  .i18n-item {
    display: flex;
    align-items: center;
    gap: va.$spacing-sm;
    padding: 8px va.$spacing-md;
    height: 36px;
    color: var(--color-black);
    text-decoration: none;
    transition: all 0.3s ease;
    white-space: nowrap;
    font-size: 0.9em;

    &:hover {
      background: var(--hover-bg);
      color: va.$primary;
    }
  }
}

.current-lang {
  color: var(--lightRed);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: va.$spacing-md;
  margin-left: auto;
  position: relative;
  right: 0px;

  .search-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background: var(--hover-bg);
    border-radius: 20px;
    color: var(--color-black);
    text-decoration: none;
    transition: all 0.3s ease;
    border: 1px solid var(--border-color);

    .search-text {
      font-size: 0.9em;
      font-weight: 500;
    }

    &:hover {
      background: var(--color-white);
      color: va.$primary;
      border-color: va.$primary;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(va.$primary, 0.1);

      i {
        transform: scale(1.1);
      }
    }
  }

  .user-container {
    position: relative;
    display: flex;
    align-items: center;
    margin-right: 20px;
  }

  .user-section {
    max-width: 240px;
  }

  .user-info {

    .user-menu {
      position: absolute;
      top: calc(100% + 14px);
      left: 50%;
      transform: translateX(-50%) translateY(5px);
      background: var(--color-white);
      border-radius: va.$border-radius-md;
      box-shadow: va.$shadow-lg;
      width: max-content;
      min-width: 120px;
      opacity: 0;
      visibility: hidden;
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
      pointer-events: none;
      padding: 0;
    }

    .user-menu.active {
      opacity: 1;
      visibility: visible;
      transform: translateX(-62%) translateY(0);
      pointer-events: auto;
    }

    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      transition: transform 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 2px solid va.$primary;

      &:hover {
        transform: scale(1.05);
        background: var(--hover-bg);
      }

    }

    .dropdown-header {
      padding: 20px;
      background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
      color: #fff;
      margin: 0 auto;
      border-radius: va.$border-radius-md va.$border-radius-md 0 0;

      .user-avatar {
        width: 100px;
        height: 100px;
        margin: 0 auto;
      }

      .user-details {
        .username {
          display: block;
          font-weight: 500;
          font-size: 24px;
          color: var(--color-pink);
        }
      }

      &:hover {
        background: var(--hover-bg);
        color: va.$primary;
      }
    }

    .dropdown-divider {
      height: 1px;
      background: var(--border-color);
      margin: 8px 0;
    }

  }
}

.dropdown-item {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.2s;
  line-height: normal;
  font-size: 14px;

  &:hover {
    background: var(--hover-bg);
    color: va.$primary;
  }
}

.menu-btn,
.mobile-search-btn {
  display: none;
  background: none;
  border: none;
  padding: va.$spacing-sm;
  font-size: 1.2em;
  color: va.$text-secondary;
  cursor: pointer;
  transition: color 0.3s ease;

  &:hover {
    color: va.$primary;
  }
}

.mobile-search {
  position: fixed;
  top: 0;
  bottom: 0;
  background: white;
  width: 80%;
  max-width: 360px;
  z-index: 1100;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  overflow-y: auto;

  &.active {
    transform: translateX(0);
  }
}


@include m.responsive(lg) {
  .nav-center {
    display: none;
  }

  .nav-right .search-btn {
    width: 160px;
  }
}

@include m.responsive(md) {
  .navbar {
    padding: va.$spacing-sm va.$spacing-md;
  }

  .menu-btn,
  .mobile-search-btn {
    display: block;
  }

  .nav-center,
  .nav-right .search-btn {
    display: none;
  }

  .nav-left {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    margin-right: 0;

    .logo {
      img {
        height: 40px;
        width: 42px;
      }

      .logo-text {
        max-width: 160px;
        font-size: 1em;
        display: block;
      }
    }
  }

  .nav-right .user-info {
    display: none;
  }
}

@media screen and (max-width: 480px) {
  .nav-left .logo .logo-text {
    max-width: 120px;
    font-size: 1.3em;
  }
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) translateY(15px);
  background: var(--color-white);
  border-radius: va.$border-radius-md;
  box-shadow: va.$shadow-lg;
  width: max-content;
  min-width: 120px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none;
  padding: va.$spacing-xs 0;

  &::before {
    content: '';
    position: absolute;
    top: -8px;
    left: 0;
    width: 100%;
    height: 8px;
    background: transparent;
  }

  .dropdown-item {
    display: flex;
    align-items: center;
    gap: va.$spacing-sm;
    padding: 8px va.$spacing-md;
    height: 36px;
    color: var(--color-black);
    text-decoration: none;
    transition: all 0.3s ease;
    white-space: nowrap;
    font-size: 0.9em;

    &:hover {
      color: va.$primary;
      background: var(--hover-bg);
    }
  }

  &.active {
    opacity: 1;
    visibility: visible;
    transform: translateX(-50%) translateY(0);
    pointer-events: auto;
  }

  @keyframes dropdownEnter {
    from {
      opacity: 0;
      transform: translateX(-50%) translateY(10px);
    }

    to {
      opacity: 1;
      transform: translateX(-50%) translateY(0);
    }
  }

  &.active {
    animation: dropdownEnter 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 添加图标悬浮动画 */
@keyframes iconFloat {
  0% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-3px);
  }

  100% {
    transform: translateY(0);
  }
}

.nav-link:hover i {
  animation: iconFloat 0.6s ease-in-out;
}

/* 修改字体相关样式 */
.nav-link {
  font-family: 'Poppins', sans-serif;
  font-weight: 500;
  letter-spacing: 0.2px;
  /* ... 其他样式保持不变 ... */
}

.logo-text {
  font-family: 'Poppins', sans-serif;
  font-weight: 600;
  letter-spacing: -0.5px;
  /* ... 其他样式保持不变 ... */
}

.icon-container {

  &:hover {
    animation: spin 2s linear infinite;
  }
}

/* 定义旋转动画 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

:deep(.el-dropdown-menu__item) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

.search-home-warp {
  position: fixed;
  width: 100vw;
  height: 100vh;
  left: 0;
  z-index: 999;
  background-color: transparent;
  transition: transform 1s ease;
  transform: translateY(-150vh);
  will-change: transform;
  backface-visibility: hidden;
}

.is-show-search {
  transform: translateY(0) !important;
}
</style>
