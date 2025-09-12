<template>
  <div class="layout-warp">
    <!-- 滚动条 -->
    <ProgressBar :disabledSmooth="disabledSmooth" @update:disabledSmooth="updateDisabledSmooth"></ProgressBar>

    <div class="layout-content-warp" id="layout" ref="layoutRef" :class="{ smooth: disabledSmooth }">
      <!-- 头部菜单 -->
      <MyHeader></MyHeader>
      <!-- main -->
      <main style="min-height: 100vh" id="main">
        <router-view></router-view>
      </main>
      <MyFooter></MyFooter>
    </div>
    <!-- 尾部 -->

  </div>
</template>

<script lang="ts" setup>
import { dayjs } from 'element-plus'
import { onMounted, provide, ref, watch } from 'vue';
import MyHeader from '@/components/Layout/Header/Header.vue';
import MyFooter from '@/components/Layout/Footer/Footer.vue'
import ProgressBar from '@/components/Layout/Progress/ProgressBar.vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '@/stores';
import { userInfoService, userViewService, viewService } from '@/api/login';

const userState = useUserStore()
const layoutRef = ref<HTMLElement | null>(null)
const disabledSmooth = ref(true)
const route = useRoute()

// 向子组件提供滚动容器引用
provide('scrollContainer', layoutRef)


// 监听路由变化
watch(
  () => route.path,
  () => {
    if (layoutRef.value) {
      // 立即滚动到顶部
      layoutRef.value.style.scrollBehavior = disabledSmooth.value ? 'auto' : 'smooth'
      layoutRef.value.scrollTop = 0

      // 重置滚动行为（如果需要保持全局设置）
      setTimeout(() => {
        if (layoutRef.value) {
          layoutRef.value.style.scrollBehavior = ''
        }
      }, 100)
    }
  }
)

// 今天 YYYY-MM-DD
const todayStr = () => dayjs().format('YYYY-MM-DD')

// 是否今天已上报过
const isReportedToday = ref(
  userState.viewDate === todayStr()
)

// 发送访问请求（只会在今天首次调用时真正执行）
const handleDailyFirstView = async () => {
  if (isReportedToday.value) return

  // 真正上报
  if (userState.getIsLogin()) {
    await userViewService()
  } else {
    await viewService()
  }

  // 标记今天已上报
  userState.setViewDate(todayStr())
  isReportedToday.value = true
}

onMounted(() => {
  checkLogin()
  handleDailyFirstView()
  userState.checkIsFirstView()
});

let avatarWidth = ref('60px');

// 定义一个方法，用于更新 disabledSmooth
const updateDisabledSmooth = (value: boolean) => {
  disabledSmooth.value = value;
};

const checkLogin = async () => {
  if (userState.getIsLogin()) {
    const { data: userRes } = await userInfoService()
    if (userRes.code === 1) {
      userState.setUserState({
        ...userRes.data,
        login: true
      })
    }
  }
}



</script>

<style lang="scss" scoped>
.layout-warp {
  overflow: hidden;
  position: relative;


  @keyframes animation {
    0% {
      background-position: 0% 50%;
    }

    50% {
      background-position: 100% 50%;
    }

    100% {
      background-position: 0% 50%;
    }
  }


  .smooth {
    scroll-behavior: smooth; // 平滑滚动
  }

  .layout-content-warp {
    user-select: none; // 禁止选中
    position: relative;
    width: 100%;
    height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
    left: 0;
    transition: left 0.8s ease;


    /* WebKit内核浏览器 */
    ::-webkit-scrollbar {
      width: 0px;
      height: 0px;
    }

    /* Firefox浏览器 */
    scrollbar-width: none;
    /* IE和Edge浏览器 */
    -ms-overflow-style: none;

    .header-warp {
      width: 100%;
      height: 60px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 20px;
      background: var(--bg-warp-light-color-2);
      border-bottom: 1px solid var(--theme-light-color-5);
      transition: all 0.8s ease;
      position: fixed;
      left: 0;
      top: 0;
      z-index: 999;
      border-radius: 0px;
      font-weight: 600;

      .title-text {
        font-size: 25px;
        padding: 5px 10px;
        background-color: var(--theme-light-color-9);
        border-radius: 5px;
        color: var(--theme-color);
      }

      .app-title {
        .title-sub-text {
          color: var(--text-color);
        }

        &:hover {
          .title-text {
            color: #fff;
            background-color: var(--theme-light-color);
          }

          .title-sub-text {
            color: var(--theme-color);
          }
        }
      }

      .right-nav-info {
        height: 100%;
        display: flex;
        align-items: center;
        position: relative;

        .menu-warp {
          height: 100%;
          position: absolute;
          right: -616px;
          transition: right 1s ease;
        }

        .menuShow {
          right: v-bind(avatarWidth);
        }

        .secrh-warp {
          display: flex;
          justify-content: center;
          align-items: center;
          width: v-bind(avatarWidth);

          .iconfont {
            font-size: 24px;
            color: var(--text-color);
          }

          &:hover {
            .iconfont {
              color: var(--theme-light-color-3);
            }
          }
        }

        .avatar-warp {
          width: v-bind(avatarWidth);

          img {
            margin-left: 20px;
            width: 40px;
            height: 40px;
            border-radius: 50%;
          }
        }
      }
    }

  }
}
</style>
