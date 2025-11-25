<template>
    <div class="stat-card-list" v-loading="loading">
        <StatCard icon="Article" color="#40c9c6" label="文章数" :value="statistics.articleCount" />
        <StatCard icon="Category" color="#CCCCCC" label="分类数" :value="statistics.categoryCount" />
        <StatCard icon="Tags" color="#2196F3" label="标签数" :value="statistics.tagCount" />
        <StatCard icon="Talk" color="#4CAF50" label="说说数" :value="statistics.talkCount" />
        <StatCard icon="Team" color="#3F51B5" label="用户数" :value="statistics.userCount" />
        <StatCard icon="Message" color="#E91E63" label="留言数" :value="statistics.messageCount" />
        <StatCard icon="Comment" color="#FFA500" label="评论数" :value="statistics.commentCount" />
        <StatCard icon="View" color="#9C27B0" label="浏览量" :value="statistics.viewCount" />
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import StatCard from '@/components/Statistics/StatCard.vue';
import type { HomeStatistics } from '@/type/home';
import { getStatisticsService } from '@/api/statistics';

const loading = ref(false)
const statistics = ref<HomeStatistics>({
    categoryCount: 0,
    commentCount: 0,
    tagCount: 0,
    userCount: 0,
    talkCount: 0,
    viewCount: 0,
    articleCount: 0,
    messageCount: 0
})

const getStatisticsData = async () => {
    loading.value = true
    const res = await getStatisticsService()
    statistics.value = res.data.data || statistics.value
    loading.value = false
}


onMounted(() => {
    getStatisticsData()
});

</script>

<style lang="scss" scoped>
.stat-card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}
</style>