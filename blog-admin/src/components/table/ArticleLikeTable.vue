<template>
    <div class="table-wrapper">
        <el-table v-loading="like.loading" :data="like.list" height="400" style="width: 100%" class="table">
            <el-table-column prop="id" label="序号" width="80"></el-table-column>
            <el-table-column prop="userId" label="点赞用户ID" width="100"></el-table-column>
            <el-table-column prop="likeTime" label="点赞时间" ></el-table-column>
            <template #empty>
                <el-empty description="没有数据"></el-empty>
            </template>
        </el-table>
        <div class="pagination">
            <el-pagination v-model:current-page="like.page" :disabled="disabled" :background="background"
                :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" :total="like.total"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { ArticleLikeTableVO } from '@/type/article';
import { ref, watch, type PropType } from 'vue';


const props = defineProps({
    like: {
        type: Object as PropType<ArticleLikeTableVO>,
        required: true
    }
})
const background = ref(true)
const disabled = ref(false)


const emit = defineEmits<{
    onHandleCurrentChange: [id: number],
    onHandleSizeChange: [id: number]
}>()

const handleSizeChange = (val: number) => {
    emit('onHandleSizeChange', val);
}

const handleCurrentChange = (val: number) => {
    emit('onHandleCurrentChange', val);
}


</script>

<style lang="scss" scoped>
.table-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 500px;

    .table {
        flex: 1;
    }
}

.pagination {
    margin-top: auto;
    padding-top: 12px;
    background: #fff;
    display: flex;
    justify-content: flex-end;
}
</style>