<template>

    <!-- <Search></Search> -->
    <page-container title="文章数据管理">
        <template #search>
            <Search v-on:on-search="searchData"></Search>
        </template>

        <template #extra>
            <el-button @click="toAdvancedSearch">高级搜索</el-button>
            <el-button @click="toAddArticle">添加新的文章</el-button>
        </template>

        <el-table v-loading="loading" :data="articleList" height="100%" class="table">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="readCount" label="浏览量"></el-table-column>
            <el-table-column prop="" label="点赞数">
                <template #default="{ row }">
                    <div class="item-pointer" @click="openDialog(row.id, 'like')">{{ row.likeCount }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="" label="评论数">
                <template #default="{ row }">
                    <div class="item-pointer" @click="openDialog(row.id, 'comment')">{{ row.commentCount }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
            <el-table-column prop="category" label="文章分类">
                <template #default="{ row }">
                    {{ row.category?.categoryName || '无分类' }}
                </template>
            </el-table-column>
            <el-table-column prop="" label="文章标签">
                <template #default="{ row }">
                    <div class="tag-info">
                        <el-tag type="primary" v-for="tag in row.tagList">{{ tag.tagName }}</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="" label="创建者头像" width="100">
                <template #default="{ row }">
                    <el-tooltip>
                        <template #content>
                            作者ID：{{ row.id }}<br />
                            作者昵称：{{ row.nickname }}
                        </template>
                        <div class="centered-avatar">
                            <el-avatar :size="50" :src="row.userAvatar" />
                        </div>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column prop="" label="状态" width="80">
                <template #default="{ row }">
                    <el-tag type="primary" v-if="row.state === 0">正常</el-tag>
                    <el-tag type="danger" v-else-if="row.state === 1">删除</el-tag>
                    <el-tag type="success" v-else-if="row.state === 3">保存</el-tag>
                    <el-tag type="primary" v-if="row.isOriginal === 1">原创</el-tag>
                    <el-tag type="success" v-if="row.isTop === 1">置顶</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="toEditArticle(row, $index)"><el-icon>
                            <Edit />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.isTop === 0" circle plain type="success"
                        @click="onUpdateArticleTop(row.id, row.isTop)"><el-icon>
                            <Top />
                        </el-icon>
                    </el-button>
                    <el-button v-else circle plain type="danger"
                        @click="onUpdateArticleTop(row.id, row.isTop)"><el-icon>
                            <Bottom />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.state !== 1" circle plain type="danger"
                        @click="onDelArticle(row.id, $index)"><el-icon>
                            <Delete />
                        </el-icon>
                    </el-button>
                    <el-button v-else circle plain type="warning" @click="onRegainArticle(row.id, $index)"><el-icon>
                            <Check />
                        </el-icon>
                    </el-button>

                </template>
            </el-table-column>

            <template #empty>
                <el-empty description="没有数据"></el-empty>
            </template>
        </el-table>


        <template #pagination>
            <div class="table-pagination">
                <el-pagination v-model:current-page="page" :disabled="disabled" :background="background"
                    :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </template>

    </page-container>




    <el-dialog v-model="isComment" :title="`文章 ${articleId} 评论列表`" :lockScroll="false" width="700" center
        class="dialog">
        <div class="dialog-table">
            <ArticleTable :comment-list="comment.commentList" :loading="loading" v-on:on-del-message="onDelMessage"
                v-on:on-pass-message="onPassMessage">
            </ArticleTable>
            <div class="pagination">
                <el-pagination v-model:current-page="comment.page" :disabled="disabled" :background="background"
                    :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper"
                    :total="comment.total" @size-change="handleSizeChangeByComment"
                    @current-change="handleCurrentChangeByComment" />
            </div>
        </div>
    </el-dialog>

    <el-dialog v-model="isLike" :title="`文章 ${articleId} 点赞列表`" :lockScroll="false" width="500" center class="dialog">
        <ArticleLikeTable :like="articleLike" v-on:on-handle-current-change="handleCurrentChangeByLike"
            v-on:on-handle-size-change="handleSizeChangeByLike"></ArticleLikeTable>
    </el-dialog>

    <el-dialog v-model="isSearch" title="高级搜索" :lockScroll="false" width="500" center class="dialog">
        <AdvancedSearch :options="searchOptions" @search="handleSearch"></AdvancedSearch>
    </el-dialog>

</template>

<script lang="ts" setup>
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import ArticleTable from '@/components/table/articleTable.vue';
import AdvancedSearch from '@/components/search/AdvancedSearch.vue';
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import { useUserStore } from '@/stores';
import {
    delArticleService, getArticleLikeListByPageService, getArticleListByPageService, regainArticleService,
    searchArticleCategoryListService, searchArticleTagListService, searchArticleTitleListService,
    searchUserListService, simpleSearchArticleService, updateArticleTopService,
    searchArticleService
} from '@/api/article';
import type { ArticleLikeTableVO, ArticleVO } from '@/type/article';
import { useRouter } from 'vue-router';
import {
    delArticleCommentService, getArticleCommentListByIdAndPageService,
    updateArticleCommentService
} from '@/api/comment';
import ArticleLikeTable from '@/components/table/ArticleLikeTable.vue';
import Search from '@/components/search/Search.vue';
import dayjs from 'dayjs'

const router = useRouter()
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const articleList: ArticleVO[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const userState = useUserStore()
const isComment = ref(false)
const isLike = ref(false)
const isSearch = ref(false)
const articleId = ref(0)

type Option =
    | {
        value: string
        label: string
        type: 'Date' | 'Select'
        opt?: { label: string; value: string }[]
        fn?: never
    }
    | {
        value: string
        label: string
        type: 'Autocomplete'
        fn: (q: string, cb: any) => void
        opt?: never
    }

const searchOptions: Option[] = reactive([
    {
        value: 'title', label: '标题', type: 'Autocomplete',
        fn: async (q: string, cb: (arg0: any) => void) => {
            const res = await searchArticleTitleListService(q)
            cb(res.data.data.map((i: any) => ({ value: i.value })))
        }
    },
    { value: 'date', label: '日期', type: 'Date' },
    {
        value: 'state', label: '状态', type: 'Select',
        opt: [{ label: '正常', value: '0' }, { label: '删除', value: '1' }, { label: '保存', value: '3' }]
    },
    {
        value: 'nickname', label: '用户', type: 'Autocomplete',
        fn: async (q: string, cb: (arg0: any) => void) => {
            const res = await searchUserListService(q)
            cb(res.data.data.map((i: any) => ({ value: i.value })))
        }
    },
    {
        value: 'tag', label: '标签', type: 'Autocomplete',
        fn: async (q: string, cb: (arg0: any) => void) => {
            const res = await searchArticleTagListService(q)
            cb(res.data.data.map((i: any) => ({ value: i.value })))
        }
    },
    {
        value: 'category', label: '分类', type: 'Autocomplete',
        fn: async (q: string, cb: (arg0: any) => void) => {
            const res = await searchArticleCategoryListService(q)
            cb(res.data.data.map((i: any) => ({ value: i.value })))
        }
    },
    {
        value: 'isTop', label: '置顶状态', type: 'Select',
        opt: [{ label: '置顶', value: '1' }, { label: '非置顶', value: '0' }]
    },
    {
        value: 'isOriginal', label: '编写类型', type: 'Select',
        opt: [{ label: '原创', value: '1' }, { label: '转载', value: '0' }]
    }
])

const handleSearch = async (form: any) => {
    if (!form) return;
    if (form.date && form.date.length === 2) {
        const [start, end] = form.date
        const adjustedStart = dayjs(start).startOf('day').toDate()
        const adjustedEnd = dayjs(end).endOf('day').toDate()
        form.date = [adjustedStart, adjustedEnd]
    }
    // form 里只有已填写且非空的字段，直接当参数发请求即可
    loading.value = true
    isSearch.value = false
    form.page = page.value
    form.pageSize = pageSize.value
    const res = await searchArticleService(form)
    clearArticle()
    total.value = res.data.data.total
    for (const item of res.data.data.data) {
        if (item.imageUrl) {
            item.imageUrl = item.imageUrl.split('#').filter((url: string) => url !== '');
            console.log(item.imageUrl)
        }
        articleList.push(item);
    }
    loading.value = false

}

//文章列表相关
const getArticleList = async () => {
    loading.value = true
    clearArticle()
    const res = await getArticleListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const item of res.data.data.data) {
        if (item.imageUrl) {
            item.imageUrl = item.imageUrl.split('#').filter((url: string) => url !== '');
            console.log(item.imageUrl)
        }
        articleList.push(item);
    }
    loading.value = false
}

const handleCurrentChange = () => {
    getArticleList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
    getArticleList()
}
const onDelArticle = async (id: any, index: any) => {
    await delArticleService(id);
    ElMessage.success("删除成功")
    getArticleList()
}
const onRegainArticle = async (id: any, index: any) => {
    await regainArticleService(id);
    ElMessage.success("复原成功")
    getArticleList()
}
const onUpdateArticleTop = async (id: number, isTop: number) => {
    if (isTop === 0) {
        await updateArticleTopService(id, 1)
        ElMessage.success("置顶成功")
    } else {
        await updateArticleTopService(id, 0)
        ElMessage.success("取消置顶成功")
    }
    getArticleList()
}

const toEditArticle = (row: any, index: any) => {
    const path = '/article/write';

    const menuExists = userState.menuList.some(menu =>
        menu.path === path ||
        (menu.children && menu.children.some(child => child.path === path))
    );

    if (menuExists) {
        console.log(row.id)
        router.push({
            path: path,
            query: { id: row.id }
        })
    } else {
        ElMessage.warning('您没有访问此页面的权限');
    }

}

const toAddArticle = () => {
    const path = '/article/write';

    const menuExists = userState.menuList.some(menu =>
        menu.path === path ||
        (menu.children && menu.children.some(child => child.path === path))
    );

    if (menuExists) {
        router.push({ path: path })
    } else {
        ElMessage.warning('您没有访问此页面的权限');
    }
}
const clearArticle = () => {
    if (articleList.length > 0) {
        articleList.splice(0, articleList.length)
    }
}

const toAdvancedSearch = () => {
    isSearch.value = true
}

//文章评论相关
const comment = reactive({
    commentList: [],
    total: 0,
    page: 1,
    pageSize: 10,
    loading: false
})
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
const getMessageList = async () => {
    comment.loading = true
    clearMessage()
    const res = await getArticleCommentListByIdAndPageService(articleId.value, comment.page, comment.pageSize)
    comment.total = res.data.data.total
    comment.commentList = res.data.data.data
    comment.loading = false
}
const clearMessage = () => {
    if (comment.commentList.length > 0) {
        comment.commentList.splice(0, comment.commentList.length)
    }
}
const handleCurrentChangeByComment = () => {
    getMessageList()
}
const handleSizeChangeByComment = (val: number) => {
    comment.pageSize = val
    getMessageList()
}

const openDialog = (id: number, tag: 'like' | 'comment') => {
    articleId.value = id
    if (tag === 'like') {
        isLike.value = true
        fetchData()
    } else if (tag === 'comment') {
        isComment.value = true
        getMessageList()
    }
}

//文章点赞相关
const articleLike = reactive<ArticleLikeTableVO>({
    page: 1,
    pageSize: 10,
    total: 0,
    list: [],
    loading: false
})

const fetchData = async () => {
    loading.value = true
    clearLike()
    const res = await getArticleLikeListByPageService(articleId.value, articleLike.page, articleLike.pageSize)
    articleLike.total = res.data.data.total
    articleLike.list = res.data.data.data
    loading.value = false
}

const handleCurrentChangeByLike = (val: number) => {
    fetchData()
}
const handleSizeChangeByLike = (val: number) => {
    articleLike.pageSize = val
    fetchData()
}

const clearLike = () => {
    if (articleLike.list.length > 0) {
        articleLike.list.splice(0, articleLike.list.length)
    }
}

const searchData = async (data: { title: string; date: string[] | Date[]; state: string }) => {
    loading.value = true
    const res = await simpleSearchArticleService({
        date: data.date,
        title: data.title,
        state: data.state,
        page: page.value,
        pageSize: pageSize.value
    })
    clearArticle()
    total.value = res.data.data.total
    for (const item of res.data.data.data) {
        if (item.imageUrl) {
            item.imageUrl = item.imageUrl.split('#').filter((url: string) => url !== '');
            console.log(item.imageUrl)
        }
        articleList.push(item);
    }
    loading.value = false
}

onMounted(() => {
    getArticleList()
    console.log(articleList.at(1)?.category.categoryName)
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

.item-pointer {
    cursor: pointer;
    color: #409EFF;
}

.table-pagination {
    display: flex;
    justify-content: flex-end;
}

.tag-info {
    display: flex;
    align-items: start;
    flex-direction: column;
}

.pagination {
    margin-top: auto;
    padding-top: 12px;
    background: #fff;
    display: flex;
    justify-content: flex-end;
}
</style>