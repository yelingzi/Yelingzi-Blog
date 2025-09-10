<template>
    <page-container title="相册管理">
        <template #extra>
            <el-button @click="onAddAlbum">添加新的相册</el-button>
        </template>

        <el-table v-loading="loading" :data="albumList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="albumName" label="相册名称"></el-table-column>
            <el-table-column prop="albumDesc" label="相册描述"></el-table-column>
            <el-table-column prop="" label="相册封面" width="200">
                <template #default="{ row }">
                    <el-image class="article-image" :src="row.albumCover" fit="cover" />
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="userId" label="创建者ID"></el-table-column>
            <el-table-column prop="nickname" label="创建者昵称"></el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="onEditAlbum(row, $index)"><el-icon>
                            <Edit />
                        </el-icon></el-button>
                    <el-button v-if="row.isDel === 0" circle plain type="danger"
                        @click="onDelAlbum(row.id, $index)"><el-icon>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :lock-scroll="false" width="500" center class="addAlbum">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="相册名称" prop="albumName">
                <el-input v-model="form.albumName" placeholder="请输入相册名称" />
            </el-form-item>
            <el-form-item label="相册描述" prop="albumDesc">
                <el-input v-model="form.albumDesc" placeholder="请输入相册描述" />
            </el-form-item>
            <el-form-item label="文章封面" prop="albumCover">
                <el-upload class="cover-upload" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                    :show-file-list="false" :auto-upload="true" :http-request="customUpload">
                    <el-image v-if="form.albumCover" :src="form.albumCover" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
        </el-form>

        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitForm()">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { addAlbumService, getAlbumListByPageService, uploadAlbumCoverService } from '@/api/album';
import { addCategoryService, delCategoryService, getCategoryListByPageService } from '@/api/category';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { AlbumVO } from '@/type/album';
import { ElMessage, type UploadProps } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const albumList: AlbumVO[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({
    albumName: '',
    albumDesc: '',
    albumCover: ''
})
const rules = ({
    albumName: [
        { required: true, message: '请输入相册名称', trigger: 'blur' }
    ],
    albumDesc: [
        { required: true, message: '请输入相册描述', trigger: 'blur' }
    ],
    albumCover: [
        { required: true, message: '封面不能为空', trigger: 'change' }
    ]
})
const getAlbumList = async () => {
    loading.value = true
    clearAlbum()
    const res = await getAlbumListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const r of res.data.data.data) {
        albumList.push(r)
    }
    loading.value = false
}

const tableRowClassName = ({ row, rowIndex }: { row: AlbumVO, rowIndex: number }) => {
    return row.isDel === 1 ? 'warning-row' : '';
}

const handleCurrentChange = () => {
    getAlbumList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
}
const onDelAlbum = async (id: any, index: any) => {
    await delCategoryService(id);
    ElMessage.success("删除成功")
    getAlbumList()
}
const onEditAlbum = (row: any, index: any) => {
    dialogTitle.value = "修改相册"
    form.value.albumDesc = row.albumDesc
    form.value.albumName = row.albumName
    form.value.albumCover = row.albumCover
    dialogVisible.value = true
}
const onAddAlbum = () => {
    dialogTitle.value = "添加相册"
    dialogVisible.value = true
}

const submitForm = async () => {
    console.log(checkAlbum())
    if (await checkAlbum()) {        
        loading.value = true
        await addAlbumService({
            id: 0,
            albumName: form.value.albumName,
            albumDesc: form.value.albumDesc,
            albumCover: form.value.albumCover
        })
        ElMessage.success("添加成功")
        getAlbumList()
        dialogVisible.value = false
        form.value.albumCover = ''
        form.value.albumDesc = ''
        form.value.albumName = ''
    }

}

const checkAlbum = async () => {
    let bool = false
    await formRef.value.validate((valid: any) => {
        if (valid) {
            bool =  true
        } else {
            ElMessage.error('请检查表单内容');
        }
    });
    return bool
}

const clearAlbum = () => {
    if (albumList.length > 0) {
        albumList.splice(0, albumList.length)
    }
}

// 自定义上传逻辑
const customUpload: UploadProps['httpRequest'] = async (options) => {
    const { file } = options;
    const formData = new FormData();
    formData.append('image', file);

    try {
        const response = await uploadAlbumCoverService(formData);
        form.value.albumCover = response.data.data;
    } catch (error) {
        ElMessage.error('上传失败，请重试！');
    }
};

// 上传成功回调
const handleAvatarSuccess: UploadProps['onSuccess'] = () => {
    ElMessage.success("上传成功")
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


onMounted(() => {
    getAlbumList()
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

:deep(.el-table .warning-row) {
    --el-table-tr-bg-color: #ffebe3;
}

.cover-upload {

    width: 168px;
    height: 168px;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);


    &:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 168px;
        height: 168px;
        text-align: center;
    }

}
.article-image{
    max-height: 160px;
}
</style>