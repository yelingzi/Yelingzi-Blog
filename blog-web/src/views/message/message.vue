<template>
  <div class="message">
    <!-- 弹幕列表 30% -->
    <div class="danmaku-container">

      <vue-danmaku ref="danmaku" class="danmaku" use-slot v-model:danmus="messageList" :is-suspend="true">
        <template v-slot:dm="{ danmu }">
          <span class="danmaku-item">
            <el-avatar :src="danmu.userAvatar ? danmu.userAvatar : avatar" :size="30" />
            <span class="ml">{{ danmu.nickname }} :</span>
            <span class="ml">{{ danmu.content }}</span>
          </span>
        </template>
      </vue-danmaku>

    </div>

    <!-- 便签板 -->
    <div class="message-container">
      <Vue3DraggableResizable v-if="initialized" :initX="200" :initY="200" :draggable="true" v-model:x="message.x"
        v-model:y="message.y" :resizable="false" :parent="true" classNameDraggable="draggable"
        v-for="message in messagePaper" :key="message.id" :style="{ visibility: initialized ? 'visible' : 'hidden' }">
        <div class="paper-image">
          <div class="paper-header">
            <el-avatar :src="message.userAvatar ? message.userAvatar : avatar" :size="40" />
            <div class="paper-nickname">{{ message.nickname }}</div>
          </div>
          <el-tooltip v-if="message.content.length > 40" :content="message.content">
            <div class="paper-text">
              {{ message.content }}
            </div>
          </el-tooltip>
          <div v-else class="paper-text">
            {{ message.content }}
          </div>
          <div class="paper-time">{{ formatDate(message.createTime) }}</div>
        </div>
      </Vue3DraggableResizable>
      <div class="message-write">
        <h1 class="message-title">{{ t('messageBorad') }}</h1>
        <div class="message-input">
          <input class="input text" v-pio="{ text: '留下你的想法吧' }" v-model="content" @click="show = true"
            @keyup.enter="send" :placeholder="t('messageInput')" />
          <button class="send" @click="send" v-if="show">{{ t('send') }}</button>
        </div>
      </div>

    </div>
  </div>

</template>

<script setup lang="ts">
import vueDanmaku from "vue3-danmaku";
import { nextTick, onMounted, reactive, ref } from 'vue';
import { ElMessage } from "element-plus";
import avatar from '@/assets/images/avatar1.jpg'
import { formatDate } from "@/utils/common";
import { addMessageService, getMessageListService } from "@/api/message";
import Vue3DraggableResizable from 'vue3-draggable-resizable'
import { useUserStore, useI18nStore } from "@/stores";
import { t } from '@/utils/i18n'

interface Message {
  id: number;
  nickname: string;
  userAvatar: string;
  content: string;
}
interface MessagePaper {
  id: number;
  nickname: string;
  userAvatar: string;
  content: string;
  createTime: string
  x: number,
  y: number
}

const content = ref("");
const show = ref(false)
const danmaku = ref();
const messageList = ref<Message[]>([]);
const messagePaper = ref<MessagePaper[]>([]);
const initialized = ref(false);
const userState = useUserStore()

const getRandomPosition = (containerWidth: number, containerHeight: number, elementWidth: number, elementHeight: number) => {
  const padding = 20; // 添加一些内边距
  const maxX = Math.max(0, containerWidth - elementWidth - padding);
  const maxY = Math.max(0, containerHeight - elementHeight - padding);

  return {
    x: padding + Math.floor(Math.random() * maxX),
    y: padding + Math.floor(Math.random() * maxY)
  };
};

const initializePositions = async () => {
  await nextTick();
  setTimeout(() => {
    const pageContainer = document.querySelector('.message-container')
    if (pageContainer) {
      const containerRect = pageContainer.getBoundingClientRect()

      messagePaper.value.forEach(message => {
        const elementWidth = 200
        const elementHeight = 200
        const { x, y } = getRandomPosition(
          containerRect.width,
          containerRect.height,
          elementWidth,
          elementHeight
        )
        message.x = x
        message.y = y
      })
    }
    initialized.value = true;
  }, 120)
}

