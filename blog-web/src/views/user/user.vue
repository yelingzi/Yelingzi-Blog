<template>
  <div class="user-layout">
    <el-container class="container">
      <!-- 左侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <el-menu :default-active="$route.path" router class="el-menu">
          <h3 class="sidebar-title">个人信息</h3>
          <el-menu-item index="/user/info">
            <span>修改信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-container class="main-content">
        <el-main class="content">
          <RouterView />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores';

const userState = useUserStore()
const router = useRouter();
const checkLogin = () => {
  if (!userState.getIsLogin()) {
    router.push('/home')
  }
}

onMounted(() => {
  checkLogin()
});
</script>

<style lang="scss" scoped>
.user-layout {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: linear-gradient(to top, #fad0c4 0%, #ffd1ff 100%);
}

.container {
  width: 100%;
  max-width: 1200px;
  /* 限制最大宽度 */
  background-color: var(--grey-1);
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.sidebar {
  background-color: var(--grey-1);
  border-right: 1px solid #e6e6e6;
  border-radius: 10px 0 0 10px;

  .sidebar-title {
    padding: 16px;
    font-size: 18px;
    font-weight: 600;
    color: var(--grey-8);
    text-align: center;
    margin: 0;
    background-color: var(--grey-1);
  }

  .el-menu {
    background-color: var(--grey-1);
    border-right: none;
  }
}

.main-content {
  flex: 1;
}

.content {
  padding: 24px;
  min-height: 600px;
  border-radius: 30px;
}
</style>
