<template>

    <page-container title="说说管理">
        <template #extra>
            <el-button @click="toAddTalk">添加新的说说</el-button>
        </template>

        <el-table v-loading="loading" :data="talkList" style="width: 100%" class="table">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="title" label="标题"></el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="" label="图片列表">
                <template #default="{ row }">
                    <div class="table-image-container">
                        <el-image v-for="(image, index) in row.imageUrl" :key="index" :src="image" fit="cover"
                            class="table-image" />
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="userId" label="创建者ID" width="100"></el-table-column>
            <el-table-column prop="nickname" label="创建者昵称"></el-table-column>
            <el-table-column prop="" label="创建者头像" width="100">
                <template #default="{ row }">
                    <el-avatar :size="50" :src="row.userAvatar" />
                </template>
            </el-table-column>
            <el-table-column prop="" label="状态" width="80">
                <template #default="{ row }">
                    <el-tag type="primary" v-if="row.state === 0">正常</el-tag>
                    <el-tag type="danger" v-else-if="row.state === 1">删除</el-tag>
                    <el-tag type="success" v-else-if="row.isTop === 1">置顶</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="toEditTalk(row, $index)"><el-icon>
                            <Edit />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.isTop === 0" circle plain type="success"
                        @click="onUpdateTalkTop(row.id, row.isTop)"><el-icon>
                            <Top />
                        </el-icon>
                    </el-button>
                    <el-button v-else circle plain type="warning"
                        @click="onUpdateTalkTop(row.id, row.isTop)"><el-icon>
                            <Bottom />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.state !== 1" circle plain type="danger"
                        @click="onDelTalk(row.id, $index)"><el-icon>
                            <Delete />
                        </el-icon>
                    </el-button>
                    <el-button v-else circle plain type="warning"
                        @click="onRegainTalk(row.id, $index)"><el-icon>
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
import { delTalkService, getTalkListByPageService, regainTalkService, updateTalkTopService } from '@/api/talk';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { TalkVO } from '@/type/talk';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref, toRaw } from 'vue';
import { useUserStore } from '@/stores';
import { useRouter } from 'vue-router';

const router = useRouter()
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const talkList: TalkVO[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const userState = useUserStore()

const getTalkList = async () => {
    loading.value = true
    clearTalk()
    const res = await getTalkListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const item of res.data.data.data) {
        if (item.imageUrl) {
            item.imageUrl = item.imageUrl.split('#').filter((url: string) => url !== '');
        }
        talkList.push(item);
    }
    loading.value = false
}


const handleCurrentChange = () => {
    getTalkList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
}

const onUpdateTalkTop = async (id: number, isTop: number) => {
    if (isTop === 0) {
        await updateTalkTopService(id, 1)
        ElMessage.success("置顶成功")
    } else {
        await updateTalkTopService(id, 0)
        ElMessage.success("取消置顶成功")
    }
    getTalkList()
}

const onDelTalk = async (id: any, index: any) => {
    await delTalkService(id);
    ElMessage.success("删除成功")
    getTalkList()
}

const onRegainTalk = async (id: any, index: any) => {
    await regainTalkService(id);
    ElMessage.success("复原成功")
    getTalkList()
}

const toEditTalk = (row: any, index: any) => {
    const path = '/talk/create';

    const menuExists = userState.menuList.some(menu =>
        menu.path === path ||
        (menu.children && menu.children.some(child => child.path === path))
    );

    if (menuExists) {
        console.log(row.id)
        router.push({
            path: '/talk/create',
            query: { id: row.id } 
        })
    } else {
        ElMessage.warning('您没有访问此页面的权限');
    }

}
const toAddTalk = () => {
    const path = '/talk/create';

    const menuExists = userState.menuList.some(menu =>
        menu.path === path ||
        (menu.children && menu.children.some(child => child.path === path))
    );

    if (menuExists) {
        router.push(path);
    } else {
        ElMessage.warning('您没有访问此页面的权限');
    }
};

const clearTalk = () => {
    if (talkList.length > 0) {
        talkList.splice(0, talkList.length)
    }
}
onMounted(() => {
    getTalkList()
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

.table-image-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    /* 图片之间的间距 */
}

.table-image {
    width: 50px;
    height: 50px;
    flex-shrink: 0;
    /* 防止图片被压缩 */
}
</style>