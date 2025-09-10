<template>

    <page-container title="文章管理">
        <template #extra>
            <el-button @click="onAddCategory">添加新的分类</el-button>
        </template>

        <el-table v-loading="loading" :data="categoryList" style="width: 100%" class="table"
        :row-class-name="tableRowClassName">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="categoryName" label="分类名"></el-table-column>
            <el-table-column prop="articleCount" label="文章数"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="userId" label="创建者ID"></el-table-column>
            <el-table-column prop="nickname" label="创建者昵称"></el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="onEditCategory(row, $index)"><el-icon>
                            <Edit />
                        </el-icon></el-button>
                    <el-button v-if="row.isDel === 0" circle plain type="danger" @click="onDelCategory(row.id, $index)"><el-icon>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle"  :lockScroll="false" 
    width="500" center class="addCategory">
        <div class="input-button-container">
            <el-input ref="InputRef" v-model="form.categoryName"  placeholder="文章分类名"/>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="addCategory">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { addCategoryService, delCategoryService, getCategoryListByPageService, updateCategoryService } from '@/api/category';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { Category, CategoryList } from '@/type/article';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const categoryList: CategoryList[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
    categoryName: '',
    id: 0
})


const getCategoryList = async () => {
    loading.value = true
    clearCategory()
    const res = await getCategoryListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const r of res.data.data.data) {
        categoryList.push(r)
    }
    loading.value = false
}
let count = 0
const tableRowClassName = ({row, rowIndex} : {row: CategoryList, rowIndex: number}) => {
    return row.isDel === 1 ? 'warning-row' : '';
}

const handleCurrentChange = () => {
    getCategoryList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
}
const onDelCategory = async (id: any, index: any) => {
    await delCategoryService(id);
    ElMessage.success("删除成功")
    getCategoryList()
}

const onEditCategory = (row: any, index: any) => {
    form.value = { ...row };
    dialogTitle.value = '更新分类';
    dialogVisible.value = true;
};

const onAddCategory = () => {
    resetForm(); 
    dialogTitle.value = '新增分类';
    dialogVisible.value = true;
};

const addCategory = async () => {
    loading.value = true;
    try {
        // 验证表单
        if (!form.value.categoryName) {
            ElMessage.error("分类名称不能为空");
            return;
        }

        if (form.value.id === 0) {
            await addCategoryService({ id: 0, categoryName: form.value.categoryName });
        } else {
            await updateCategoryService({ id: form.value.id, categoryName: form.value.categoryName });
        }

        ElMessage.success("操作成功");
        getCategoryList(); 
        dialogVisible.value = false; 
        resetForm(); 
    } catch (error) {
    } finally {
        loading.value = false;
    }
};

// 重置表单
const resetForm = () => {
    form.value = {
        id: 0,
        categoryName: ''
    };
};

const clearCategory = () => {
    if (categoryList.length > 0) {
        categoryList.splice(0, categoryList.length)
    }
}
onMounted(() => {
    getCategoryList()
})
</script>


<style lang="scss" scoped>

.table{
    height: 76vh;
}

.pagination {
    display: flex;
    justify-content: flex-end; /* 将内容推到右边 */
    margin-top: 12px;
}

:deep(.el-table .warning-row) {
    --el-table-tr-bg-color: #ffebe3; 
}
</style>