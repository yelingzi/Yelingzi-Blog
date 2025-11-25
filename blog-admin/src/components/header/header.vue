<template>
    <el-card class="header-container">
        <div class="header-content">
            <el-icon class="fold" :size="20">
                <Fold />
            </el-icon>
            <el-breadcrumb separator="/" class="breadcrumb">
                <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.menuName"
                    :to="index < breadcrumbs.length - 1 ? item.path : null">
                    {{ item.menuName }}
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="right-content">
               <HeaderRight></HeaderRight>
            </div>
        </div>
    </el-card>
</template>

<script lang="ts" setup>
import {  useUserStore } from '@/stores';
import type { MenuList } from '@/type/user';
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import HeaderRight from './HeaderRight.vue';

const userStore = useUserStore()
const route = useRoute()

interface Breadcrumbs {
    path: string,
    menuName: string
}


// 安全获取第一个有效子路径
const findFirstValidPath = (menus: MenuList[] | null | undefined): string | undefined => {
    // 防御性编程：处理无效输入
    if (!menus || !Array.isArray(menus)) return undefined

    // 深度优先搜索有效路径
    for (const menu of menus) {
        // 优先返回当前菜单的有效路径
        if (menu.path?.trim()) return menu.path

        // 递归检查子菜单
        if (menu.children?.length) {
            const childPath = findFirstValidPath(menu.children)
            if (childPath) return childPath
        }
    }

    return undefined
}
// 动态生成面包屑
const breadcrumbs = computed(() => {
    const currentPath = route.path
    const breadcrumbList: Breadcrumbs[] = []

    // 改进版路径匹配方法
    const findBreadcrumb = (menus: MenuList[], targetPath: string): boolean => {
        return menus.some(menu => {
            // 处理父级菜单无路径的情况
            let effectivePath = menu.path || ''

            // 路径匹配成功
            if (effectivePath === targetPath) {
                breadcrumbList.push({
                    path: effectivePath,
                    menuName: menu.menuName
                })
                return true
            }

            if (!effectivePath && menu.children) {
                const firstValidChild = findFirstValidPath(menu.children)
                effectivePath = firstValidChild || ''
            }

            // 递归查找子菜单
            if (menu.children?.length) {
                const found = findBreadcrumb(menu.children, targetPath)
                if (found) {
                    // 添加父级菜单项（带路径修正）
                    breadcrumbList.push({
                        path: effectivePath,
                        menuName: menu.menuName
                    })
                    return true
                }
            }

            return false
        })
    }

    // 主匹配流程
    if (findBreadcrumb(userStore.menuList, currentPath)) {
        console.log(breadcrumbList)
        return breadcrumbList.reverse()
    }

    // 备选方案：路径分段匹配
    const pathSegments = currentPath.split('/').filter(Boolean)
    return pathSegments.map((_, index) => {
        const partialPath = '/' + pathSegments.slice(0, index + 1).join('/')
        return {
            path: partialPath,
            menuName: pathSegments[index] || 'Home'
        }
    })
})


</script>

<style lang="scss" scoped>
.header-container {
    display: flex;
    align-items: center;
    width: 100%;
    box-sizing: border-box; 
    
    // 覆盖 Element Plus 默认样式
    :deep(.el-card__body) {
        padding: 10px 16px;
        width: 100%;
        display: flex;
        box-sizing: border-box;
    }
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    min-width: 0;
}

.fold {
    margin-right: 15px;
}

.breadcrumb {
    display: flex;
    align-items: center;
}

.right-content {
    display: flex;
    align-items: center;
    margin-left: auto;
}

</style>