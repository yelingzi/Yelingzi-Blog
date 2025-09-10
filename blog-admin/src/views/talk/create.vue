<template>
    <div class="container">
        <el-card v-loading="loading" element-loading-text="uploading...">
            <el-text v-if="talkId === 0">发布说说</el-text>
            <el-text v-else>编辑说说：{{ talkId }}</el-text>
            <el-text>{{  }}</el-text>
            <input v-model="title" class="title-input" placeholder="起一个好的标题更能吸引人"></input>
            <textarea v-model="content" class="content-input" placeholder="有什么想和大家分享的？" @input="autoResize"
                ref="textArea"></textarea>
            <div class="upload-container">
                <el-upload class="upload-image" v-model:file-list="fileList" :auto-upload="false"
                    list-type="picture-card" :before-upload="beforeAvatarUpload" :on-preview="handlePictureCardPreview"
                    :on-remove="handleRemove" :on-change="onSelectImage">

                    <el-icon class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </div>

            <div class="footer-content">
                <div>表情占位</div>
                <div class="right-content">
                    <el-checkbox class="top" v-model="isTop">置顶</el-checkbox>
                    <el-button class="footer-button" @click="save()">保存</el-button>
                    <el-button v-if="isSubmit" class="footer-button" type="primary">正在上传...</el-button>
                    <el-button v-else class="footer-button" type="primary" @click="submit()">发布</el-button>
                </div>
            </div>
        </el-card>
    </div>
    <el-dialog v-model="dialogVisible">
        <img :src="dialogImageUrl" alt="Preview Image" />
    </el-dialog>
    <el-dialog v-model="isSubmit">
        <el-progress v-if="uploadProgress < 100" :percentage="uploadProgress" :indeterminate="true" />
        <el-progress v-else status="success" :percentage="uploadProgress" />
    </el-dialog>
</template>

<script lang="ts" setup>
import { addTalkService, editTalkService, getTalkByIdService, uploadTalkImageService } from '@/api/talk';
import type { TalkVO } from '@/type/talk';
import { booleanToNumber, numberToBoolean } from '@/utils/commom';
import { ElMessage, type UploadProps, type UploadUserFile } from 'element-plus';
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router'

const route = useRoute()
const title = ref('');
const content = ref('');
const imageUrl = ref('')
const textArea = ref<HTMLTextAreaElement | null>(null);
const fileList = ref<UploadUserFile[]>([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const image = ref<any[]>([])
const isTop = ref(false)
const isSubmit = ref(false)
const loading = ref(false)
const uploadProgress = ref(0)

// 响应式获取ID（自动处理类型转换）
const talkId = computed(() => {
    const id = route.query.id
    return id ? Number(id) : 0
})


const talkData = ref<TalkVO>({
    id: 0,
    title: '',
    content: '',
    imageUrl: [],
    isTop: 0,
    state: 0,
    createTime: new Date().toISOString(),
    userId: 0,
    nickname: '',
    userAvatar: '',
    likeCount: 0,
    commentCount: 0,
})
//方法
const save = () => {
    if (checkSubmit()) {
        uploadTalk(0, 2)
    }
}
const submit = () => {
    if (checkSubmit()) {
        uploadTalk(0, 0)
    }

}

const resetForm = () => {
    title.value = ''
    content.value = ''
    imageUrl.value = ''
    image.value = []
    fileList.value = []
}

const uploadTalk = async (id: number, state: number) => {
    loading.value = true
    isSubmit.value = true
    let imgurl = ''
    try {
        for (const file of image.value) {
            const fd = new FormData()
            fd.append('image', file);
            const res = await uploadTalkImageService(fd)
            imgurl = imgurl + res.data.data + '#'
        }
        if (talkData.value.id === 0) {
            imageUrl.value = imgurl
            await addTalkService({
                id: id, title: title.value, content: content.value, imageUrl:
                    imageUrl.value, isTop: booleanToNumber(isTop.value), state: state
            });
        } else {
            let url = ''
            for (const str of talkData.value.imageUrl) {
                url = url + str + '#'
            }
            imageUrl.value = imgurl
            await editTalkService({
                id: talkData.value.id, title: title.value, content: content.value, imageUrl:
                    imageUrl.value, isTop: booleanToNumber(isTop.value), state: state
            });
        }
        loading.value = false
        ElMessage.success('发布成功！')
        isSubmit.value = false
        resetForm()
    } catch (error) {
        loading.value = false
        isSubmit.value = false
    }
}

const checkSubmit = () => {
    if (content.value === '') {
        ElMessage.error('发布或保存的内容不能为空')
        return false
    }
    return true
}

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

const onSelectImage = (uploadFiles: any) => {
    const url = URL.createObjectURL(uploadFiles.raw);
    image.value.push(uploadFiles.raw)
    fileList.value.push({ 
      name: uploadFiles.name, 
      url: url,
      uid: uploadFiles.uid 
    })
}

const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
  // 从 fileList 中移除
  fileList.value = fileList.value.filter(file => file.uid !== uploadFile.uid)
  
  // 从 image 数组中移除对应的原始文件
  if (uploadFile.raw) {
    image.value = image.value.filter(img => {
      // 比较文件名和大小，因为原始 File 对象没有 uid
      return img.name !== uploadFile.name || img.size !== uploadFile.size
    })
  }
}
const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
    dialogImageUrl.value = uploadFile.url!
    dialogVisible.value = true
}

