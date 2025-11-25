<template>
    <el-calendar :range="calendarRange">
        <template #date-cell="{ data }">
            <div class="cell-box" :class="{ today: isToday(data.day), future: isFuture(data.day) }">
                {{ formatDay(data.day) }}
                <el-tag v-if="hasPublished(data.day, '文章')" size="small" type="success" class="indicator">
                    文章
                </el-tag>
                <el-tag v-if="hasPublished(data.day, '说说')" size="small" type="warning" class="indicator">
                    说说
                </el-tag>
            </div>
        </template>
    </el-calendar>
</template>

<script lang="ts" setup>
import { computed, type PropType } from 'vue'
import dayjs from 'dayjs'
import isTodayPlugin from 'dayjs/plugin/isToday'
import type { ArtAndTalkList } from '@/type/home'
dayjs.extend(isTodayPlugin)

const props = defineProps({
    artAndTalkList: {
        type: Array as PropType<ArtAndTalkList[]>,
        required: true
    }
})

/* 1. 计算起止日期：本周 + 前四周 */
const calendarRange = computed(() => {
    const thisWeekMonday = dayjs().startOf('week')
    const thisWeekSunday = dayjs().endOf('week')
    const startMonday = thisWeekMonday.subtract(4, 'week')
    return [startMonday.toDate(), thisWeekSunday.toDate()]
})

/* 2. 工具函数 */
const today = dayjs().startOf('day')

function formatDay(fullDay: string) {
    return fullDay.split('-').slice(1).join('-')
}

function isToday(fullDay: string | number | Date | dayjs.Dayjs | null | undefined) {
    return dayjs(fullDay).isToday()
}

function isFuture(fullDay: string) {
    return dayjs(fullDay).isAfter(today, 'day')
}

// 存在性检测数据结构
const publishedMap = computed(() => {
    const result: Record<string, { 文章: boolean; 说说: boolean }> = {}

    props.artAndTalkList.forEach(item => {
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

</script>

<style lang="scss" scoped>
.cell-box {
    box-sizing: border-box;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
}

.today {
    color: #409eff;
    border-radius: 4px;
}

.future {
    color: #c0c4cc;
}

.indicator {
    margin: 2px;
}
</style>