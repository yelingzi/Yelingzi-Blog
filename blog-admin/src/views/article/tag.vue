<template>

    <page-container title="文章管理">
        <template #extra>
            <el-button @click="onAddTag">添加新的标签</el-button>
        </template>

        <el-table v-loading="loading" :data="tagList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="tagName" label="标签名"></el-table-column>
            <el-table-column prop="articleCount" label="文章数"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="userId" label="创建者ID"></el-table-column>
            <el-table-column prop="nickname" label="创建者昵称"></el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="onEditTag(row, $index)"><el-icon>
                            <Edit />
                        </el-icon></el-button>
                    <el-button v-if="row.isDel === 0" circle plain type="danger" @click="onDelTag(row.id, $index)"><el-icon>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :lockScroll="false" width="500" center class="addtag">
        <div class="input-button-container">
            <el-input ref="InputRef" v-model="form.tagName" placeholder="标签名"/>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addTag">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { addTagService, delTagService, getTagListByPageService, updateTagService } from '@/api/tag';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { TagList, Tags } from '@/type/article';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref, toRaw } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const tagList: TagList[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const dialogVisible = ref(false)
const form = ref({
    tagName: '',
    id: 0
})

const dialogTitle = ref('')


const getTagList = async () => {
    loading.value = true
    clearTag()
    const res = await getTagListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const r of res.data.data.data) {
        tagList.push(r)
    }
    loading.value = false
}

const tableRowClassName = ({row, rowIndex} : {row: TagList, rowIndex: number}) => {
    return row.isDel === 1 ? 'warning-row' : '';
}

const handleCurrentChange = () => {
    getTagList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
}
const onDelTag = async (id: any, index: any) => {
    await delTagService(id);
    ElMessage.success("删除成功")
    getTagList()
}
const onEditTag = (row: any, index: any) => {
    form.value = { ...row };
    dialogTitle.value = '更新标签'
    dialogVisible.value = true
}
const onAddTag = () => {
    resetForm()
    dialogTitle.value = '新增标题'
    dialogVisible.value = true
}

const addTag = async () => {
    loading.value = true;
    try {
        // 验证表单
        if (!form.value.tagName) {
            ElMessage.error("分类名称不能为空");
            return;
        }
        
        if (form.value.id === 0) {
            await addTagService({ id: 0, tagName: form.value.tagName })
        } else {
            await updateTagService({id: form.value.id, tagName: form.value.tagName })
        }

        ElMessage.success("操作成功");
        getTagList(); 
        dialogVisible.value = false; 
        resetForm(); 
    } catch (error) {
    } finally {
        loading.value = false;
    }
}

const resetForm = () => {
    form.value.id = 0
    form.value.tagName = ''
}

const clearTag = () => {
    if (tagList.length > 0) {
        tagList.splice(0, tagList.length)
    }
}
onMounted(() => {
    getTagList()
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

</style>