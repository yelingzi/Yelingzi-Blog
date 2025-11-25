import { defineStore } from 'pinia'
import avatar from '@/assets/images/default.png'
import { computed, reactive, ref } from 'vue'

export const useUserStore = defineStore(
  'userStore',
  () => {
    // 用户信息
    const userInfo = ref({
      id: -1,
      email: '123456@123.com',
      nickname: '游客',
      userAvatar: avatar,
    });

    // 用户状态
    const lastShowWeb = ref('')
    const accessToken = ref('')
    const refreshToken = ref('')

    // 网站信息
    const webSiteInfo = ref({
      showList: [],
    });

    //访问信息
    const viewDate = ref()
    const isFirstView = ref(true)
    const deviceId = ref('')

    //文章主题
    const previewTheme = ref('')


    const removeToken = () => {
      accessToken.value = ''
      refreshToken.value = ''
      removeUserState()
    }
    const setTokens = (at: string, rt: string) => {
      accessToken.value = at
      refreshToken.value = rt
    }

    // 移除用户状态
    const removeUserState = () => {
      userInfo.value.id = -1;
      userInfo.value.nickname = '游客' + deviceId.value;
      userInfo.value.userAvatar = avatar;
    };

    // 设置用户状态
    const setUserState = (userData: {
      id: number;
      nickname: string;
      userAvatar: string;
    }) => {
      userInfo.value.id = userData.id;
      userInfo.value.nickname = userData.nickname;
      userInfo.value.userAvatar = userData.userAvatar;
      viewDate.value = undefined
    };


    // 获取是否登录
    const getIsLogin = () => {
      return accessToken.value != '' ? true : false;
    };

    // 设置最后显示的网页
    const setLastShowWeb = (url: string) => {
      lastShowWeb.value = url;
    };

    // 获取最后显示的网页
    const getLastShowWeb = () => {
      return lastShowWeb.value;
    };

    // 获取用户状态（组合）
    const getUserStateCombined = computed(() => {
      return {
        ...userInfo,
        ...lastShowWeb,
      };
    });

    const checkIsFirstView = () => {
      if (!isFirstView.value && deviceId.value) {
        return
      }

      // 只有在没有 deviceId 时才生成新的
      if (!deviceId.value) {
        deviceId.value = crypto.randomUUID().replace(/-/g, '').substring(0, 14)
        userInfo.value.nickname = '游客' + deviceId.value
      }
      isFirstView.value = false
    }

    const setViewDate = (date: Date | string) => {
      viewDate.value = date
    }

    const setPreviewTheme = (theme: string) => {
      previewTheme.value = theme
    }

    return {
      removeToken,
      removeUserState,
      setUserState,
      getIsLogin,
      setLastShowWeb,
      getLastShowWeb,
      setTokens,
      setViewDate,
      checkIsFirstView,
      setPreviewTheme,
      previewTheme,
      webSiteInfo,
      lastShowWeb,
      getUserStateCombined,
      userInfo,
      viewDate,
      deviceId,
      accessToken,
      refreshToken
    };
  },
  {
    persist: true,
  },
);
