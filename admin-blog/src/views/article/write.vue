<!-- /views/article/write -->
<template>
    <el-card shadow="never" class="container">
        <template #header>
            <div class="header-content">
                <span v-if="articleId === 0">发布文章</span>
                <span v-else>编辑文章：{{ article.title }}</span>
                <div>
                    <el-button @click="openHtmlText">常用HTML标签</el-button>
                    <el-button @click="clearContent">清空</el-button>
                    <el-button @click="setsetArticle">保存文章</el-button>
                </div>
            </div>
        </template>
        <md-editor v-model="articleContent" @onUploadImg="handleUploadImage" :showToolbarName="true" class="md-edit" />
    </el-card>
    <el-dialog v-model="showFormDialog" title="文章设置" width="50%">
        <div class="article-form">
            <el-form :model="form" label-width="120px" :rules="rules" ref="articleForm">
                <el-form-item label="标题" prop="title" class="form-item">
                    <el-input v-model="form.title" placeholder="请输入标题" />
                </el-form-item>
                <el-form-item label="文章分类" prop="category" class="form-item">
                    <el-select v-model="form.category" placeholder="请选择分类">
                        <el-option v-for="category of categoryList" :label="category.categoryName"
                            :value="category.categoryName" />
                    </el-select>
                </el-form-item>
                <el-form-item label="文章标签" prop="tag">
                    <div class="article-tag">
                        <el-tag v-for="tag in form.tag" :key="tag" closable :disable-transitions="false"
                            @close="handleClose(tag)" effect="plain" class="tag" size="large">
                            {{ tag.tagName }}
                        </el-tag>
                        <el-button @click="tagDialogVisible = true">
                            + New Tag
                        </el-button>
                    </div>
                </el-form-item>
                <el-form-item label="文章封面" prop="articleCover">
                    <el-upload class="cover-upload" :on-success="handleAvatarSuccess"
                        :before-upload="beforeAvatarUpload" :show-file-list="false" :auto-upload="true"
                        :http-request="customUpload">
                        <el-image v-if="imageUrl" :src="imageUrl" class="cover" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>

                <el-form-item label="是否原创">
                    <el-radio-group v-model="formRadio">
                        <el-radio :value="1">是</el-radio>
                        <el-radio :value="0">否
                            <el-input v-if="formRadio === 0" v-model="originalUrl" placeholder="请输入转载文章链接" />
                        </el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="是否顶置">
                    <el-checkbox v-model="isTop">置顶</el-checkbox>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="addArticle()">发布文章</el-button>
                    <el-button @click="saveArticle">保存文章</el-button>
                </el-form-item>
            </el-form>
        </div>
    </el-dialog>
    <el-dialog v-model="tagDialogVisible" title="选择标签" width="640" center class="addTags" :lockScroll="false">
        <div class="tag-container">
            <el-check-tag v-for="tag in tagList" :key="tag" effect="plain" class="tags" :checked="tag.check"
                type="primary" @change="onChangeTag(tag)">
                {{ tag.tagName }}
            </el-check-tag>
        </div>
        <div class="input-button-container">
            <el-input v-if="inputVisible" ref="InputRef" v-model="inputValue" class="w-20"
                @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
            <el-button v-else class="button-new-tag" @click="showInput">
                + 添加新的标签
            </el-button>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="tagDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="selectTag">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>

    <el-dialog v-model="htmlText" title="常用HTML标签" width="640" center :lockScroll="false">
        <openHtml></openHtml>
    </el-dialog>

</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { ElMessage, type InputInstance, type UploadProps } from 'element-plus';
import { articleAddService, getArticleByIdService, updateArticleService, uploadArticleCoverService, uploadArticleImageService } from '@/api/article';
import type { Article, ArticleForm, Category, CheckTags, Tags } from '@/type/article';
import { addTagService, getTagListService } from '@/api/tag';
import { getCategoryListService } from '@/api/category';
import { booleanToNumber, numberToBoolean } from '@/utils/commom';
import { useRoute } from 'vue-router'
import openHtml from '@/components/HtmlTag/openHtml.vue';

