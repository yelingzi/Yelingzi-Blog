<!-- components/Header/Tabs.vue -->
<template>
    <el-tabs v-model="activeTab" type="card" closable class="menu-tabs" @tab-remove="handleTabsEdit"
        @tab-click="handleTabClick">
        <el-tab-pane v-for="tab in tabList" :key="tab.route" :label="tab.name" :name="tab.route">
        </el-tab-pane>
    </el-tabs>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useTabsStore } from '@/stores'
import type { TabPaneName } from 'element-plus'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()
const { tabList } = storeToRefs(tabsStore)

// 当前激活的标签
const activeTab = ref<string>(route.path)

// 处理标签页切换
const handleTabClick = (pane: any) => {
    const targetRoute = pane.props.name as string
    if (targetRoute && targetRoute !== route.path) {
        router.push(targetRoute)
    }
}

// 监听路由变化，自动添加标签
watch(
    () => route.path,
    (newPath) => {
        if (newPath && newPath !== '/') {
            tabsStore.addTab({
                route: newPath,
                name: route.meta.name as string || newPath,
            })
            activeTab.value = newPath
        }
    },
    { immediate: true }
)


// 处理标签页编辑（添加/删除）
const handleTabsEdit = (
    targetName: TabPaneName | undefined,
) => {
    if (targetName) {
        // 删除标签
        tabsStore.removeTab(targetName as string)

        // 如果删除的是当前激活标签，跳转到最后一个标签
        if (targetName === activeTab.value && tabList.value.length > 0) {
            const lastTab = tabList.value[tabList.value.length - 1]
            router.push(lastTab.route)
        }
    }
}
</script>

<style lang="scss" scoped>
.menu-tabs {
    :deep(.el-tabs__header) {
        background-color: #fafafa;
        font-size: 32px;
        font-weight: 600;
        margin: 0;
        border-radius: 12px;
    }

}
</style>