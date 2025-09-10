<template>
  <div class="info">
    <div class="header">
      <div>
        修改信息
      </div>
      <div>
        <div class="button-container" v-if="edit">
          <el-button @click="quxiao()">取消</el-button>
          <el-button @click="submit()" type="primary">保存</el-button>
        </div>
        <el-button v-else @click="editInfo()">修改信息</el-button>
      </div>
    </div>

    <el-divider></el-divider>

    <div class="user-container">
      <div class="user-avatar-container">
        <div class="user-avatar">
          <el-avatar :size="240" :src="user.userAvatar ? user.userAvatar : avatar0" />
        </div>
        <div class="user-edit-avatar">
          <el-button @click="getAvatar()" :class="{ 'visibility-hidden': !edit }">更换头像</el-button>
        </div>
      </div>

      <div class="user-info-container">
        <el-form :label-position="labelPosition" label-width="auto" :model="user">
          <el-form-item label="昵称：">
            <div style="height: 32px;">
              <span>
                <el-input v-if="edit" v-model="user.nickname" />
                <div v-else>{{ user.nickname }}</div>
              </span>
            </div>
          </el-form-item>
          <el-form-item label="邮箱：">
            <div>
              <span>
                {{ user.email }}
              </span>
            </div>
          </el-form-item>
          <el-form-item label="特征码：">
            <div>
              <span>
                {{ user.userId }}
              </span>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="更换头像" :lock-scroll="false" style="width: 600px">
      <div class="avatar-container">
        <span v-for="(a, index) in avatar" :key="index" @click="selectAvatar(a)">
          <el-avatar :size="160" :src="a" :class="{ highlighted: a === avatarData }"
            style="border: 2px solid darkgray;" />
        </span>
      </div>
      <div style="padding-left: 500px;margin-top: 12px;">
        <el-button v-if="avatarData === ''" disabled @click="submitAvatar()">确定</el-button>
        <el-button v-else type="primary" @click="submitAvatar()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormProps } from 'element-plus'
import avatar0 from '@/assets/images/default.png'
import { setUserInfoService, userInfoService } from '@/api/login';

const labelPosition = ref<FormProps['labelPosition']>('right')
interface User {
  userId: string,
  userAvatar: string,
  email: string,
  nickname: string
}
const edit = ref(false)
const dialogVisible = ref(false)
const avatar: string[] = reactive([])
const historyNiCheng = ref<string>('')
const user = reactive<User>({
  userId: '',
  userAvatar: '',
  email: '',
  nickname: ''
})

const getUserInfo = async () => {
  const res = await userInfoService()
  user.userId = res.data.data.userId
  user.nickname = res.data.data.nickname
  user.userAvatar = res.data.data.userAvatar
  user.email = res.data.data.email
  historyNiCheng.value = res.data.data.nickname
}

const editInfo = () => {
  edit.value = true
}

const quxiao = () => {
  edit.value = false
  getUserInfo()
}

const submit = async () => {
  if (historyNiCheng.value !== user.nickname) {
    await ElMessageBox.confirm('昵称180天只能修改一次', '是否修改昵称', {
      type: 'warning',
      lockScroll: false,
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
  }
  const res = await setUserInfoService({
    email: user.email,
    nickname: user.nickname,
    userAvatar: user.userAvatar
  })
  const code = res.data.code
  codeMessage(code, "信息修改成功")
  edit.value = false
}

const codeMessage = (code: number, message: string) => {
  if (code == 1) {
    ElMessage.success(message)
  }
}

const avatarData = ref('')
const getAvatar = () => {
  avatarEmpty(avatar)
  dialogVisible.value = true
  for (let i = 0; i < 5; i++) { // 使用for循环，从0开始，小于4（不包含4）
    const str = '/image/avatar/avatar' + (i + 1) + '.jpg'; // 如果想要图片编号从1开始，就在i后加1
    avatar.push(str);
  }

}
const avatarEmpty = (avatar: any) => {
  if (avatar.length > 0) {
    avatar.splice(0, avatar.length)
  }
}

const selectAvatar = (a: string) => {
  avatarData.value = a
}

const submitAvatar = () => {
  user.userAvatar = avatarData.value
  dialogVisible.value = false
}


onMounted(() => {
  getUserInfo()
})
</script>

<style lang="scss" scoped>
.info {
  width: 100%;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.user-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  height: 100%;
  min-height: 400px;
}

.user-avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 50%;
}

.user-avatar {
  width: 240px;
  height: 240px;
  margin-bottom: 20px;
}

.user-edit-avatar {
  width: 90px;

  .el-button {
    transition: opacity 0.3s ease;
  }

  .visibility-hidden {
    visibility: hidden;
    opacity: 0;
    pointer-events: none;
    /* 禁用交互 */
  }
}

.user-info-container {
  width: 55%;
}

.el-form-item {
  margin-bottom: 20px;
}

.button-container {
  text-align: right;
}

.avatar-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.avatar-container span {
  margin-right: 20px;
}

.avatar-container span:last-child {
  margin-right: 0;
}

.highlighted {
  border-color: red !important;
}
</style>