const route = useRoute()
const articleId = computed(() => {
    const id = route.query.id
    return id ? Number(id) : 0
})
const article = ref<Article>({
    id: 0,
    title: '',
    category: { id: 0, categoryName: '' },
    tagList: [],
    createTime: '',
    content: '',
    isOriginal: 0,
    userId: 0,
    originalUrl: '',
    nickname: '',
    userAvatar: '',
    isTop: 0,
    state: 0,
    articleCover: '',
})
const articleContent = ref<string>('# 新文章\n在这里编写你的 Markdown 内容...');
const imageUrl = ref()
const categoryList: Category[] = reactive([])
const inputValue = ref('')
const tagList = ref<CheckTags[]>([])
const inputVisible = ref(false)
const InputRef = ref<InputInstance>()
const articleForm = ref()
const tagDialogVisible = ref(false)
const formRadio = ref(1)
const isTop = ref(false)
const originalUrl = ref('')
const showFormDialog = ref(false)
const htmlText = ref(false)
const form = ref<ArticleForm>({
    title: '',
    category: '',
    tag: [],
    articleCover: ''
})
const rules = ({
    title: [
        {
            required: true,
            message: '请输入标题',
            trigger: 'blur'
        },
        {
            pattern: /^\S{1,96}$/,
            message: '标题必须是1-96非空字符',
            trigger: 'blur'
        }
    ],
    category: [
        {
            required: true,
            message: '请选择分类',
            trigger: 'change'
        }
    ],
    articleCover: [
        {
            required: true,
            message: '封面不能为空',
            trigger: 'change'
        }
    ],
    tag: [
        {
            required: true,
            message: '标签不能为空',
            trigger: 'change'
        }
    ],
})

const handleUploadImage = async (files: any, callback: (arg0: any[]) => void) => {
    const urls = [];
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    const maxSize = 5 * 1024 * 1024;
    // 校验每个文件
    for (const file of files) {
        if (!allowedTypes.includes(file.type)) {
            ElMessage.error('文件格式不符，请使用 jpg、png 格式文件');
            return;
        }
        if (file.size > maxSize) {
            ElMessage.error('文件大于 2MB!');
            return;
        }
    }

    // 如果校验通过，继续上传
    for (const file of files) {
        const formData = new FormData();
        formData.append('image', file);

        try {
            const response = await uploadArticleImageService(formData);
            urls.push(response.data.data);
        } catch (error) {
            ElMessage.error('上传失败，请重试！');
            return;
        }
    }

    // 调用回调函数，传递上传成功的图片 URL 数组
    console.log(urls)
    callback(urls)
};

// 保存文章
const saveArticle = async () => {
    if (await checkArticle()) {
        await uploadArticle(2)
        ElMessage.success('文章保存成功');
    }

};

//设置文章属性
const setsetArticle = () => {
    showFormDialog.value = true
}
// 自定义上传逻辑
const customUpload: UploadProps['httpRequest'] = async (options) => {
    const { file } = options;
    const formData = new FormData();
    formData.append('image', file);

    try {
        const response = await uploadArticleCoverService(formData);
        imageUrl.value = response.data.data;
        form.value.articleCover = imageUrl.value
        if (response.data.code === 1) {
            ElMessage.success("上传成功")
        } else {
            ElMessage.error('上传失败，请重试！');
        }
    } catch (error) {

    }
};

