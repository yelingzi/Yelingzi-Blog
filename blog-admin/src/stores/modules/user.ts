import { injectDynamicRoutes } from '@/router'
import type { MenuList } from '@/type/user'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface UserInfo {
  userId: number
  email: string
  nickname: string
  userAvatar: string
}

export const useUserStore = defineStore(
  'userStore',
  () => {
    // 使用独立 ref 替代嵌套的 reactive 对象
    const userInfo = ref<UserInfo>({
      userId: 0,
      email: '',
      nickname: '',
      userAvatar: ''
    })
    const isLogin = ref(false)
    const menuList = ref<MenuList[]>([])
    const accessToken = ref('')
    const refreshToken = ref('')
    const deviceId = ref('')
    

    // 操作方法
    const removeToken = () => {
      accessToken.value = ''
      refreshToken.value = ''
    }
    const setTokens = (at: string, rt: string) => {
      accessToken.value = at
      refreshToken.value = rt
    }

    const removeUserState = () => {
      userInfo.value = { userId: 0, email: '', nickname: '', userAvatar: '' }
      isLogin.value = false
    }

    const setUserState = (user: UserInfo & { login: boolean }) => {
      userInfo.value = { ...user }
      isLogin.value = user.login
    }

    const setMenuList = (menu: MenuList[]) => {
      menuList.value = menu
    }

    const clearMenuList = () => {
      menuList.value = []
    }

    const addRoute = () =>{
      if(isLogin.value && userInfo.value && menuList.value){
        injectDynamicRoutes(menuList.value)
      }
    }

    return {
      // 状态
      userInfo,
      isLogin,
      menuList,
      accessToken,
      refreshToken,
      deviceId,

      // 方法
      removeToken,
      setTokens,
      removeUserState,
      setUserState,
      setMenuList,
      clearMenuList,
      addRoute
    }
  },
  {
    persist: true
  }
)