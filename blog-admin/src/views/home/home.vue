<template>
    <div class="home">
        <div class="stat-card">
            <StatCard icon="Article" color="#40c9c6" label="文章数" :value="statistics.articleCount" />
            <StatCard icon="Category" color="#CCCCCC" label="分类数" :value="statistics.categoryCount" />
            <StatCard icon="Tags" color="#2196F3" label="标签数" :value="statistics.tagCount" />
            <StatCard icon="Talk" color="#4CAF50" label="说说数" :value="statistics.talkCount" />
            <StatCard icon="Team" color="#3F51B5" label="用户数" :value="statistics.userCount" />
            <StatCard icon="Message" color="#E91E63" label="留言数" :value="statistics.messageCount" />
            <StatCard icon="Comment" color="#FFA500" label="评论数" :value="statistics.commentCount" />
            <StatCard icon="View" color="#9C27B0" label="浏览量" :value="statistics.viewCount" />
        </div>
        <div class="calendar-container">
            <div class="calendar">
                <div v-for="(monthData, index) in monthList" :key="index">
                    <el-calendar v-model="monthData.date">
                        <template #header="{ date }">
                            <span>{{ formatYearMonth(date) }}</span>
                        </template>
                        <template #date-cell="{ data }">
                            <div class="day-cell">
                                <div class="day-number">{{ data.day.split('-')[2] }}</div>
                                <div class="indicators">
                                    <el-tag v-if="hasPublished(data.day, '文章')" size="small" type="success"
                                        class="indicator">
                                        文章
                                    </el-tag>
                                    <el-tag v-if="hasPublished(data.day, '说说')" size="small" type="warning"
                                        class="indicator">
                                        说说
                                    </el-tag>
                                </div>
                            </div>
                        </template>
                    </el-calendar>
                </div>
            </div>
        </div>
        <div class="echarts">
    <echarts v-loading="loading" :x-data="chartData.xData" :y-data="chartData.yData" title="最近访问量"
        width="1000px" />

    <div class="table-container">
        <h3 class="table-title">最近访客</h3>
        <el-table v-loading="loading" :data="viewInfoList" class="table">
            <el-table-column prop="ip" label="IP"></el-table-column>
            <el-table-column prop="city" label="地区"></el-table-column>
            <el-table-column prop="createTime" label="时间"></el-table-column>
            <el-table-column prop="nickname" label="昵称"></el-table-column>
            <template #empty>
                <el-empty description="没有数据"></el-empty>
            </template>
        </el-table>
    </div>
</div>
    </div>

</template>

<script lang="ts" setup>
import { ref, onMounted, watch, onUnmounted, defineAsyncComponent, computed } from 'vue';
import echarts from '@/components/Echarts/echarts.vue';
import StatCard from '@/components/Statistics/StatCard.vue';
import type { HomeStatistics, ViewData, ViewInfo } from '@/type/home';
import { getArtAndTalkStatisticsService, getStatisticsService, getViewInfoListService, getViewStatisticsService } from '@/api/statistics';
import { formatDate, removeTFrontDateString } from '@/utils/commom';

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

interface ArtAndTalkList {
    id: number
    type: '文章' | '说说'
    createTime: string // ISO 格式日期 如 '2024-07-15T08:00:00Z'
}

// 当前日期基准
const baseDate = ref(new Date())
// 生成半年的月份数据（包含正确年份）
const monthList = computed(() => {
    return Array.from({ length: 3 }).map((_, index) => {
        const date = new Date(baseDate.value)
        date.setMonth(date.getMonth() - index)
        return {
            year: date.getFullYear(),
            month: date.getMonth() + 1, // 月份调整为1-12
            date: date
        }
    }).reverse() // 按时间顺序排列
})

