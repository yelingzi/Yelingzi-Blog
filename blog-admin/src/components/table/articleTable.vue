<template>
    <div class="table-wrapper">
        <el-table v-loading="loading" :data="commentList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column prop="id" label="序号" width="80"></el-table-column>
            <el-table-column prop="articleId" label="文章" width="80"></el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="createTime" label="评论时间" width="200"></el-table-column>
            <el-table-column prop="userId" label="评论用户ID" width="100"></el-table-column>
            <el-table-column prop="userNickname" label="评论用户昵称" width="200"></el-table-column>
            <el-table-column prop="toId" label="回复用户ID" width="100"></el-table-column>
            <el-table-column prop="toNickname" label="回复用户昵称" width="200"></el-table-column>
            <el-table-column prop="replyCount" label="回复数" width="100"></el-table-column>
            <el-table-column prop="likeCount" label="点赞数" width="100"></el-table-column>
            <el-table-column prop="" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag type="primary" v-if="row.state === 0">待审核</el-tag>
                    <el-tag type="danger" v-else-if="row.state === 1">删除</el-tag>
                    <el-tag type="success" v-else-if="row.state === 2">正常</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button v-if="row.state !== 2" circle plain type="primary"
                        @click="onPassMessage(row.id, $index)"><el-icon>
                            <Check />
                        </el-icon></el-button>
                    <el-button v-if="row.state !== 1" circle plain type="danger"
                        @click="onDelMessage(row.id, $index)"><el-icon>
                            <Delete />
                        </el-icon></el-button>
                </template>
            </el-table-column>

            <template #empty>
                <el-empty description="没有数据"></el-empty>
            </template>
        </el-table>

    </div>
</template>

<script lang="ts" setup>
import type { ArticleComment } from '@/type/comment';
import type { PropType } from 'vue';

const props = defineProps({
    commentList: {
        type: Array as PropType<ArticleComment[]>,
        required: true
    },
    loading: {
        type: Boolean,
        required: false,
        default: true
    }
})

const emit = defineEmits<{
    onPassMessage: [id: number],
    onDelMessage: [id: number]
}>()

const tableRowClassName = ({ row, rowIndex }: { row: ArticleComment, rowIndex: number }) => {
    if (row.state === 1) {
        return 'warning-row'
    } else if (row.state === 2) {
        return 'success-row'
    }
    return '';
}

const onDelMessage = (id: number, index: any) => {
    emit('onDelMessage', id);
}

const onPassMessage = (id: number, index: any) => {
    emit('onPassMessage', id);
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
</style>