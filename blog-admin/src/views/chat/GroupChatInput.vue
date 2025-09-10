<template>
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
import type { ChatMessage } from '@/type/chatType';
import { sendMessageService } from '@/api/chat';
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
    await sendMessageService({ type: 'group', message: content.value })
    emit('scrollToBottom')
  } finally {
    remove()
  }


}
</script>

<style lang="scss" scoped>
.input-content {
  width: 100%;
  height: 99px;
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
