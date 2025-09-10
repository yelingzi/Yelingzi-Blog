<template>
    <el-card class="header-container" body-style="padding: 16px; width: 100%;">
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
                <div class="badges">
                    <el-badge :value="200" :max="99" class="item">
                        <el-icon>
                            <Message />
                        </el-icon>
                    </el-badge>
                    <el-badge :value="newMessage" :max="99" class="item" >
                        <el-icon class="pointer" @click="()=> router.push({ name: 'dynamic_36', params: { chatId: '0' } })">
                            <ChatDotRound />
                        </el-icon>
                    </el-badge>
                </div>
                <el-dropdown trigger="click" @command="handleCommand" class="dropdown">
                    <span style="display: flex;align-items: center;">
                        <el-avatar :size="32" :src="userStore.userInfo.userAvatar" style="float: left; margin-right: 5px;" />
                        {{ userStore.userInfo.nickname }}
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info">
                                <el-icon>
                                    <User />
                                </el-icon>
                                个人信息
                            </el-dropdown-item>
                            <el-dropdown-item command="logout">
                                <el-icon>
                                    <SwitchButton />
                                </el-icon>
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </el-card>
</template>

<script lang="ts" setup>
import { getNewChatCountByGroupService, getNewChatCountBySingleService } from '@/api/chat';
import { useChatStore, useUserStore } from '@/stores';
import type { MenuList } from '@/type/user';
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()

interface Breadcrumbs {
    path: string,
    menuName: string
}

const newMessage = computed(()=>{
    const list = chatStore.chatList
    let count = 0
    list.forEach(chat => {
        count += chat.unReadChat
    })
    return count
})

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



const handleCommand = async (key: string) => {
    if (key === 'logout') {
        // 退出操作
        userStore.removeUserState()
        userStore.clearMenuList()
        ElMessage.success('退出登录成功')
        router.push('/login')
    } else if (key === 'info') {
        router.push('/admin/info')
    }
}

/* 4. 轮询 / 获取每天会话的新消息 */
const refreshNewCount = async () => {
  let maxSingleId = 0;
  let maxGroupId = 0;
  chatStore.chatList.forEach(c => {
    const ids = [
      ...c.chatMessageList.map(m => m.id),
      c.lastMessage?.id ?? 0
    ];
    const maxId = Math.max(...ids);

    if (c.chatType === 'single' && maxId > maxSingleId) maxSingleId = maxId;
    if (c.chatType === 'group' && maxId > maxGroupId) maxGroupId = maxId;
  });

  // 分别获取单聊和群聊的新消息
  const [singleChatData, groupChatData] = await Promise.all([
    getNewChatCountBySingleService({ device: userStore.deviceId, id: maxSingleId }),
    getNewChatCountByGroupService({ device: userStore.deviceId, id: maxGroupId })
  ]);

  // 处理单聊数据
  for (const brief of singleChatData.data.data) {
    const exist = chatStore.chatList.find(c => c.id === brief.info.id && c.chatType === brief.info.type);
    console.log(exist)
    console.log(brief.info.id)
    if (exist) {
      // 单聊返回消息列表，需要添加到现有消息中
      if (brief.messages && brief.messages.length > 0) {
        exist.chatMessageList = [...exist.chatMessageList, ...brief.messages];
        exist.unReadChat += brief.messages.length;
        if (brief.messages[brief.messages.length - 1].id > (exist.lastMessage?.id || 0)) {
          exist.lastMessage = brief.messages[brief.messages.length - 1];
        }
      }
    } else {
      chatStore.chatList.push({
        id: brief.info.id,
        route: brief.info.id,
        chatType: 'single',
        chatMessageList: brief.messages || [],
        nickname: brief.info.nickname,
        avatar: brief.info.avatar,
        unReadChat: brief.messages?.length || 0,
        lastMessage: brief.messages && brief.messages.length > 0
          ? brief.messages[brief.messages.length - 1]
          : { type: 'text', message: '', id: 0, userId: 0, nickname: '', userAvatar: '', createTime: '' }
      });
    }
  }

  // 处理群聊数据
  for (const brief of groupChatData.data.data) {
    const exist = chatStore.chatList.find(c => c.id === brief.info.id && c.chatType === 'group');
    if (exist) {
      exist.unReadChat += brief.count;
      if (brief.lastMessage && brief.lastMessage.id > (exist.lastMessage?.id || 0)) {
        exist.lastMessage = brief.lastMessage;
      }
    } else {
      chatStore.chatList.push({
        id: brief.info.id,
        route: brief.info.id,
        chatType: 'group',
        chatMessageList: [],
        nickname: brief.info.nickname,
        avatar: brief.info.avatar,
        unReadChat: brief.count,
        lastMessage: brief.lastMessage || { type: 'text', message: '', id: 0, userId: 0, nickname: '', userAvatar: '', createTime: '' }
      });
    }
  }
}

onMounted(()=>{
    refreshNewCount()
})

</script>

<style lang="scss" scoped>
.header-container {
    display: flex;
    align-items: center;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
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
.pointer{
    
}
.badges {
    display: flex;
    align-items: center;
    margin-right: 20px;
}

.badges .item {
    cursor: pointer;
    margin: 0 16px;
}

.dropdown {
    margin-left: 10px;
    margin-right: 12px;

    .el-tooltip__trigger:focus-visible{
    outline: unset;
}
}
</style>