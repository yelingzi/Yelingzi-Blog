<!--
 * @Description: ------------ fileDescription -----------
 * @Author: snows_l snows_l@163.com
 * @Date: 2024-07-08 10:56:57
 * @LastEditors: snows_l snows_l@163.com
 * @LastEditTime: 2025-01-03 15:42:27
 * @FilePath: \BLOG\src\components\common\Empty.vue
-->

<!--
  * @Description: 空状态组件
  *props:
  * text: 显示的文字 默认为'暂无数据'
  * loading: 是否显示加载中 默认为false
  * loadingText: 加载中文字 默认为'加载中...'
-->
<template>
  <div class="custom-empty-warp">
    <div class="empty-warp">
      <div class="loading-warp" v-if="loading">
        <img class="loading-img" src="@/assets/images/common/loading.svg" alt="" />
        <span class="empty-text">{{ loadingText }}</span>
      </div>
      <div class="img-warp" v-else>
        <img class="empty-img" src="@/assets/images/common/empty.svg" alt="" />
        <span v-if="true" class="empty-text">{{ text }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>

const props = defineProps({
  text: {
    type: String,
    default: '暂无数据'
  },
  loading: {
    type: Boolean,
    default: false
  },
  loadingText: {
    type: String,
    default: '加载中...'
  }
});
</script>

<style lang="scss" scoped>
.custom-empty-warp {
  width: 100%;
  height: 100%;
  padding: 20px 0;

  .empty-warp {
    background-color: var(--bg-image-warp-color);
    border-radius: var(--border-radius-2);
    height: 100%;
    width: 100%;
    padding: 20px 0;

    .img-warp,
    .loading-warp {
      height: 100%;
      width: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .empty-img {
        max-width: 180px;
        max-height: 180px;
        width: 100%;
        height: calc(100% - 20px);
      }
    }

    .empty-text {
      height: 36px;
      margin-top: 20px;
      color: var(--empty-text-color);
    }
  }
}
</style>
