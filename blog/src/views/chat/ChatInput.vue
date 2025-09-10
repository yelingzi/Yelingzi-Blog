<template>
  <div class="input-header">
    <!-- 1. 点击图标相当于点击 file input -->
    <div class="input-icon" tabindex="0">
      <SvgIcon name="icon-zhaopian" class="pointer" @click="openFileSelect"></SvgIcon>
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
import { sendSingleImageService, sendMessageService } from '@/api/chat';
import { useUserStore } from '@/stores';
import type { ChatMessage } from '@/types/chatType';

const userStore = useUserStore()
const content = ref('')
const fileInputRef = ref<HTMLInputElement>();
const previewUrl = ref<string>();
const imageFile = ref<File>();

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

const onSendSuccess = () => {
  emit('scrollToBottom')
}

</script>

<style lang="scss" scoped>
.input-header {
  padding-bottom: 6px;
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
