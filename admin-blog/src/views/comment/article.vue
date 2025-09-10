<template>

    <page-container title="留言管理">
        <template #extra>
            <!-- <el-button @click="onAddMessage">添加新的留言</el-button> -->
        </template>

        <el-table v-loading="loading" :data="commentList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column prop="id" label="序号" width="80"></el-table-column>
            <el-table-column prop="articleId" label="文章" width="80"></el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="createTime" label="评论时间" width="200"></el-table-column>
            <el-table-column prop="userId" label="评论ID" width="100"></el-table-column>
            <el-table-column prop="userNickname" label="评论昵称" width="200"></el-table-column>
            <el-table-column prop="toId" label="回复ID" width="100" ></el-table-column>
            <el-table-column prop="toNickname" label="回复昵称" width="200"></el-table-column>
            <el-table-column prop="replyCount" label="回复数" width="100"></el-table-column>
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
        <div class="pagination">
            <el-pagination v-model:current-page="page" :disabled="disabled" :background="background"
                :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </page-container>
    <el-dialog v-model="dialogVisible" title="添加留言" :lockScroll="false" width="500" center>
        <div class="input-button-container">
            <el-input ref="InputRef" v-model="inputValue" placeholder="标签名" />
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addMessage">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { delArticleCommentService, getArticleCommentListByPageService, updateArticleCommentService } from '@/api/comment';
import { addMessageService, delMessageService, getMessageListByPageService, updateMessageService } from '@/api/message';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
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
const dialogVisible = ref(false)
const inputValue = ref('')


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
const onDelMessage = async (id: number, index: any) => {
    await delArticleCommentService(id);
    ElMessage.success("删除成功")
    getMessageList()
}
const onPassMessage = async (id: number, index: any) => {
    await updateArticleCommentService(id);
    ElMessage.success("审核通过")
    getMessageList()
}
const onAddMessage = () => {
    dialogVisible.value = true
}

const addMessage = async () => {

    if (inputValue.value) {
        loading.value = true
        await addMessageService({ content: inputValue.value })
        ElMessage.success("添加成功")
        getMessageList()
        inputValue.value = ''
        dialogVisible.value = false
    } else {
        ElMessage.error("留言为空")
    }

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
.table {
    height: 76vh;
}

.pagination {
    display: flex;
    justify-content: flex-end;
    /* 将内容推到右边 */
    margin-top: 12px;
}

:deep(.warning-row) {
    background-color: #ffebe3;
}

:deep(.success-row) {
    background-color: #e1f3d8;
} 
</style>