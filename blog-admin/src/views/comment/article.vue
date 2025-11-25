<template>

    <page-container title="文章评论管理">
        <template #extra>
            <!-- <el-button @click="onAddMessage">添加新的留言</el-button> -->
        </template>
        <ArticleTable :comment-list="commentList" :loading="loading"
            v-on:on-del-message="onDelMessage"
            v-on:on-pass-message="onPassMessage">
        </ArticleTable>
        <template #pagination>
            <div class="pagination">
                <el-pagination v-model:current-page="page" :disabled="disabled" :background="background"
                    :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </template>
    </page-container>

</template>

<script lang="ts" setup>
import { delArticleCommentService, getArticleCommentListByPageService, updateArticleCommentService } from '@/api/comment';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import ArticleTable from '@/components/table/articleTable.vue';
import type { ArticleComment } from '@/type/comment';
import type { MessageVO } from '@/type/message';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref, toRaw } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const commentList = ref<ArticleComment[]>([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)

const getMessageList = async () => {
    loading.value = true
    clearMessage()
    const res = await getArticleCommentListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    commentList.value = res.data.data.data
    loading.value = false
}

const tableRowClassName = ({ row, rowIndex }: { row: MessageVO, rowIndex: number }) => {
    if (row.state === 1) {
        return 'warning-row'
    } else if (row.state === 2) {
        return 'success-row'
    }
    return '';
}

const handleCurrentChange = () => {
    getMessageList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
    getMessageList()
}
const onDelMessage = async (id: number) => {
    await delArticleCommentService(id);
    ElMessage.success("删除成功")
    getMessageList()
}
const onPassMessage = async (id: number) => {
    await updateArticleCommentService(id);
    ElMessage.success("审核通过")
    getMessageList()
}

const clearMessage = () => {
    if (commentList.value.length > 0) {
        commentList.value.splice(0, commentList.value.length)
    }
}
onMounted(() => {
    getMessageList()
})
</script>


<style lang="scss" scoped>
.table-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;

    .table {
        flex: 1;
    }
}

.pagination {
    display: flex;
    justify-content: flex-end;
}

:deep(.warning-row) {
    background-color: #ffebe3;
}

:deep(.success-row) {
    background-color: #e1f3d8;
}
</style>