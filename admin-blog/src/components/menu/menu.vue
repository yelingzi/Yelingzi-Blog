<!-- /components/menu.vue -->
<template>
    <div class="menu">
        <div class="logo">
            <el-image :src="logo" />
            <span class="logo-text">小栈管理后台</span>
        </div>

        <el-menu active-text-color="#ffd04b" background-color="#545c64" text-color="#fff" :default-active="$route.path">
            <template v-for="item in processedMenuList" :key="item.id">
                <!-- 带子菜单的目录 -->
                <el-sub-menu v-if="shouldShowAsSubMenu(item)" :index="item.path || item.id.toString()">
                    <template #title>
                        <el-icon v-if="item.icon">
                            <component :is="item.icon" />
                        </el-icon>
                        <span>{{ item.menuName }}</span>
                    </template>

                    <!-- 子菜单项 -->
                    <el-menu-item v-for="subItem in item.children" :key="subItem.id" :index="subItem.path"
                        @click="handleMenuClick(subItem)">
                        <el-icon v-if="subItem.icon">
                            <component :is="subItem.icon" />
                        </el-icon>
                        <span>{{ subItem.menuName }}</span>
                    </el-menu-item>
                </el-sub-menu>

                <!-- 独立菜单项 -->
                <el-menu-item v-else :index="item.path" @click="handleMenuClick(item)">
                    <el-icon>
                        <component :is="item.icon" />
                    </el-icon>
                    <span>{{ item.menuName }}</span>
                </el-menu-item>
            </template>
        </el-menu>
    </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuList } from '@/type/user'

const props = defineProps({
    menuData: {
        type: Array as () => MenuList[],
        default: () => []
    },
    logo: {
        type: String,
        required: true
    }
})

const router = useRouter()

// 处理后的菜单数据（过滤无效项）
const processedMenuList = computed(() => {
    return props.menuData.filter(item =>
        // 保留有子菜单的目录或有效菜单项
        (item.children?.length && item.menuType === 1) ||
        (item.menuType === 2 && item.path)
    )
})

// 判断是否显示为子菜单
const shouldShowAsSubMenu = (item: MenuList) => {
    return item.menuType === 1 && item.children?.length
}

// 处理菜单点击
const handleMenuClick = async (menu: MenuList) => {
    if (!menu.path) return

    try {
        // 等待可能的异步路由加载
        await router.isReady()

        // 检查路由是否存在
        const exists = router.getRoutes().some(r => r.path === menu.path)
        if (!exists) {
            console.error('路由不存在:', menu.path)
            return router.push('/404')
        }

        // 执行导航
        if (router.currentRoute.value.path !== menu.path) {
            await router.push(menu.path)
        } else {
            // 相同路径添加时间戳刷新
            await router.replace({
                path: menu.path,
                query: { t: Date.now() }
            })
        }
    } catch (err) {
        console.error('路由跳转失败:', err)
        router.push('/error')
    }
}
</script>

<style lang="scss" scoped>
.menu {
    height: 100%;
    background: #545c64;

    .el-menu {
        border-right: none;
    }
}

.logo {
    display: flex;
    align-items: center;
    padding: 20px 15px;
    color: #fff;
    font-size: 20px;

    &-text {
        margin-left: 10px;
        font-weight: 500;
    }

    .el-image {
        flex-shrink: 0;
        height: 40px;
        width: 40px;
    }
}
</style>
