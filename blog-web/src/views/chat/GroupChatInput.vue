<template>
  <div class="emoji-container" ref="emojiContainerRef">
    <!-- 使用 v-show 而不是 v-if，以便保持组件状态 -->
    <div class="emoji-picker-wrapper" v-show="emojiVisible">
      <ImageListMapper @select="handleEmojiSelect" />
    </div>
  </div>
  <div class="input-header">
    <!-- 包裹表情图标和列表的容器 -->
    <div class="input-icon" tabindex="0">
      <SvgIcon size="20" name="icon-biaoqing" class="icon-emoji pointer" @click="opeEmojiSelect">
      </SvgIcon>
    </div>
  </div>
  <div class="input-content">
    <div class="chip-wrapper">
      <div v-for="(input, idx) in inputList" :key="idx" class="chip pointer" :class="{ active: selectedIndex === idx }"
        @click="select(idx, input)">
        {{ input }}
      </div>
    </div>
  </div>

  <div class="input-send">
    <YlButton class="input-button" info="发送" width="100px" default-color="#d8d8d8" focus-color="#9cd0ed" @click="send"
      :focus="content != ''"></YlButton>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import YlButton from '@/components/Button/YlButton.vue';
import type { ChatMessage } from '@/types/chatType';
import { sendEmojiMessageService, sendMessageService } from '@/api/chat';
import { useUserStore } from '@/stores';
import { ElMessage } from 'element-plus';

const userStore = useUserStore()
const emit = defineEmits<{
  scrollToBottom: []
}>()
const content = ref('')
const selectedIndex = ref<number | null>(null)
const inputList = ref(["大家好吖，喵！",
  "喵喵喵，喵！",
  "今天也是元气满满的一天呢~",
  "(≧∇≦)ﾉ",
  "awsl！这也太可爱了吧",
  "摸鱼ing，勿扰~",
  "qwq",
  "ovo"])
const emojiVisible = ref(false);

const select = (idx: number, input: string) => {
  if (selectedIndex.value == idx) {
    remove()
    return
  }
  selectedIndex.value = idx;
  content.value = input
}

const remove = () => {
  selectedIndex.value = null
  content.value = ''
}
const send = async () => {
  if (content.value == '') {
    ElMessage.warning('请输入想要发送的内容！！！')
    return
  }
  try {
    await sendMessageService({ type: 'group', message: content.value }, userStore.deviceId)
    emit('scrollToBottom')
  } finally {
    remove()
  }


}
const opeEmojiSelect = () => {
  if (emojiVisible.value) {
    emojiVisible.value = false
    return
  } else {
    emojiVisible.value = true
  }

}

const nonEmojiSelect = () => {
  emojiVisible.value = false
}

const handleEmojiSelect = async (emoji: string) => {

  emojiVisible.value = false
  await sendEmojiMessageService({ type: 'group', message: emoji }, userStore.deviceId)
  onSendSuccess()
}
const onSendSuccess = () => {
  emit('scrollToBottom')
}
defineExpose({
  nonEmojiSelect
})
</script>

<style lang="scss" scoped>
.emoji-container {
  position: relative;
}

.emoji-picker-wrapper {
  position: absolute;
  width: 100%;
  height: 242px;
  top: -250px;
  left: 0;
  z-index: 1000;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.input-header {
  height: 23px;
  display: flex;
  gap: 12px;
}

.input-icon {
  &:hover {
    color: var(--color-blue);
  }

  &:hover SvgIcon {
    filter: drop-shadow(0 0 2px var(--color-blue));
  }
}

.input-content {
  width: 100%;
  height: 78px;
}

.chip-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 0;
}

.chip {
  padding: 4px 12px;
  border-radius: 16px;
  background: #f2f2f2;
  transition: all 0.2s;

  &.active {
    background: #ed6ea0;
    color: #fff;
  }

  &:hover:not(.active) {
    background: #ffe6fa;
  }
}

.input-send {
  display: flex;
  align-items: center;
  width: 100%;
}

.input-button {
  margin-left: auto;
}
</style>