const formatYearMonth = (dateStr: string) => {
    // 提取年份和月份
    const match = dateStr.match(/(\d+) 年 (\d+) 月/);
    if (!match) return '无效日期';

    const year = parseInt(match[1]);
    const month = parseInt(match[2]);

    // 创建日期对象
    const date = new Date(year, month - 1); // 月份从0开始
    if (isNaN(date.getTime())) return '无效日期';

    // 格式化输出
    return `${year}年${(month).toString().padStart(2, '0')}月`;
};

// 内容数据
const artAndTalkList = ref<ArtAndTalkList[]>([])

// 存在性检测数据结构
const publishedMap = computed(() => {
    const result: Record<string, { 文章: boolean; 说说: boolean }> = {}

    artAndTalkList.value.forEach(item => {
        // 标准化日期为YYYY-MM-DD格式
        const dateStr = item.createTime.split('T')[0]
        if (!result[dateStr]) {
            result[dateStr] = { 文章: false, 说说: false }
        }
        result[dateStr][item.type] = true
    })

    return result
})

// 检测是否发布
const hasPublished = (dateStr: string, type: '文章' | '说说') => {
    return !!publishedMap.value[dateStr]?.[type]
}

const viewData = ref<ViewData[]>([])
const chartData = ref<{ xData: string[], yData: number[] }>({
    xData: [],
    yData: []
});

const generateChartData = () => {
    const xData: string[] = [];
    const yData: number[] = [];

    viewData.value.forEach((item) => {
        xData.push(item.createTime);
        yData.push(item.viewCount);
    });

    // 倒序处理
    return {
        xData: xData,
        yData: yData,
    };
};

const viewInfoList = ref<ViewInfo[]>([])

const getStatisticsData = async () => {
    loading.value = true
    const res = await getStatisticsService()
    statistics.value = res.data.data

    const artAndTalk = await getArtAndTalkStatisticsService()
    artAndTalkList.value = artAndTalk.data.data

    const vd = await getViewStatisticsService()
    vd.data.data.forEach((item: { id: any; viewCount: any; createTime: string | Date; }) => {
        viewData.value.push({ id: item.id, viewCount: item.viewCount, createTime: formatDate(item.createTime) })
    })

    chartData.value = generateChartData()

    const info = await getViewInfoListService()
    for (const item of info.data.data) {
        viewInfoList.value.push({ ...item, createTime: removeTFrontDateString(item.createTime) })
        console.info(removeTFrontDateString(item.createTime))
    }

    loading.value = false
}


onMounted(() => {
    getStatisticsData()
});

onUnmounted(() => {
});
</script>

<style lang="scss" scoped>
.home {
    min-height: 80vh;
}

.stat-card {
    display: flex;
    align-items: center;
}



.calendar-container {
    width: 100%;
    max-width: 1600px;
    border: 1px solid var(--grey-9-a1);
    margin-top: 12px;
    padding: 0 20px;
}

.calendar {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    /* 日历之间的间距 */
}

/* 为每个日历设置最小宽度，确保在小屏幕上也能正常显示 */
.calendar>div {
    min-width: 300px;
}

/* 响应式设计：在小屏幕上显示为单列 */
@media (max-width: 768px) {
    .calendar {
        grid-template-columns: 1fr;
    }
}

/* 日期单元格样式 */
.day-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.day-number {
    font-weight: bold;
    margin-bottom: 4px;
}

.indicators {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 4px;
}

.indicator {
    margin: 2px;
}

.echarts {
    margin-top: 12px;
    border: 1px solid var(--grey-9-a1);
    padding: 20px;
    max-width: 1600px;
    display: flex;
}

.table-container {
    width: 550px;
    height: 500px;
    margin-left: 30px;
}

.table {
    width: 100%;
    height: calc(100% - 40px); 
    margin-top: 10px;
}

.table-title {
    margin: 0;
    padding: 8px 0;
    font-size: 18px;
    color: var(--grey-6);
    border-bottom: 1px solid var(--grey-9-a1);
}
</style>