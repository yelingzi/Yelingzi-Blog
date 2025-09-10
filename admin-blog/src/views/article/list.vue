<template>

    <page-container title="文章管理">
        <template #extra>
            <el-button @click="toAddArticle">添加新的文章</el-button>
        </template>

        <el-table v-loading="loading" :data="articleList" style="width: 100%" class="table">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="" label="文章封面" width="200">
                <template #default="{ row }">
                    <el-image class="article-image" :src="row.articleCover" fit="cover" />
                </template>
            </el-table-column>
            <el-table-column prop="category" label="文章分类">
                <template #default="{ row }">
                    {{ row.category?.categoryName || '无分类' }}
                </template>
            </el-table-column>
            <el-table-column prop="" label="文章标签">
                <template #default="{ row }">
                    <el-tag type="info" v-for="tag in row.tagList">{{ tag.tagName }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170"></el-table-column>
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
                    <el-button v-else circle plain type="warning"
                        @click="onRegainArticle(row.id, $index)"><el-icon>
                            <Check />
                        </el-icon>
                    </el-button>

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

</template>

<script lang="ts" setup>
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref} from 'vue';
import { useUserStore } from '@/stores';
import { delArticleService, getArticleListByPageService, regainArticleService, updateArticleTopService } from '@/api/article';
import type { ArticleVO } from '@/type/article';
import { useRouter } from 'vue-router';

const router = useRouter()
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const articleList: ArticleVO[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const userState = useUserStore()

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
onMounted(() => {
    getArticleList()
    console.log(articleList.at(1)?.category.categoryName)
})
</script>


<style lang="scss" scoped>
.table {
    height: 76vh;
}

.pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
}

.article-image {
    width: 160px;
    height: 100px;
}
.centered-avatar {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}
</style>