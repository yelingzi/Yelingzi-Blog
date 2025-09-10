<template>
    <div class="chart-wrapper" :style="{ width: width, height: height }">
        <div ref="chart" style="width: 100%; height: 100%"></div>
        <div v-if="loading" class="loading">加载中...</div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref, watch } from 'vue';
import * as echarts from 'echarts/core';
import { LineChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

//ec图表
interface Props {
    xData: string[]
    yData: number[]
    title: string
    theme?: string
    width?: string
    height?: string
}
const props = withDefaults(defineProps<Props>(), {
    xData: () => [] as string[],
    yData: () => [] as number[],
    title: '',
    theme: 'light',
    width: '100%',
    height: '500px'
});
echarts.use([LineChart, TitleComponent, TooltipComponent, GridComponent, CanvasRenderer]);

const emit = defineEmits(['click']);
const chart = ref(null);
const loading = ref(true);
let chartInstance: echarts.ECharts | null = null;
const initChart = () => {
    if (!chart.value) return;

    chartInstance = echarts.init(chart.value, props.theme);

    const baseOptions = {
        title: { text: props.title },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: props.xData },
        yAxis: { type: 'value' },
        series: [{
            data: props.yData,
            type: 'line'
        }]
    };

    chartInstance.setOption(baseOptions);
    chartInstance.on('click', params => emit('click', params));
    loading.value = false;
};

const updateChart = () => {
    if (!chartInstance) return;

    const option = {
        title: {
            text: props.title
        },
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: props.xData
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: props.yData,
                type: 'line'
            }
        ]
    };

    chartInstance.setOption(option);
};

watch(
    () => [props.xData, props.yData, props.title, props.width, props.height],
    () => {
        updateChart();
    },
    { deep: true }
);

onMounted(() => {
    initChart();
    window.addEventListener('resize', () => chartInstance?.resize());
});

onUnmounted(() => {
    window.removeEventListener('resize', () => chartInstance?.resize());
    chartInstance?.dispose();
});
</script>

<style lang="scss" scoped>
.chart-wrapper {
    position: relative;
}

.loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}
</style>