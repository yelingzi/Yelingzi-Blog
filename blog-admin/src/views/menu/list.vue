<template>
    <page-container title="菜单管理">
        <template #extra>
            <el-select v-model="selectMenu" placeholder="请选择查看类型" style="width: 200px;margin-right: 20px;">
                <el-option label="正常" :value="0" />
                <el-option label="已删除" :value="1" />
            </el-select>
            <el-button @click="onAddMenu">添加新的菜单</el-button>
        </template>

        <el-table v-loading="loading" :data="menuList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName" row-key="id" :tree-props="{ children: 'children' }"
            border >

            <el-table-column prop="id" label="ID" width="100"></el-table-column>
            <el-table-column prop="menuName" label="菜单名称"></el-table-column>
            <el-table-column prop="path" label="菜单路径"></el-table-column>
            <el-table-column prop="" label="菜单图标" width="100">
                <template #default="{ row }">
                    <el-icon>
                        <component :is="row.icon" />
                    </el-icon>
                </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序"></el-table-column>
            <el-table-column prop="pathPattern" label="接口路径"></el-table-column>
            <el-table-column prop="component" label="文件路径"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column prop="" label="菜单类型" width="100">
                <template #default="{ row }">
                    <el-tag type="danger" v-if="row.menuType === 0">未定义</el-tag>
                    <el-tag type="primary" v-else-if="row.menuType === 1">目录</el-tag>
                    <el-tag type="success" v-else-if="row.menuType === 2">菜单</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="{ row, $index }">
                    <el-button circle plain type="primary" @click="onEditMenu(row, $index)">
                        <el-icon>
                            <Edit />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.isDel === 0" circle plain type="danger"
                        @click="onDelMenu(row.id, $index)">
                        <el-icon>
                            <Delete />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.isDel === 1" circle plain type="primary"
                        @click="onRenewMenu(row.id, $index)">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-button>
                    <el-button v-if="row.parentId === 0" circle plain type="primary"
                        @click="onAddMenuChildren(row.id)">
                        <el-icon>
                            <Plus />
                        </el-icon>
                    </el-button>
                </template>
            </el-table-column>

            <template #empty>
                <el-empty description="没有数据"></el-empty>
            </template>
        </el-table>

    </page-container>
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :lock-scroll="false" width="500" center class="addAlbum">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">

            <!-- 父菜单 -->
            <el-form-item v-if="form.parentId !== 0" label="父菜单ID" prop="parentId">
                {{ form.parentId }}
            </el-form-item>

            <!-- 菜单名称 -->
            <el-form-item label="菜单名称" prop="menuName">
                <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>

            <!-- 路由地址 -->
            <el-form-item label="路由地址" prop="path">
                <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>

            <!-- 菜单图标 -->
            <el-form-item label="菜单图标" prop="icon">
                <el-input v-model="form.icon" placeholder="请输入菜单图标" />
            </el-form-item>

            <!-- 排序 -->
            <el-form-item label="排序" prop="sortOrder">
                <el-input-number v-model="form.sortOrder" :min="0" placeholder="请输入排序值" />
            </el-form-item>

            <!-- 接口路径 -->
            <el-form-item label="接口路径" prop="pathPattern">
                <el-input v-model="form.pathPattern" placeholder="请输入接口路径" />
            </el-form-item>

            <!-- 文件路径 -->
            <el-form-item label="文件路径" prop="component">
                <el-input v-model="form.component" placeholder="请输入文件路径" />
            </el-form-item>

            <!-- 菜单类型 -->
            <el-form-item label="菜单类型" prop="menuType">
                <el-select v-model="form.menuType" placeholder="请选择菜单类型">
                    <el-option label="目录" :value="1" />
                    <el-option label="菜单" :value="2" />
                </el-select>
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
import { addMenuService, delMenuService, getMenuListService, renewMenuService, updateMenuService } from '@/api/menu';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { MenuDTO, MenuVO } from '@/type/menu';
import { ElMessage, type UploadProps } from 'element-plus';
import { onMounted, reactive, ref, watch } from 'vue';

const menuList: MenuVO[] = reactive([])
const loading = ref(false)
const selectMenu = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref<MenuDTO>({
    id: 0,
    menuName: '',
    path: '',
    icon: '',
    sortOrder: 0,
    pathPattern: '',
    component: '',
    menuType: 1,
    parentId: 0,
})
const rules = ({
    menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
    icon: [{ required: true, message: '请输入菜单图标', trigger: 'blur' }],
    sortOrder: [{ required: true, message: '请输入排序值', trigger: 'blur' }],
    menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
})
const getMenuList = async () => {
    loading.value = true
    clearMenu()
    const res = await getMenuListService(selectMenu.value)
    for (const r of res.data.data) {
        menuList.push(r)
    }
    loading.value = false
}

const tableRowClassName = ({ row, rowIndex }: { row: MenuVO, rowIndex: number }) => {
    return row.isDel === 1 ? 'warning-row' : '';
}

const onDelMenu = async (id: any, index: any) => {
    await delMenuService(id);
    ElMessage.success("删除成功")
    getMenuList()
}

const onRenewMenu = async (id: any, index: any) => {
    await renewMenuService(id);
    ElMessage.success("恢复成功")
    getMenuList()
}

const onEditMenu = (row: MenuVO, index: any) => {
    dialogTitle.value = "修改菜单"
    form.value.id = row.id
    form.value.menuName = row.menuName
    form.value.path = row.path
    form.value.icon = row.icon
    form.value.sortOrder = row.sortOrder
    form.value.pathPattern = row.pathPattern
    form.value.component = row.component
    form.value.menuType = row.menuType
    form.value.parentId = row.parentId
    dialogVisible.value = true
}
const onAddMenu = () => {
    dialogTitle.value = "添加菜单"
    dialogVisible.value = true
}
const onAddMenuChildren = (parentId: number) => {
    dialogTitle.value = "添加菜单"
    resetForm()
    form.value.parentId = parentId
    dialogVisible.value = true
}

const submitForm = async () => {
    if (await checkMenu()) {
        loading.value = true;
        const menuParams = {
            id: form.value.id,
            menuName: form.value.menuName,
            path: form.value.path,
            icon: form.value.icon,
            sortOrder: form.value.sortOrder,
            pathPattern: form.value.pathPattern,
            component: form.value.component,
            menuType: form.value.menuType,
            parentId: form.value.parentId
        };
        if (form.value.id === 0) {
            await addMenuService(menuParams);
        } else {
            await updateMenuService(menuParams);
        }
        ElMessage.success("操作成功");
        getMenuList();
        dialogVisible.value = false;
        resetForm()
    }
};


// 表单验证函数
const checkMenu = async (): Promise<boolean> => {
    return new Promise((resolve) => {
        formRef.value.validate((valid: boolean) => {
            if (!valid) {
                ElMessage.error('请检查表单内容');
            }
            resolve(valid);
        });
    });
};

// 重置表单函数
const resetForm = () => {
    form.value = {
        id: 0,
        menuName: '',
        path: '',
        icon: '',
        sortOrder: 0,
        pathPattern: '',
        component: '',
        menuType: 1,
        parentId: 0
    };
};

const clearMenu = () => {
    if (menuList.length > 0) {
        menuList.splice(0, menuList.length)
    }
}

watch(
    () => selectMenu.value,
    () => {
        getMenuList();
    }
);

onMounted(() => {
    getMenuList()
})
</script>


<style lang="scss" scoped>
.table {
    height: 76vh;
    user-select: none;
}

:deep(.el-table .warning-row) {
    --el-table-tr-bg-color: #ffebe3;
}
</style>