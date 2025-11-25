<template>
    <div class="badges">
        <el-badge :value="200" :max="99" class="item">
            <el-icon>
                <Message />
            </el-icon>
        </el-badge>
        <el-badge :value="newMessage" :max="99" class="item">
            <el-icon class="pointer" @click="() => router.push({ name: 'dynamic_36', params: { chatId: '0' } })">
                <ChatDotRound />
            </el-icon>
        </el-badge>
    </div>
    <el-dropdown trigger="click" @command="handleCommand" class="dropdown">
        <span style="display: flex;align-items: center;">
            <yl-avatar :size="32" :src="userStore.userInfo.userAvatar" style="float: left; margin-right: 5px;" />
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
</template>

<script lang="ts" setup>
import { getNewChatCountByGroupService, getNewChatCountBySingleService } from '@/api/chat';
import { useChatStore, useUserStore } from '@/stores';
import { ElMessage } from 'element-plus';
import { computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import YlAvatar from '../Image/YlAvatar.vue';

const userStore = useUserStore()
const chatStore = useChatStore()
const router = useRouter()
const newMessage = computed(() => {
    const list = chatStore.chatList
    let count = 0
    list.forEach(chat => {
        count += chat.unReadChat
    })
    return count
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

onMounted(() => {
    refreshNewCount()
})
</script>

<style lang="scss" scoped>
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

    .el-tooltip__trigger:focus-visible {
        outline: unset;
    }
}
</style>