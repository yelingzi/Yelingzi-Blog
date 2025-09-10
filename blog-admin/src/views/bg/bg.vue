<template>

    <page-container title="留言管理">
        <template #extra>
            只会展示最近的5张
            <el-select v-model="selectBg" placeholder="请选择查看类型" style="width: 200px;margin-right: 20px;">
                <el-option label="展示" :value="0" />
                <el-option label="未展示" :value="1" />
            </el-select>
            <el-button @click="onAddBg">添加新的图片</el-button>
        </template>

        <el-table v-loading="loading" :data="bgList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column prop="id" label="序号" width="80"></el-table-column>
            <el-table-column prop="url" label="图片" width="400">
                <template #default="{ row }">
                    <el-image class="cover-image" :src="row.url" fit="cover">
                        <template #placeholder>
                            <div class="image-placeholder">加载中...</div>
                        </template>
                    </el-image>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="添加时间" ></el-table-column>
            <el-table-column prop="userId" label="ID" width="100"></el-table-column>
            <el-table-column prop="nickname" label="昵称" ></el-table-column>
            <el-table-column prop="" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag type="primary" v-if="row.state === 0">展示</el-tag>
                    <el-tag type="danger" v-else-if="row.state === 1">未展示</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button v-if="row.state !== 0" circle plain type="primary"
                        @click="onShowBg(row.id, $index)"><el-icon>
                            <Check />
                        </el-icon></el-button>
                    <el-button v-if="row.state !== 1" circle plain type="danger"
                        @click="onDelBg(row.id, $index)"><el-icon>
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
    <el-dialog v-model="dialogVisible" title="添加背景图片" :lockScroll="false" width="500" center>
        <div>
            <el-upload class="cover-upload" :on-change="onSelectImage" :before-upload="beforeAvatarUpload"
                :show-file-list="false" :auto-upload="false">
                <el-image v-if="imageUrl" :src="imageUrl" class="avatar" fit="cover"/>
                <el-icon v-else class="avatar-uploader-icon">
                    <Plus />
                </el-icon>
            </el-upload>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addBg">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { delBgCommentService, getBgDelListByPageService, getBgListByPageService, updateBgCommentService, uploadBgImageService } from '@/api/bg';
import { delArticleCommentService, delTalkCommentService, getArticleCommentListByPageService, updateArticleCommentService, updateTalkCommentService } from '@/api/comment';
import { addMessageService, delMessageService, getMessageListByPageService, updateMessageService } from '@/api/message';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { Background } from '@/type/bg';
import type { ArticleComment } from '@/type/comment';
import type { MessageVO } from '@/type/message';
import { ElMessage, type UploadProps } from 'element-plus';
import { onMounted, reactive, ref, toRaw, watch } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const bgList = ref<Background[]>([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const dialogVisible = ref(false)
const imageUrl = ref('')
const imageFile = ref()
const selectBg = ref(0)

const getBgList = async () => {
    loading.value = true
    clearBg()
    if (selectBg.value === 0) {
        const res = await getBgListByPageService(page.value, pageSize.value)
        total.value = res.data.data.total
        bgList.value = res.data.data.data
    } else if (selectBg.value === 1) {
        const res = await getBgDelListByPageService(page.value, pageSize.value)
        total.value = res.data.data.total
        bgList.value = res.data.data.data
    }

    loading.value = false
}

const tableRowClassName = ({ row, rowIndex }: { row: MessageVO, rowIndex: number }) => {
    if (row.state === 1) {
        return 'warning-row'
    } else if (row.state === 0) {
        return 'success-row'
    }
    return '';
}

const handleCurrentChange = () => {
    getBgList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
    getBgList()
}
const onDelBg = async (id: number, index: any) => {
    await delBgCommentService(id);
    ElMessage.success("设置未展示成功")
    getBgList()
}
const onShowBg = async (id: number, index: any) => {
    await updateBgCommentService(id);
    ElMessage.success("设置展示成功")
    getBgList()
}
const onAddBg = () => {
    dialogVisible.value = true
}

const onSelectImage = (uploadFile: any) => {

    const { raw } = uploadFile;
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    const maxSize = 2 * 1024 * 1024;
    if (!allowedTypes.includes(raw.type)) {
        ElMessage.error('文件格式不符，请使用 jpg、png 格式文件');
        return;
    }
    if (raw.size > maxSize) {
        ElMessage.error('文件大于 2MB!');
        return;
    }
    imageUrl.value = URL.createObjectURL(raw)
    imageFile.value = raw

};

// 上传前校验
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    if (!allowedTypes.includes(rawFile.type)) {
        ElMessage.error('文件格式不符，请使用 jpg、png 格式文件');
        return false;
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('文件大于 2MB!');
        return false;
    }
    return true;
};

const addBg = async () => {
    const formData = new FormData();
    formData.append('image', imageFile.value);
    await uploadBgImageService(formData)
    ElMessage.success("添加背景图片成功")
    imageFile.value = ''
    imageUrl.value = ''
    getBgList()
}

const clearBg = () => {
    if (bgList.value.length > 0) {
        bgList.value.splice(0, bgList.value.length)
    }
}

watch(
    () => selectBg.value,
    (newVal) => {
        page.value = 1;
        getBgList();
    }
);

onMounted(() => {
    getBgList()
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

.cover-upload {
    width: 320px;
    height: 200px;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    position: relative;
    overflow: hidden;

    margin: 12px auto;
    transition: var(--el-transition-duration-fast);


    &:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 320px;
        height: 200px;
        text-align: center;
    }

}
</style>