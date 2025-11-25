<template>
  <div class="page-container">
    <el-card>
      <slot name="search"></slot>
    </el-card>
    <el-card class="content">
    <template #header>
      <div class="header">
        <span>{{ title }}</span>
        <div class="extra">
          <slot name="extra"></slot>
        </div>
      </div>
    </template>

    <!-- 可滚动的内容区域 -->
    <div class="content-wrapper">
      <slot></slot>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-wrapper" v-if="$slots.pagination">
      <slot name="pagination"></slot>
    </div>
  </el-card>
  </div>
  
</template>

<script lang="ts" setup>
defineProps({
  title: {
    required: true,
    type: String
  }
})
</script>

<style lang="scss" scoped>
.page-container {
  height: 100%;
  box-sizing: border-box;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .content {
    flex: 1;
    box-sizing: border-box;
    border-radius: 6px;
    display: flex;
    flex-direction: column;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0; 
    overflow: hidden;
  }

  .content-wrapper {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
  }

  .pagination-wrapper {
    // border-top: 1px solid #e4e7ed;
    padding: 0 24px 20px 24px;
    background: #fff;
    flex-shrink: 0;
  }
}
</style>