<template>
  <el-avatar :size="size" :src="displaySrc" @error="onError">
    <!-- 兜底插槽：图片彻底失败时显示图标 -->
    <SvgIcon :size="size" name="icon-geren">
    </SvgIcon>
  </el-avatar>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import defaultAvatar from '@/assets/images/avatar.jpg'
import SvgIcon from '../SvgIcon/SvgIcon.vue';

interface Props {
  src?: string
  size?: number
}
const props = withDefaults(defineProps<Props>(), {
  src: '',
  size: 40
})

/* 用本地默认图兜底，避免 404 再发一次请求 */
const displaySrc = ref(props.src || defaultAvatar)

watch(() => props.src, (val) => {
  displaySrc.value = val || defaultAvatar
})

/* 图片加载失败 → 立即换成默认图 */
const onError = () => {
  displaySrc.value = defaultAvatar
  return true   // 阻止 el-avatar 继续抛错
}
</script>
