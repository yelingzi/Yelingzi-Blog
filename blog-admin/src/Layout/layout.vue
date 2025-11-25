<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="layout-aside">
      <Menu :menu-data="menuList" :logo="logo" class="aside-menu" />
    </el-aside>
    
    <el-container class="layout-main-wrapper">
      <el-header class="layout-header">
        <Header></Header>
        <Tabs class="header-tabs" />
      </el-header>
      
      <el-main class="layout-main">
        <div class="main-content">
          <RouterView />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts" setup>
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import { useChatStore, useUserStore } from '@/stores'
import Menu from '@/components/menu/menu.vue'
import Header from '@/components/header/header.vue'
import Tabs from '@/components/header/Tabs.vue'
import logo from '@/assets/images/logo.jpg'
import type { WsMsg } from '@/type/chatType'
import { addWsMessageHandler, closeWs, openWs, removeWsMessageHandler } from '@/utils/websocket'

const userStore = useUserStore()
const chatStore = useChatStore()
const menuList = computed(() => userStore.menuList)
const route = useRoute()
const id = computed(() => route.params.chatId as string)

/* WS 收到消息后统一入口 */
function handleWsMessage(msg: { data: WsMsg }) {
  const { receiver, message } = msg.data

  if(id.value === receiver) {
    /* ---- 当前会话 ---- */
    return
  }

  /* ---- 非当前会话 ---- */
  let target = chatStore.chatList.find(c => c.id === receiver)
  if (!target) {
    target = chatStore.addChatByWsMsg(msg.data)
  }

  const lastMsg = target.chatMessageList.at(-1)
  // 末尾消息与旧 lastMessage 完全相等（id 相同即可）
  const needUpdateList = lastMsg && lastMsg.id === target.lastMessage.id

  if (needUpdateList) {
    target.chatMessageList.push(message)
  }
  target.lastMessage = message
  target.unReadChat++
}

/* WS 收到消息后统一入口 */
function handleWsNotification(msg: { data: WsMsg }) {
  console.log('收到通知消息', msg.data)
}

/* ---------------- 生命周期 ---------------- */
onMounted(() => { 
  openWs()
  addWsMessageHandler('chat', handleWsMessage)
  addWsMessageHandler('notification', handleWsNotification)
})

onUnmounted(() => {
  removeWsMessageHandler('chat', handleWsMessage)
  addWsMessageHandler('notification', handleWsNotification)

  closeWs()
})

</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background-color: #545c64;
  overflow: hidden;
}

.aside-menu {
  height: 100%;
  overflow-y: auto;
}

.layout-main-wrapper {
  flex: 1;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  padding: 0;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  height: auto;
  flex-shrink: 0;
}

.header-tabs {
  border-top: 1px solid #f0f2f5;
}

.layout-main {
  flex: 1;
  padding: 0;
  overflow: hidden;
  background-color: #f0f2f5;
}

.main-content {
  height: 100%;
  padding: 16px;
  overflow-y: auto;
  box-sizing: border-box;
}
</style>