// 上传成功回调
const handleAvatarSuccess: UploadProps['onSuccess'] = () => {

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

// 清空内容
const clearContent = () => {
    articleContent.value = '# 新文章\n在这里编写你的 Markdown 内容...';
};

const clearData = () => {
    form.value.articleCover = ''
    form.value.category = ''
    form.value.tag = []
    form.value.title = ''
    articleContent.value = '# 新文章\n在这里编写你的 Markdown 内容...';
}

const checkArticle = async () => {
    let isValid = false;
    await articleForm.value.validate(async (valid: boolean) => {
        if (articleContent.value == "") {
            ElMessage.error('请完善文章内容后再提交!');
            return;
        }
        if (valid) {
            isValid = true;
        } else {
            ElMessage.error('请完善文章内容后再提交~');
        }
    });
    return isValid;
};

const uploadArticle = async (state: number) => {

    let tagString = ''
    let categoryId = 0
    for (const t of form.value.tag) {
        tagString = tagString + t.id + "%%"
    }
    for (const t of categoryList) {
        if (t.categoryName === form.value.category) {
            categoryId = t.id
        }
    }

    if (articleId.value === 0) {
        await articleAddService({
            id: 0,
            title: form.value.title,
            content: articleContent.value,
            articleCover: imageUrl.value,
            category: categoryId,
            tagList: tagString,
            isOriginal: formRadio.value,
            originalUrl: formRadio.value === 0 ? originalUrl.value : '',
            state: state,
            isTop: booleanToNumber(isTop.value)
        })
    } else {
        await updateArticleService({
            id: articleId.value,
            title: form.value.title,
            content: articleContent.value,
            articleCover: imageUrl.value,
            category: categoryId,
            tagList: tagString,
            isOriginal: formRadio.value,
            originalUrl: formRadio.value === 0 ? originalUrl.value : '',
            state: state,
            isTop: booleanToNumber(isTop.value)
        })
    }

}

const addArticle = async () => {
    if (await checkArticle()) {
        await uploadArticle(0)
        ElMessage.success('文章发布成功')
    }
    clearData()
    showFormDialog.value = false
}

const onChangeTag = (tag: CheckTags) => {
    tag.check = !tag.check
}

const selectTag = () => {
    if (form.value.tag.length > 0) {
        form.value.tag.splice(0, form.value.tag.length)
    }

    for (const tag of tagList.value) {
        if (tag.check) {
            form.value.tag.push({ id: tag.id, tagName: tag.tagName })
        }
    }
    tagDialogVisible.value = false
}

const handleClose = (tag: Tags) => {
    const index = form.value.tag.findIndex(item => item.id === tag.id);
    if (index !== -1) {
        form.value.tag.splice(index, 1);
    }
};

const showInput = () => {
    inputVisible.value = true
    nextTick(() => {
        InputRef.value!.input!.focus()
    })
}

const handleInputConfirm = async () => {
    if (inputValue.value) {
        const res = await addTagService({ id: 0, tagName: inputValue.value })
        tagList.value.push({ id: res.data.data.id, check: false, tagName: res.data.data.tagName })
    }
    inputVisible.value = false
    inputValue.value = ''
}

const getArticleCategory = async () => {
    if (categoryList.length > 0) {
        categoryList.splice(0, categoryList.length)
    }
    const res = await getCategoryListService()
    for (const category of res.data.data) {
        categoryList.push(category)
    }
}

const getArticleTag = async () => {
    if (tagList.value.length > 0) {
        tagList.value.splice(0, tagList.value.length)
    }
    const res = await getTagListService()
    for (const tag of res.data.data) {
        tagList.value.push({ id: tag.id, tagName: tag.tagName, check: false })
    }
}

const openHtmlText = () => {
    htmlText.value = true
}

const fetchData = async (id: number) => {
    const res = await getArticleByIdService(id)
    article.value = res.data.data

    form.value.tag = article.value.tagList
    form.value.articleCover = article.value.articleCover
    form.value.category = article.value.category.categoryName
    form.value.title = article.value.title
    isTop.value = numberToBoolean(article.value.isTop)
    articleContent.value = article.value.content
    originalUrl.value = article.value.originalUrl
    formRadio.value = article.value.isOriginal
    imageUrl.value = article.value.articleCover
}

// 监听ID变化获取数据
watch(articleId, (newId) => {
    if (newId) {
        fetchData(newId)
    }
}, { immediate: true })

onMounted(async () => {
    await getArticleCategory()
    await getArticleTag()
})

</script>

<style lang="scss" scoped>
.header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.container {
    width: 1670 px;

    .md-edit {
        height: 790px;
    }

    :deep(.el-card__body) {
        padding: 0;
    }
}

.article-form {
    margin-top: 20px;
    margin: auto;

    .form-item {
        width: 500px;
    }
}

.article-tag {
    display: flex;
    align-items: center;

    .tag {
        margin-right: 10px;
    }
}

.cover-upload {

    .cover {
        width: 168px;
        height: 168px;
    }


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

.addTags {
    display: flex;
    flex-direction: column;
    align-items: center;

    .tag-container {
        display: flex;
        flex-wrap: wrap;
        justify-content: flex-start;
        width: 100%;
    }

    .tags {
        margin-right: 12px;
        margin-bottom: 12px;
    }

    .input-button-container {
        display: flex;
        justify-content: flex-start;
    }

    .w-20 {
        width: 160px;
    }

    .button-new-tag {
        width: 160px;
    }
}
</style>