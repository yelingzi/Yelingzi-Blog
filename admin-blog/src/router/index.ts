// router/index.ts
import { useUserStore } from '@/stores';
import type { MenuList } from '@/type/user';
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/layout',
      name: 'layout',
      component: () => import('@/Layout/layout.vue'),
      children: [] 
    },
    {
      path: '/',
      component: () => import('@/Layout/check.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/login.vue')
    },
    {
      path: "/404",
      component: () => import("@/views/error/notFound.vue"),
    },
    {
      path: "/error",
      component: () => import("@/views/error/error.vue"),
    },
    { path: "/:catchAll(.*)", redirect: "/404" },
  ],
})

let isRoutesInjected = false

export const injectDynamicRoutes = (menuList: MenuList[]) => {
  if (isRoutesInjected ) return

  // 清理旧路由
  const layout = router.getRoutes().find(r => r.name === 'layout')
  layout?.children?.forEach(child => {
    if (child.meta?.isDynamic) {
      router.removeRoute(child.name!)
    }
  })

  // 递归处理菜单
  const processMenu = (menus: MenuList[]) => {
    menus.forEach(menu => {
      if (menu.children?.length) processMenu(menu.children)
      
      // 过滤有效路由项
      if (menu.menuType !== 2 || !menu.path || !menu.component) return

      // 创建唯一路由名称
      const routeName = `dynamic_${menu.id}`
      
      // 配置路由记录
      const route: RouteRecordRaw = {
        path: menu.path,
        name: routeName,
        component: resolveComponent(menu.component),
        meta: {
          menuId: menu.id,
          isDynamic: true
        },
        props: menu.path == '/chat/:chatId' ? true : false
      }

      // 防止重复添加
      if (!router.hasRoute(routeName)) {
        router.addRoute('layout', route)
      }
    })
  }

  processMenu(menuList)

  isRoutesInjected = true
}

// 增强版组件解析方法
const resolveComponent = (component: string) => {
  // 统一路径格式
  const normalized = component
    .replace(/^\/+/, '')
    .replace(/\/+$/, '')
    .replace(/\.vue$/, '')

  // 尝试多种文件组织方式
  const pathVariants = [
    `/src/views/${normalized}.vue`,
    `/src/views/${normalized}/index.vue`,
    `/src/views/${normalized}View.vue`
  ]

  // 匹配模块
  const modules = import.meta.glob('@/views/**/*.vue')
  for (const path of pathVariants) {
    if (modules[path]) {
      return modules[path]
    }
  }

  console.error('❌ 组件解析失败，原始路径:', component)
  return () => import('@/views/error/notFound.vue')
}

//路由守卫
router.beforeEach(async (to) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (userStore.isLogin && !isRoutesInjected) {
    injectDynamicRoutes(userStore.menuList)
    isRoutesInjected = true 
  }

  if(userStore.isLogin && to.path == '/login'){
    return { path: '/' }   
  }

})

export default router
