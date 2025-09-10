import { createRouter, createWebHistory } from 'vue-router'

import Layout from '@/views/Layout/Layout.vue'
import FixedLayout from '@/views/Layout/FixedLayout.vue'
import Home from '@/views/home/Home.vue'
import TalkList from '@/views/Talk/TalkList.vue'
import Archive from '@/views/archive/Archive.vue'

/**
 *  @description: 路由对象参数说明
 *  path: 路由路径，必填
 *  name: 路由名称，必填
 *  meta: 路由元信息，必填
 *  meta.title: 路由标题，必填
 *  meta.img: 路由图片，非必填 优先级 1
 *  meta.textIcon: 路由文字图标，非必填 优先级 2
 *  meta.icon: 路由图标，非必填 优先级 3
 *  meta.isHidden: 是否隐藏路由，非必填 默认 false
 *  component: 路由组件，必填
 *  redirect: 重定向，非必填
 *  children: 子路由，非必填
 */
const route = [
  {
    path: '',
    name: 'home',
    meta: {
      title: '首页',
    },
    component: Home,
  },
  {
    path: '/article/:id',
    name: 'article',
    component: () => import('@/views/article/Article.vue'),
    meta: {
      title: '文章',
    },
    props: true,
  },
  {
    name: 'talks',
    path: '/talks',
    component: TalkList,
    meta: {
      title: '说说列表',
    },
  },
  {
    name: 'talk',
    path: '/talk/:id',
    component: () => import('@/views/Talk/Talk.vue'),
    meta: {
      title: '说说',
    },
    props: true,
  },
  {
    name: 'album',
    path: '/album',
    component: () => import('@/views/album/album.vue'),
    meta: {
      title: '相册',
    },
  },
  {
    name: 'albumById',
    path: '/albums/:id',
    component: () => import('@/views/album/photos.vue'),
    meta: {
      title: '相册',
    },
    props: true,
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('@/views/Login/Login.vue'),
    meta: {
      title: '登录',
    },
  },
  {
    name: 'forget',
    path: '/login/forget',
    component: () => import('@/views/Login/Forget.vue'),
    meta: {
      title: '忘记密码',
    },
  },
  {
    path: '/archive',
    name: 'Archive',
    component: Archive,
    meta: {
      title: '归档',
    },
  },
  {
    name: 'category',
    path: '/category',
    component: () => import('@/views/Category/Category.vue'),
  },
  {
    name: 'tag',
    path: '/tag',
    component: () => import('@/views/Tag/TagCloud.vue'),
    props: true,
  },
  {
    path: '/friend',
    name: 'Friend',
    component: () => import('@/views/friend/Friend.vue'),
    meta: {
      title: '友链',
    },
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/about/About.vue'),
    meta: {
      title: '关于',
    },
  },

  {
    path: '/user',
    name: 'user',
    component: () => import('@/views/user/user.vue'),
    redirect: '/user/info',
    children: [
      {
        path: '/user/info',
        name: 'userInfo',
        component: () => import('@/views/user/userInfo.vue'),
        meta: {
          title: '个人信息',
        },
      },
    ],
  },
  {
    path: '/test',
    component: () => import('@/components/Clock/particleClock.vue')
  }
]

const fixedRoute = [
  {
    path: '/chat/:chatType',
    name: 'chat',
    component: () => import('@/views/chat/ChatLayout.vue'),
    meta: {
      title: '聊天',
    },
    props: true
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/views/message/message.vue'),
    meta: {
      title: '留言',
    },
  },
]

// createRouter 创建路由实例
//配置 history 模式
// 1. history模式  createWebHistory  地址栏不带#
// 2. hash模式  createWebHashHistory  地址栏带#
// vite 中的环境变量 import.meta.env.BASE_URL  就是 vite.config.js 中的 base 配置项
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: Layout,
      children: [...route],
    },
    {
      path: '/fixedlayout',
      name: 'fixedlayout',
      component: FixedLayout,
      children: [...fixedRoute]
    },
    {
      path: "/404",
      component: () => import("@/views/error/error404.vue"),
    },
    { path: "/:catchAll(.*)", redirect: "/404" },
  ],
})

export default router
