<template>
  <div class="emoji-container" ref="emojiContainerRef">
    <!-- 使用 v-show 而不是 v-if，以便保持组件状态 -->
    <div class="emoji-picker-wrapper" v-show="emojiVisible">
      <ImageListMapper @select="handleEmojiSelect" />
    </div>
  </div>
  <div class="input-header">
    <!-- 1. 点击图标相当于点击 -->
    <div class="input-icon" tabindex="0">
      <SvgIcon size="20" name="icon-zhaopian" class="pointer" @click="openFileSelect"></SvgIcon>
    </div>
    <!-- 包裹表情图标和列表的容器 -->

    <div class="input-icon" tabindex="0">
      <SvgIcon size="20" name="icon-biaoqing" class="icon-emoji pointer" @click="opeEmojiSelect">
      </SvgIcon>
    </div>
    <!-- 2. 真正的文件选择框，隐藏起来 -->
    <input ref="fileInputRef" type="file" accept="image/*" style="display: none" @change="onFileChange" />
  </div>
  <div class="input-content">
    <!-- <img v-if="previewUrl" :src="previewUrl" style="max-width: 120px;" /> -->
    <textarea type="textarea" class="input-textarea pen" v-model="content"></textarea>
  </div>
  <div class="input-send">
    <proButton class="input-button pointer" info="发送" width="100px" before="#ed6ea0" after="#9cd0ed" @click="send">
    </proButton>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import proButton from "@/components/Button/proButton.vue";
import { ElMessage } from 'element-plus';
import SvgIcon from '@/components/SvgIcon/SvgIcon.vue';
import { sendSingleImageService, sendMessageService, sendEmojiMessageService } from '@/api/chat';
import { useUserStore } from '@/stores';
import ImageListMapper from '@/components/Image/ImageListMapper.vue';
import { t } from '@/utils/i18n';

const userStore = useUserStore()
const content = ref('')
const fileInputRef = ref<HTMLInputElement>();
const previewUrl = ref<string>();
const imageFile = ref<File>();
const emojiVisible = ref(false);

const emit = defineEmits<{
  scrollToBottom: []
}>()
/* 打开系统文件选择框 */
const openFileSelect = () => {
  fileInputRef.value?.click();
}

/* 选中文件后触发 */
const onFileChange = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  // 简单校验
  if (!beforeAvatarUpload(file)) {
    return
  }

  imageFile.value = file;

  previewUrl.value = URL.createObjectURL(file);

  const formData = new FormData();
  formData.append('image', file);

  await sendSingleImageService(formData, userStore.deviceId)
  onSendSuccess()
}

// 上传前校验
const beforeAvatarUpload = (rawFile: File) => {
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('文件格式不符，请使用 jpg、png 格式文件');
    return false;
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('文件大于 5MB!');
    return false;
  }
  return true;
};

const send = async () => {
  if (content.value == '') {
    ElMessage.warning('请输入想要发送的内容！！！')
    return
  }

  await sendMessageService({ type: 'single', message: content.value }, userStore.deviceId)
  content.value = ''
  onSendSuccess()
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
  await sendEmojiMessageService({ type: 'single', message: emoji }, userStore.deviceId)
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
.input-header {
  height: 23px;
  padding-bottom: 6px;
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

.input-content {
  overflow-y: auto !important;
  scrollbar-width: auto;
  -ms-overflow-style: auto;
  scrollbar-width: thin;
  scrollbar-color: #c4c4c4 transparent;
  width: 100%;
  height: 72px;
}

.input-textarea {
  width: 100%;
  box-sizing: border-box;
  min-height: 68px;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-all;

  letter-spacing: 1px;
  font-family: inherit;
  font-size: 12px;
  resize: none;
  outline: none;
}

.input-textarea::-webkit-scrollbar {
  width: 6px;
}

.input-textarea::-webkit-scrollbar-thumb {
  background: #c4c4c4;
  border-radius: 3px;
}

.input-textarea::-webkit-scrollbar-track {
  background: transparent;
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