// 自动调整文本域高度
const autoResize = () => {
    if (textArea.value) {
        textArea.value.style.height = 'auto'; // 重置高度
        textArea.value.style.height = `${textArea.value.scrollHeight}px`; // 设置为内容高度
    }
};



const fetchData = async (id: number) => {
    loading.value = true
    try {
        const res = await getTalkByIdService(id)
        talkData.value = res.data.data
        title.value = talkData.value.title
        content.value = talkData.value.content
        for (const str of talkData.value.imageUrl) {
            fileList.value.push({ name: str, url: str })
        }
        isTop.value = numberToBoolean(talkData.value.isTop)
    } finally {
        loading.value = false
    }
}

// 监听ID变化获取数据
watch(talkId, (newId) => {
    if (newId) {
        fetchData(newId)
    }
}, { immediate: true })

// 初始化时调整高度
onMounted(() => {
    if (textArea.value) {
        textArea.value.style.height = `${textArea.value.scrollHeight}px`;
    }
});
</script>

<style lang="scss" scoped>
.container {
    height: 80%;

    .el-card {
        margin: auto 25%;
    }
}

.title-input {
    word-wrap: break-word;
    box-sizing: border-box;
    letter-spacing: 1px;
    outline: none;
    padding-right: 5px;
    position: relative;
    vertical-align: baseline;
    white-space: pre-wrap;
    width: 100%;
    word-break: break-all;
    border: none;
    font-family: inherit;
    font-size: inherit;
}

.content-input {
    word-wrap: break-word;
    box-sizing: border-box;
    letter-spacing: 1px;
    outline: none;
    padding-right: 5px;
    position: relative;
    vertical-align: baseline;
    white-space: pre-wrap;
    width: 100%;
    word-break: break-all;
    border: none;
    font-family: inherit;
    font-size: inherit;
    overflow: hidden;
    resize: none;
}

.upload-container {
    display: flex;
    align-items: flex-start;
}

.upload-image {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    border-radius: 6px;
    transition: border-color .3s;
    margin-top: 5px;

    &:hover {
        border-color: var(--color-blue);
    }

    .el-upload-list__item {
        width: 128px;
        height: 128px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .el-icon.avatar-uploader-icon {
        font-size: 48px;
        color: #8c939d;
        width: 128px;
        height: 128px;
        text-align: center;
    }
}

.footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    margin-top: 30px;

    .top {
        margin-right: 20px;
    }

    .right-content {
        display: flex;
        align-items: center;
        margin-left: auto;
    }

    .footer-button {
        width: 100px;
    }
}
</style>