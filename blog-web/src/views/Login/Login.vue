<template>
  <div class="container">
    <div class="header">
      <h1 class="form-title">登录</h1>
      <div class="to-reg pointer" @click="emit('toRegist')">去注册</div>
    </div>
    <el-form :model="form" :rules="rules" ref="loginForm" size="large">

      <el-form-item prop="email">
        <el-input v-model="form.email" placeholder="邮箱"></el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item prop="verifyCode">
        <div class="myCenter form-input">
          <el-input v-model="form.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
          <div @click="getVerifyCode()" class="button-vc">
            <el-tooltip effect="dark" content="点击刷新" placement="bottom" hide-after="0">
              <el-image :src="verifyCodeUrl" class="image"></el-image>
            </el-tooltip>
          </div>
        </div>
      </el-form-item>

    </el-form>

    <div class="code-login" :class="{ 'coding-login': !isPasswordLogin }">
      <el-form :model="form" :rules="rules" ref="loginForm" size="large">
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item prop="verifyCode">
          <div class="myCenter form-input">
            <el-input v-model="form.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
            <div @click="getMailVerifyCode()" :disabled="isCounting" class="button-vc input-flex myCenter pointer">
              {{ isCounting ? `${countdown}秒后重新发送` : '发送验证码' }}
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>



    <el-form-item>
      <el-button class="form-button-login" @click="handleLogin()">登录</el-button>
    </el-form-item>
    <el-form-item>
      <div class="form-button pointer" href="#" @click="isPasswordLogin = !isPasswordLogin">
        {{ isPasswordLogin ? '验证码登录' : '密码登录' }}
      </div>
      <div class="forget pointer" @click="router.push('/login/forget')">忘记密码？</div>
    </el-form-item>

  </div>


</template>

<script lang="ts" setup>
import { getLoginEmailVerifyCodeService, getVerifyCodeService, userInfoService, userLoginService, userVerifyCodeLoginService } from '@/api/login';
import { useUserStore } from '@/stores';
import { ElMessage, type FormInstance } from 'element-plus';
import { Md5 } from 'ts-md5';
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const userStore = useUserStore()
const router = useRouter()
const loginForm = ref<FormInstance>()
const form = reactive({
  email: '',
  password: '',
  verifyCode: ''
})
const isCounting = ref(false)
const countdown = ref(0)
const isPasswordLogin = ref(true)
const emit = defineEmits(['toRegist'])
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,32}$/, message: '6-32 位非空字符', trigger: 'blur' }
  ],
  verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

/* 验证码图片 */
const verifyCodeKey = ref('')
const verifyCodeUrl = ref('')
const getVerifyCode = async () => {
  const res = await getVerifyCodeService()
  verifyCodeKey.value = res.data.data.key
  verifyCodeUrl.value = res.data.data.image
}
/* ---------- 工具函数 ---------- */
const encrypt = (v: string) => Md5.hashStr(v).toString()

const startCountdown = () => {
  isCounting.value = true
  countdown.value = 120
  const t = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(t)
      isCounting.value = false
    }
  }, 1000)
}

/* 发送邮箱验证码 */
const getMailVerifyCode = async () => {
  if (isCounting.value) return

  try {
    await loginForm.value?.validateField('email')
  } catch {
    ElMessage.error('请完善表单后再提交')
    return
  }
  await getLoginEmailVerifyCodeService({ email: form.email })


  ElMessage.success('验证码已发送')
  startCountdown()

}

/* 登录 */
const handleLogin = async () => {
  try {
    await loginForm.value?.validate()
  } catch {
    ElMessage.error('请完善表单后再提交')
    return
  }

  try {
    const api = isPasswordLogin.value
      ? userLoginService
      : userVerifyCodeLoginService

    const res = await api({
      email: form.email,
      password: isPasswordLogin.value ? encrypt(form.password) : '',
      verifyCodeKey: verifyCodeKey.value,
      verifyCode: form.verifyCode
    })
    await loginSuccess(res.data.data)
    ElMessage.success('登录成功')
    resetForm()
    redirectAfterLogin()
  } catch (e: any) {
    form.verifyCode = ''
    getVerifyCode()
  }
}

/* 登录成功后续 */
const loginSuccess = async (token: { accessToken: string; refreshToken: string }) => {
  userStore.setTokens(token.accessToken, token.refreshToken)
  const { data: userRes } = await userInfoService()
  userStore.setUserState({ ...userRes.data })
}

/* 跳转逻辑 */
const redirectAfterLogin = () => {
  const last = userStore.getLastShowWeb()
  router.push(last && last !== '/login/forget' ? last : '/')
}

const resetForm = () => {
  Object.assign(form, { email: '', password: '', repassword: '', verifyCode: '' })
  getVerifyCode()
}

/* 生命周期 */
onMounted(() => {
  if (userStore.getIsLogin()) router.replace('/home')
  getVerifyCode()
})

</script>


<style lang="scss" scoped>
.container {
  width: 300px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 3rem 0 1rem 0;
}

.to-reg {
  display: none;
}

.myCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-title {
  margin: 0 auto;
}


.form-button-login {
  margin: 0 auto;
  margin-top: 14px;
  height: 40px;
  width: 100px;
}

.form-container {
  position: absolute;
  height: 100%;

  transition: all 0.5s ease-in-out;
  background: var(--grey-0);
  flex-direction: column;
}

.button-vc {
  width: 120px;
  height: 40px;
  padding: 0px;
  border-radius: 4px;
  background-color: var(--color-blue);
}

.input-flex {
  flex: 1;
  margin-right: 10px;
}

.forget {
  margin: 0 auto;

  &:hover {
    color: var(--color-blue);
  }
}

.form-button {
  margin: 0 auto;

  &:hover {
    color: var(--color-pink);
  }
}

.code-login {
  position: absolute;
  top: 0;
  transform: translateY(-700px);
  height: 150px;
  background: #FFF;
  z-index: 100;
  padding-top: 3rem;
  width: 300px;
  margin: 0 auto;
  transition: all 0.6s ease;
}

.coding-login {
  transform: translateY(100px);
}

@media (max-width: 767px) {
  .to-reg {
    display: block;
  }

}
</style>
