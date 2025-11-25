<template>
    <div class="segmented-container">
        <div class="segmented-label">文章排序方式</div>
        <el-segmented v-model="rankType" :options="options">
            <template #default="{ item }">
                <div class="segmented-item">
                    <el-icon :size="20">
                        <component :is="item.icon" />
                    </el-icon>
                    <span>{{ item.label }}</span>
                </div>
            </template>
        </el-segmented>

        <div class="table-wrapper">
            <StatTable :data="articleList" :loading="loading" :type="rankType" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { View, Star, ChatLineRound } from '@element-plus/icons-vue'
import StatTable from './StatTable.vue'
import { getCommentCountRankService, getLikeCountRankService, getViewCountRankService } from '@/api/statistics'
import type { StatArticleList } from '@/type/statistic'

type RankType = 'Views' | 'Likes' | 'Messages'

const rankType = ref<RankType>('Views')
const articleList = ref<StatArticleList[]>([])
const loading = ref(false)

const options = [
    { label: '浏览量', value: 'Views' as RankType, icon: View },
    { label: '点赞数', value: 'Likes' as RankType, icon: Star },
    { label: '评论数', value: 'Messages' as RankType, icon: ChatLineRound },
]

// 服务映射表，消除重复代码
const rankServiceMap: Record<RankType, () => Promise<any>> = {
    Views: getViewCountRankService,
    Likes: getLikeCountRankService,
    Messages: getCommentCountRankService,
}

// 统一的数据获取逻辑
const fetchRankData = async () => {
    loading.value = true
    try {
        const service = rankServiceMap[rankType.value]
        const res = await service()
        articleList.value = res.data.data || []
    } catch (error) {
        console.error('获取排名数据失败:', error)
        articleList.value = []
    } finally {
        loading.value = false
    }
}

// 监听排序方式变化，自动刷新数据
watch(rankType, fetchRankData)

// 初始加载
onMounted(() => {
    fetchRankData()
})
</script>

<style scoped>
.segmented-container {
    padding: 20px;
}

.segmented-label {
    margin-bottom: 12px;
    font-size: 24px ;
    color: var(--el-text-color-regular);
}

.segmented-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 8px 12px;
}

.table-wrapper {
    margin-top: 24px;
}
</style>