onMounted(async () => {
  getMessage()

  initializePositions()

});

const getMessage = async () => {
  if (messageList) {
    messageList.value.splice(0, messageList.value.length)
  }

  const res = await getMessageListService()
  messageList.value = res.data.data
  messagePaper.value = res.data.data

}

const addPaper = (message: { userAvatar?: string; nickname?: string; content?: string; }) => {
  const pageContainer = document.querySelector('.message-container');
  if (!pageContainer) return;

  const containerRect = pageContainer.getBoundingClientRect();
  const elementWidth = 200;
  const elementHeight = 200;
  const { x, y } = getRandomPosition(
    containerRect.width,
    containerRect.height,
    elementWidth,
    elementHeight
  );

  const currentTime = formatDate(new Date()); // 获取当前时间并格式化

  messagePaper.value.push({
    id: 0,
    nickname: message.nickname || '',
    userAvatar: message.userAvatar || '',
    content: message.content || '',
    createTime: currentTime,
    x: x,
    y: y,
  });
};


const send = async () => {
  if (content.value.trim() == "") {
    ElMessage.warning("留言内容不能为空");
    return;
  }


  let message = {
    userAvatar: userState.userInfo.avatar,
    nickname: userState.userInfo.nickname,
    content: content.value,
  };

  addPaper(message)
  // messageList.value.push({ id: 0, ...message })
  ElMessage.success("留言成功");
  try {
    await addMessageService({ content: content.value })
  } finally {
    content.value = ""
  }

}
</script>

<style lang="scss" scoped>
body,
html {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}

.message-container {
  position: fixed;
  top: 35%;
  left: 0;
  right: 0;
  width: 100%;
  height: 65%;
  z-index: 5;
  // background: url("/src/assets/images/board.png");
  // background-size: contain;
}

.message {
  background: url("/src/assets/images/board.png");
  background-size: 100% 120%;
  width: 100vw;
  height: 100vh;
  position: relative;

}

.paper-image {
  width: 200px;
  height: 200px;
  position: relative;
  background: url("/src/assets/images/paper.png");
  background-size: cover;
  padding: 10px 15px;
  box-sizing: border-box;
  border-radius: 10px;
  overflow: hidden;
  z-index: 1;
}

.paper-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.paper-nickname {
  margin-left: 10px;
  font-weight: bold;
}

.paper-text {
  padding-left: 20px;
  margin-bottom: 8px;
  word-wrap: break-word;
  height: 105px;
  overflow: hidden;
}

.paper-time {
  display: flex;
  justify-content: flex-end;
  font-size: 12px;
  color: #999;
}

.message-write {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 2rem;
}

.message-title {
  color: var(--color-orange);
  text-align: center;
  animation: titleScale 1s;
}

.message-input {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 400px;
}

.message-input .input {
  flex: 1;
  height: 2.5rem;
  border-radius: 1.25rem;
  padding: 0 1.25rem;
  outline: none;
  color: #333;
  border: 1px solid #ddd;
  margin-right: 1rem;

}

.message-input .input::-webkit-input-placeholder {
  color: #999;
}

.message-input .send {
  height: 2.5rem;
  padding: 0 1.25rem;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  animation: slideUpIn 0.4s;
}

.danmaku-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 35%;
  animation: slideDownIn 1s;
  // background: url("/src/assets/images/image4.jpg");
}

.danmaku {
  position: fixed;
  top: 7rem;
  width: 100%;
  height: 30%;
  z-index: 100;

  .danmaku-item {
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6.25rem;
    background-color: rgba(0, 0, 0, 0.3);
    color: #fff;
    padding-right: 2rem;
  }

  .ml {
    margin-left: 0.5rem;
  }
}
</style>
