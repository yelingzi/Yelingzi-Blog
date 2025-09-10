<template>

    <page-container title="友链管理">
        <template #extra>
            <el-button @click="dialogVisible = true">添加友链</el-button>
        </template>

        <el-table v-loading="loading" :data="friendList" style="width: 100%" class="table"
            :row-class-name="tableRowClassName">
            <el-table-column type="index" label="序号" width="80"></el-table-column>
            <el-table-column prop="title" label="网站标题"></el-table-column>
            <el-table-column prop="cover" label="网页封面" width="200">
                <template #default="{ row }">
                    <el-image class="cover-image" :src="row.cover" fit="cover" @click="openLink(row.cover)">
                        <template #placeholder>
                            <div class="image-placeholder">加载中...</div>
                        </template>
                    </el-image>
                </template>
            </el-table-column>
            <el-table-column prop="introduction" label="简介"></el-table-column>
            <el-table-column prop="url" label="链接" width="200">
                <template #default="{ row }">
                    <a :href="row.url" target="_blank" class="link-text">{{ row.url }}</a>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200"></el-table-column>
            <el-table-column prop="" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag type="primary" v-if="row.state === 0">待审核</el-tag>
                    <el-tag type="danger" v-else-if="row.state === 1">已删除</el-tag>
                    <el-tag type="success" v-else-if="row.state === 2">已通过</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
                <template #default="{ row, $index }">
                    <el-button v-if="row.state !== 2" circle plain type="primary"
                        @click="onPassFriend(row.id, $index)"><el-icon>
                            <Check />
                        </el-icon></el-button>
                    <el-button v-if="row.state !== 1" circle plain type="danger"
                        @click="onDelFriend(row.id, $index)"><el-icon>
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
    <el-dialog v-model="dialogVisible" title="添加友链"  :lockScroll="false" 
    width="500" center class="addCategory">
    <el-form class="form-friend" :model="friend" :rules="rules" ref="form" autocomplete="off">
                <el-form-item class="form-input " label="名称：" prop="title">
                  <el-input  v-model="friend.title" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input" label="简介：" prop="introduction">
                  <el-input v-model="friend.introduction" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input" label="封面：" prop="cover">
                  <el-input  v-model="friend.cover" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input" label="网址：" prop="url">
                  <el-input  v-model="friend.url" clearable></el-input>
                </el-form-item>
              </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitFriend()">
                    确定
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { addFriendService, delFriendService, getFriendListByPageService, updateFriendService } from '@/api/friend';
import PageContainer from '@/components/pageContainer/PageContainer.vue';
import type { MessageVO } from '@/type/message';
import { ElMessage } from 'element-plus';
import { onMounted, reactive, ref, toRaw } from 'vue';

const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const friendList: MessageVO[] = reactive([])
const loading = ref(false)
const background = ref(true)
const disabled = ref(false)
const dialogVisible = ref(false)
const form = ref()
const friend = ref({
  title: '',
  introduction: '',
  cover: '',
  url: '',
  recommendStatus: false,
  createTime: ''
})
const rules = {
  title: [
    { required: true, message: '名称是什么呢', trigger: 'blur' }
  ],
  introduction: [
    { required: true, message: '介绍一下网站吧', trigger: 'blur' }
  ],
  cover: [
    { required: true, message: '要有个形象才行吖', trigger: 'blur' },
    // 新增 HTTPS 校验
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!/^https:\/\//i.test(value)) { // 忽略大小写校验
          callback(new Error('封面链接必须以 https:// 开头'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  url: [
    { required: true, message: '没有网址可访问不到捏', trigger: 'blur' },
    { type: 'url', message: '请输入合法URL', trigger: 'blur' },
    // 新增 HTTPS 校验
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!/^https:\/\//i.test(value)) { // 忽略大小写校验
          callback(new Error('网址链接必须以 https:// 开头'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};
const getFriendList = async () => {
    loading.value = true
    clearFriend()
    const res = await getFriendListByPageService(page.value, pageSize.value)
    total.value = res.data.data.total
    for (const r of res.data.data.data) {
        friendList.push(r)
    }
    loading.value = false
}

const tableRowClassName = ({ row, rowIndex }: { row: MessageVO, rowIndex: number }) => {
    if (row.state === 1) {
        return 'warning-row'
    } else if (row.state === 2) {
        return 'success-row'
    }
    return '';
}

const handleCurrentChange = () => {
    getFriendList()
}
const handleSizeChange = (val: number) => {
    pageSize.value = val
    getFriendList()
}
const onDelFriend = async (id: number, index: any) => {
    await delFriendService(id);
    ElMessage.success("删除成功")
    getFriendList()
}
const onPassFriend = async (id: number, index: any) => {
    await updateFriendService(id);
    ElMessage.success("审核通过")
    getFriendList()
}

const submitFriend = async () => {

  await form.value.validate(async (valid: boolean) => {
    if (valid) {
      // 如果验证通过
      await addFriendService({
        cover: friend.value.cover,
        url: friend.value.url,
        title: friend.value.title,
        introduction: friend.value.introduction
      })
      ElMessage.success('提交成功');
      dialogVisible.value = false
      resetFriend()
      // 在这里执行提交逻辑
    } else {
      // 如果验证失败，提示用户
      ElMessage.error('请完善表单内容后再提交~');
    }
  });
}
const resetFriend = () => {
  friend.value.cover = ''
  friend.value.url = ''
  friend.value.title = ''
  friend.value.introduction = ''
}
const clearFriend = () => {
    if (friendList.length > 0) {
        friendList.splice(0, friendList.length)
    }
}

const openLink = (url: string | URL | undefined) => {
    window.open(url, '_blank');
};

onMounted(() => {
    getFriendList()
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
.cover-image{
    max-width: 240px;
    max-height: 150px;
}
.form-input {
  width: 360px;
  margin: 16px auto;
}
:deep(.warning-row) {
    background-color: #ffebe3;
}

:deep(.success-row) {
    background-color: #e1f3d8;
}
</style>