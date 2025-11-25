<template>
    <div class="search-container">
        <div class="container-left">
            <div class="item">
                <div class="text">时间：</div>
                <el-date-picker v-model="date" type="daterange" range-separator="到" start-placeholder="开始时间"
                    end-placeholder="结束时间" :disabled-date="disabledFuture" @change="handleDateChange" />
            </div>
            <div class="item">
                <div class="text">标题 ：</div>
                <el-autocomplete v-model="content" :fetch-suggestions="querySearch" clearable placeholder="选择查找的标题"
                    @select="handleSelect" :debounce="600" style="width: 220px" />
            </div>
            <div class="item">
                <div class="text">状态 ：</div>
                <el-select v-model="state" placeholder="选择状态" style="width: 140px">
                    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </div>
        </div>
        <div class="container-right">
            <el-button class="search-btn" type="warning" @click="clearSearch">清空</el-button>
            <el-button class="search-btn" type="primary" @click="handleSearch">查询</el-button>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { searchArticleTitleListService } from '@/api/article';
import dayjs from 'dayjs';
import { ref } from 'vue';

const emit = defineEmits<{
    onSearch: [data: { title: string; date: string[] | Date[]; state: string }]
}>()

const content = ref('')
const date = ref<Date[]>([])
const state = ref('')

const options = [
    {
        value: '0',
        label: '正常',
    },
    {
        value: '1',
        label: '删除',
    },
    {
        value: '2',
        label: '保存',
    }
]

const handleDateChange = (val: string | any[]) => {
  if (val && val.length === 2) {
    const [start, end] = val
    
    const adjustedStart = dayjs(start).startOf('day').toDate()
    const adjustedEnd = dayjs(end).endOf('day').toDate()
    
    date.value = [adjustedStart, adjustedEnd]
    
  }
}

const disabledFuture = (time: { getTime: () => number; }) => {
    return time.getTime() > Date.now()
}

const querySearch = async (queryString: string, cb: any) => {
    const res = await searchArticleTitleListService(queryString)
    const restaurants = res.data.data
    const results = queryString ? restaurants.filter((r: { value: string | string[]; }) => r.value.indexOf(queryString) === 0) : restaurants
    cb(results)
}

const handleSelect = (item: Record<string, any>) => {
    console.log('选择的标题：', item.value)
}

const clearSearch = () => {
    content.value = ''
    date.value = []
    state.value = ''
    emit('onSearch', { title: '', date: [], state: '' })
}

const handleSearch = () => {
    emit('onSearch', { title: content.value, date: date.value, state: state.value })
}

</script>

<style lang="scss" scoped>
.search-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    background-color: #fff;
    border-radius: 12px;


    .container-left {
        display: flex;
        gap: 32px;
        justify-content: flex-start;

        .text {
            min-width: 60px;
            font-size: 18px;
        }

        .item {
            display: flex;
            align-items: center;
        }


    }
}

.search-btn {
    width: 80px;
}
</style>