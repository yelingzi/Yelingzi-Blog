<!--
 * @Description: ------------ fileDescription -----------
 * @Author: snows_l snows_l@163.com
 * @Date: 2024-08-14 12:23:38
 * @LastEditors: snows_l snows_l@163.com
 * @LastEditTime: 2025-01-12 01:00:47
 * @FilePath: /BLOG/src/components/common/LImg.vue
-->

<!--
  * @Description: 图片展示组件
  *props:
  * src: 图片地址
  * fit: 图片缩放类型 默认 fill
  * size: 图片大小 默认 60px
  * isUnPreview: 是否不显示预览 默认 false
  * loadingText: 加载文字 默认 加载中...
  * isText: 是否显示文字 默认 true
  * alt: 图片alt属性 默认 ''
 -->
<template>
  <el-image class="pointer el-img-warp" :width="width" :height="height" preview-teleported hide-on-click-modal infinite
    :src="src" :fit="fit" :previewSrcList="isUnPreview ? [] : preview.length > 0 ? preview : [src]"
    :initial-index="index" :lazy="true">
    <template #placeholder>
      <div class="img-loading-warp pointer glogal-img-warp"
        style="width: 100%; height: 100%; background-color: #ffffff">
        <img :style="{ width: size, height: size }" class="img-loading" src="@/assets/images/common/loading.svg"
          alt="loading" />
        <span v-if="isText" class="img-loading-text tip-text">{{ loadingText }}</span>
      </div>
    </template>

    <template #error>
      <div class="img-error-warp glogal-img-warp">
        <img :style="{ width: size, height: size }" class="img-error" src="@/assets/images/common/error.png"
          alt="error" />
        <span v-if="isText" class="img-error-text tip-text">😭 加载失败了</span>
      </div>
    </template>
  </el-image>
</template>

<script lang="ts" setup>

const props = defineProps({
  // 图片地址
  src: {
    type: String,
    required: true,
    default: ''
  },
  // 图片缩放类型 默认 fill
  fit: {
    type: String,
    default: 'cover'
  },
  // 图片大小 默认 60px
  size: {
    type: String,
    default: '60px'
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: '100%'
  },
  // 是否不显示预览 默认 false
  isUnPreview: {
    type: Boolean,
    default: false
  },
  preview: {
    type: Array,
    default: () => []
  },
  index: {
    type: Number,
    default: 0
  },
  // 加载文字 默认 加载中...
  loadingText: {
    type: String,
    default: '加载中...'
  },
  // 是否显示文字 默认 true
  isText: {
    type: Boolean,
    default: true
  },
  alt: {
    type: String,
    default: ''
  }
});
</script>

<style lang="scss" scoped>
.el-img-warp {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .el-image__inner,
  .el-image__preview {
    cursor: url('@/assets/images/cursor/pointer.png'), auto !important;
  }
}

.glogal-img-warp {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;

  .tip-text {
    margin-top: 4px;
    font-size: 10px;
    color: #999;
  }
}
</style>
