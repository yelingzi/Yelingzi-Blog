<template>
  <div class="stat-table-container">
    <el-table 
      v-loading="loading" 
      :data="data" 
      style="width: 100%"
    >
      <el-table-column type="index" label="序号" width="80" />
      <el-table-column prop="articleName" label="文章名称" min-width="200" />
      <el-table-column prop="count" :label="countLabel" width="150" />

      <template #empty>
        <el-empty description="暂无数据" />
      </template>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { StatArticleList } from '@/type/statistic'

type RankType = 'Views' | 'Likes' | 'Messages'

interface Props {
  data: StatArticleList[]
  loading?: boolean
  type?: RankType
}

const props = withDefaults(defineProps<Props>(), {
  data: () => [],
  loading: false,
  type: 'Views'
})

// 动态计算统计列标题
const countLabel = computed(() => {
  const labelMap: Record<RankType, string> = {
    Views: '浏览量',
    Likes: '点赞数',
    Messages: '评论数'
  }
  return labelMap[props.type] || '统计数量'
})
</script>

<style scoped>
.stat-table-container {
  background-color: var(--el-bg-color-overlay);
  border-radius: 4px;
}
</style>