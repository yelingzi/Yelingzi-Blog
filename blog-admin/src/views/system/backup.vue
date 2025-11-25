<template>
    <page-container title="系统备份" class="tag-manage-container">

        <div class="table-wrapper">
            <el-table v-loading="loading" :data="tableList" style="width: 100%" class="table">
                <el-table-column type="index" label="序号" width="80"></el-table-column>
                <el-table-column prop="name" label="表"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="操作" width="120">
                    <template #default="{ row }">
                        <el-button :loading="row.waitDownload" circle plain type="primary" @click="handleExport(row)">
                            <template #icon>
                                <Download />
                            </template>
                        </el-button>
                    </template>
                </el-table-column>
                <template #empty>
                    <el-empty description="没有数据"></el-empty>
                </template>
            </el-table>

        </div>
    </page-container>
</template>

<script lang="ts" setup>
import { exportTableService, getTablesListService } from '@/api/system';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { TableInfo, TableRow, WsExportResponse } from '@/type/system';
import { addWsMessageHandler, removeWsMessageHandler } from '@/utils/websocket';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, onUnmounted, ref } from 'vue';

const tableList = ref<TableRow[]>([]);
const loading = ref<boolean>(false);

// 加载表格列表
const loadTableList = async (): Promise<void> => {
    loading.value = true;
    try {
        const res = await getTablesListService();
        tableList.value = res.data.data.map((item: TableInfo) => ({
            ...item,
            waitDownload: false
        }));
    } catch {
        ElMessage.error('加载失败，请稍后重试');
    } finally {
        loading.value = false;
    }
};

// 安全触发下载
const triggerDownload = (url: string, filename: string): void => {
    try {
        // 如果 URL 是相对路径，转换为完整 URL
        let downloadUrl = url;
        if (url.startsWith('/')) {
            downloadUrl = `${window.location.origin}${url}`;
        }
        
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = filename;
        link.target = '_blank';
        link.rel = 'noopener noreferrer';
        link.style.display = 'none';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        // 注意：这里不需要清理，因为这不是 blob URL
        // 只有使用 URL.createObjectURL() 创建的 blob URL 才需要 revoke
    } catch (error) {
        console.error('下载失败:', error);
        ElMessage.error('下载失败，请重试');
    }
};

// WebSocket 消息处理
const handleWsMessage = (raw: WsExportResponse): void => {
    const { status, data } = raw;
    console.log('WebSocket 消息:', raw);
    
    if (status === 'error') {
        ElMessage.error(`导出失败：${data || '未知错误'}`);
        resetTableStatus(data.tableName);
        return;
    }

    if (status === 'success' && data.url && data.tableName) {
        ElMessage.success('导出成功，开始下载');
        
        // 根据 URL 后缀确定文件扩展名
        const fileExtension = data.url.split('.').pop() || 'xlsx';
        const filename = `${data.tableName}_${Date.now()}.${fileExtension}`;
        
        triggerDownload(data.url, filename);
        resetTableStatus(data.tableName);
    }
};

// 重置表格状态
const resetTableStatus = (tableName?: string): void => {
    const table = tableList.value.find(t => t.name === tableName);
    if (table) table.waitDownload = false;
};

// 导出处理（带确认）
const handleExport = async (table: TableRow): Promise<void> => {
    if (table.waitDownload) {
        ElMessage.warning('正在导出中，请稍候...');
        return;
    }

    try {
        await ElMessageBox.confirm(
            `确认导出表 "${table.description}" 吗？`,
            '导出确认',
            {
                confirmButtonText: '确认导出',
                cancelButtonText: '取消',
                type: 'info'
            }
        );

        await exportTableService({ tableName: table.name });
        table.waitDownload = true;
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('导出请求失败，请重试');
        }
    }
};

onMounted(() => {
    addWsMessageHandler('export', handleWsMessage)
    loadTableList()
})

onUnmounted(() => {
    removeWsMessageHandler('export', handleWsMessage)
})
</script>


<style lang="scss" scoped>
.tag-manage-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.table-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;

    .table {
        flex: 1;
    }
}
</style>