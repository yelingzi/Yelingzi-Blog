<template>
    <div class="center-container">
      <div class="loading">
        <h1>加载中...</h1>
        <el-image class="loading-warp" :src="img" />
      </div>
    </div>
  </template>

<script lang="ts" setup>
import { useUserStore } from "@/stores";
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import img from "@/assets/images/logo.jpg"
import { userInfoService } from "@/api/login";
const userState = useUserStore()
const router = useRouter()
const checkLogin = async () => {
    if (userState.isLogin) {

        try {
            const { data: userRes } = await userInfoService()
            if (userRes.code === 1) {
                userState.setUserState({
                    ...userRes.data,
                    login: true
                })
            }
        } catch (error) {
            // 清除 token 和用户状态
            userState.removeToken()
            userState.removeUserState()
        }
    }

    if (userState.isLogin) {
        router.push('/home')
    } else {
        router.push('/login')
    }
}

onMounted(() => {
    checkLogin()
})
</script>

<style lang="scss" scoped>
.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 使用视口高度确保全屏 */
  width: 100vw; /* 使用视口宽度确保全屏 */
  margin: 0;
  padding: 0;
}

.loading {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  width: 350px;
  max-width: 100%;
  min-height: 300px;
  
  h1 {
    margin-bottom: 20px; 
    width: 100%;
  }
  
  .loading-warp {
    max-width: 100%; 
  }
}
</style>
