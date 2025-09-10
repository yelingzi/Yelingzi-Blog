<template>
    <page-container title="相册管理">
        <template #extra>
            <el-select v-model="selectAlbum" placeholder="请选择相册" style="width: 200px;margin-right: 20px;">
                <el-option v-for="album of albumList" :label="album.albumName" :value="album.albumName" />
            </el-select>
        </template>
        <div v-if="album.id !== 0" class="data">
            <el-table v-loading="loading" :data="[album]" style="width: 100%" class="table">
                <el-table-column prop="albumName" label="相册名称" width="250"></el-table-column>
                <el-table-column prop="albumDesc" label="相册描述"></el-table-column>
                <el-table-column prop="" label="相册封面" width="200">
                    <template #default="{ row }">
                        <el-image class="article-image" :src="row.albumCover" fit="cover" />
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
                <el-table-column prop="userId" label="创建者ID" width="100"></el-table-column>
                <el-table-column prop="nickname" label="创建者昵称" width="200"></el-table-column>
                <el-table-column label="操作" width="100">
                    <template #default="{ row }">
                        <el-button circle plain type="primary" @click="onEditAlbum(row)"><el-icon>
                                <Edit />
                            </el-icon></el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div>
                <div class="uploading-image">
                    <div v-for="(image, index) in photoList" :key="index" class="image-item">
                        <el-image :src="image.photoUrl" fit="cover" />
                        <div class="input-container">
                            <div class="input-text">图片名称：</div>
                            <el-input v-model="image.photoName" placeholder="可输入图片名称" />
                        </div>
                    </div>
                    <div class="upload-container">
                        <el-upload class="cover-upload" :on-change="onSelectImage" :show-file-list="false"
                            :before-upload="beforeAvatarUpload" :auto-upload="false">
                            <el-icon class="avatar-uploader-icon">
                                <Plus />
                            </el-icon>
                        </el-upload>
                    </div>
                    <div class="button-container">
                        <el-button v-if="photoList.length > 0" type="primary" @click="uploadImage()">上传图片</el-button>
                    </div>
                </div>
            </div>
            <el-divider content-position="left">图片列表</el-divider>
            <div class="uploading-image">
                <div v-for="image in album.photoList" class="image-item">
                    <el-image :src="image.photoUrl" fit="cover"/>
                </div>
            </div>

        </div>

        <el-empty v-else description="请选择相册"></el-empty>
    </page-container>
    <el-dialog v-model="dialogVisible" title="修改相册" :lock-scroll="false" width="500" center class="addAlbum">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="相册名称" prop="albumName">
                <el-input v-model="form.albumName" placeholder="请输入相册名称" />
            </el-form-item>
            <el-form-item label="相册描述" prop="albumDesc">
                <el-input v-model="form.albumDesc" placeholder="请输入相册描述" />
            </el-form-item>
            <el-form-item label="相册封面" prop="albumCover">
                <el-upload class="cover-upload" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                    :show-file-list="false" :auto-upload="true" :http-request="customUpload">
                    <el-image v-if="form.albumCover" :src="form.albumCover" class="avatar" fit="cover" />
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
    <el-dialog v-model="isSubmit">
        <el-progress v-if="uploadProgress < 100" :percentage="uploadProgress" :indeterminate="true" />
        <el-progress v-else status="success" :percentage="uploadProgress" />
    </el-dialog>
</template>

<script lang="ts" setup>
import { addAlbumService, addPhotoToAlbumService, getAlbumByIdService, getAlbumListService, uploadAlbumCoverService } from '@/api/album';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { Album, SimpleAlbumDTO, LocalPhotoTempData } from '@/type/album';
import { ElMessage, type UploadProps } from 'element-plus';
import { onMounted, reactive, ref, watch } from 'vue';

