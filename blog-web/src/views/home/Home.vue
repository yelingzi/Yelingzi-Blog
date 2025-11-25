<template>
    <div class="home">
        <div class="splitter-container">
            <el-splitter lazy>
                <el-splitter-panel :size="360" :min="350" :max="500">
                    <div class="stat-card">
                        <StatCardList />
                    </div>
                </el-splitter-panel>
                <el-splitter-panel  :size="620" :min="520" :max="650">
                    <CalendarView v-show="!calendar" :art-and-talk-list="artAndTalkList" />
                </el-splitter-panel>
                <el-splitter-panel>
                    <Segmented />
                </el-splitter-panel>
            </el-splitter>
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
import { ref, onMounted, onUnmounted } from 'vue';
import echarts from '@/components/Echarts/echarts.vue';
import StatCardList from '@/components/Statistics/StatCardList.vue';
import type { ArtAndTalkList, ViewData, ViewInfo } from '@/type/home';
import { getArtAndTalkStatisticsService, getViewInfoListService, getViewStatisticsService } from '@/api/statistics';
import { formatDate, removeTFrontDateString } from '@/utils/commom';
import CalendarView from '@/components/Calendar/CalendarView.vue';
import Segmented from '@/components/Statistics/Segmented.vue';

const loading = ref(false)

// 内容数据
const artAndTalkList = ref<ArtAndTalkList[]>([])

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
const calendar = ref(true)
const getArtAndTalkStatistics = async () => {
    calendar.value = true
    try {
        const artAndTalk = await getArtAndTalkStatisticsService()
        if (artAndTalk.data.data) {
            artAndTalkList.value = artAndTalk.data.data
        }
        calendar.value = false
    } catch (error) {
        console.error('Failed to fetch art and talk statistics:', error)
    }
}

const getStatisticsData = async () => {
    loading.value = true

    getArtAndTalkStatistics()
    const vd = await getViewStatisticsService()
    if (vd.data.data) {
        vd.data.data.forEach((item: { id: any; viewCount: any; createTime: string | Date; }) => {
            viewData.value.push({ id: item.id, viewCount: item.viewCount, createTime: formatDate(item.createTime) })
        })
    }

    chartData.value = generateChartData()

    const info = await getViewInfoListService()
    if (info.data.data) {
        for (const item of info.data.data) {
            viewInfoList.value.push({ ...item, createTime: removeTFrontDateString(item.createTime) })
            console.info(removeTFrontDateString(item.createTime))
        }
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
    height: 100%;
}

.stat-card {
    display: flex;
    align-items: center;
}



.splitter-container {
    border: 1px solid var(--grey-9-a1);
    margin-top: 12px;
}

/* 响应式设计：在小屏幕上显示为单列 */
@media (max-width: 768px) {
    .calendar {
        grid-template-columns: 1fr;
    }
}

.echarts {
    margin-top: 12px;
    border: 1px solid var(--grey-9-a1);
    padding: 20px;  
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