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
      avatar: avatar,
    });

    // 用户状态
    const userState = ref({
      lastShowWeb: '',
      isLogin: false,
    });
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


    const removeToken = () => {
      accessToken.value = ''
      refreshToken.value = ''
    }
    const setTokens = (at: string, rt: string) => {
      accessToken.value = at
      refreshToken.value = rt
    }

    // 移除用户状态
    const removeUserState = () => {
      userInfo.value.id = -1;
      userInfo.value.nickname = '游客' + deviceId.value;
      userInfo.value.avatar = avatar;
      userState.value.isLogin = false;
    };

    // 设置用户状态
    const setUserState = (userData: {
      id: number;
      nickname: string;
      avatar: string;
      login: boolean;
    }) => {
      userInfo.value.id = userData.id;
      userInfo.value.nickname = userData.nickname;
      userInfo.value.avatar = userData.avatar;
      userState.value.isLogin = userData.login;
    };

    // 获取用户状态
    const getUserState = () => {
      return {
        id: userInfo.value.id,
        nickname: userInfo.value.nickname,
        avatar: userInfo.value.avatar,
        email: userInfo.value.email
      };
    };

    // 获取是否登录
    const getIsLogin = () => {
      return userState.value.isLogin;
    };

    // 设置最后显示的网页
    const setLastShowWeb = (url: string) => {
      userState.value.lastShowWeb = url;
    };

    // 获取最后显示的网页
    const getLastShowWeb = () => {
      return userState.value.lastShowWeb;
    };

    // 获取用户状态（组合）
    const getUserStateCombined = computed(() => {
      return {
        ...userInfo,
        ...userState.value,
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

    return {
      removeToken,
      removeUserState,
      setUserState,
      getUserState,
      getIsLogin,
      setLastShowWeb,
      getLastShowWeb,
      setTokens,
      setViewDate,
      checkIsFirstView,

      webSiteInfo,
      userState,
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