const prop = ref()
const album = ref<Album>({
    id: 0,
    albumName: '',
    albumDesc: '',
    albumCover: '',
    userId: 0,
    nickname: '',
    createTime: '',
    photoList: []
})
const albumList: SimpleAlbumDTO[] = reactive([])
const selectAlbum = ref()
const loading = ref(false)
const dialogVisible = ref(false)
const uploadProgress = ref(0)
const isSubmit = ref(false)
const formRef = ref()
const form = ref({
    id: 0,
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
const photoList = reactive<LocalPhotoTempData[]>([])
const updloadPhotoId = ref(0)
const getAlbum = async (albumId: number) => {
    loading.value = true
    const res = await getAlbumByIdService(albumId)
    album.value = res.data.data
    loading.value = false
}

const getAlbumList = async () => {
    clearAlbum()
    const res = await getAlbumListService()
    for (const r of res.data.data) {
        albumList.push(r)
    }

}

const onEditAlbum = (row: any) => {

    form.value.id = row.id
    form.value.albumDesc = row.albumDesc
    form.value.albumName = row.albumName
    form.value.albumCover = row.albumCover
    dialogVisible.value = true

}

const submitForm = async () => {
    console.log(checkAlbum())
    if (await checkAlbum()) {
        loading.value = true
        await addAlbumService({
            id: form.value.id,
            albumName: form.value.albumName,
            albumDesc: form.value.albumDesc,
            albumCover: form.value.albumCover
        })
        ElMessage.success("更新成功")
        getAlbum(form.value.id)
        dialogVisible.value = false
        form.value.albumCover = ''
        form.value.albumDesc = ''
        form.value.albumName = ''
    }

}

const uploadImage = async () => {
    isSubmit.value = true;
    // 复制一份 photoList 作为上传的数据
    const uploadList = [...photoList];
    let count = 100 / uploadList.length;

    for (const image of uploadList) {
        const formData = new FormData();
        formData.append('image', image.imageFile);

        try {
            const response = await uploadAlbumCoverService(formData);
            image.photoUrl = response.data.data;
        } catch (error) {
            ElMessage.error('上传失败，请重试！');
            return;
        }

        uploadProgress.value = uploadProgress.value + count;

        // 如果 photoName 为空，使用文件原始名
        const photoName = image.photoName || image.fileName;

        const res = await addPhotoToAlbumService({
            id: image.id,
            albumId: image.albumId,
            photoName: photoName,
            photoUrl: image.photoUrl
        });

        let id = res.data.data;
        album.value.photoList.push({
            id: Number(id),
            photoName: photoName,
            photoUrl: image.photoUrl,
            albumId: image.albumId,
            createTime: '',
            userId: 0,
            nickname: '',
            isDel: 0
        });

        // 从原始 photoList 中删除已上传的文件
        const index = photoList.indexOf(image);
        if (index !== -1) {
            photoList.splice(index, 1);
        }
    }
    ElMessage.success("添加成功");
    uploadProgress.value = 0;
    isSubmit.value = false;
};

const checkAlbum = async () => {
    let bool = false
    await formRef.value.validate((valid: any) => {
        if (valid) {
            bool = true
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
// 上传成功回调
const handleAvatarSuccess: UploadProps['onSuccess'] = () => {
    ElMessage.success("上传成功")
};
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

const onSelectImage = (uploadFile: any) => {
    console.log(uploadFile)
    const { name: originalName, raw } = uploadFile;
    const fileName = originalName.replace(/\.[^.]+$/, '');
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];
    const maxSize = 10 * 1024 * 1024;
    if (!allowedTypes.includes(raw.type)) {
        ElMessage.error('文件格式不符，请使用 jpg、png 格式文件');
        return;
    }
    if (raw.size > maxSize) {
        ElMessage.error('文件大于 10MB!');
        return;
    }
    photoList.push({
        id: updloadPhotoId.value++,
        albumId: album.value.id,
        photoName: '',
        photoUrl: URL.createObjectURL(raw),
        imageFile: raw,
        fileName
    });
    updloadPhotoId.value += 1;
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

watch(
    () => selectAlbum.value,
    (newVal) => {
        if (newVal) {
            // 获取选中的相册 ID
            const selectedAlbumId = albumList.find((album: SimpleAlbumDTO) => album.albumName === newVal)?.id;
            console.log(selectedAlbumId)
            if (selectedAlbumId !== undefined) {
                getAlbum(selectedAlbumId);
            }
        }
    }
);

onMounted(() => {
    getAlbumList()
    // getAlbum(0)
})
</script>


<style lang="scss" scoped>
.data {
    height: 80vh;

    .table {
        margin-bottom: 12px;
    }
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

.uploading-image {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    border-radius: 6px;
    transition: border-color .3s;
    margin-top: 5px;
    margin-bottom: 10px;
}

.image-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 128px;
}

.el-image {
    width: 128px;
    height: 128px;
    margin-bottom: 5px;
}

.input-container {
    display: flex;
    flex-direction: column;
    width: 128px;
}

.input-text {
    font-size: 12px;
    margin-bottom: 5px;
    text-align: center;
}

.el-input {
    width: 128px;
}

.upload-container {
    width: 128px;
    display: flex;
    align-items: flex-start;
}

.button-container {
    width: 128px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.cover-upload {

    width: 128px;
    height: 128px;
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
        width: 128px;
        height: 128px;
        text-align: center;
    }

}
</style>