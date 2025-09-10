<template>
  <!-- 登陆和注册 -->
  <div class=" my-animation-hideToShow">

    <div class="in-up" id="loginAndRegist">
      <div class="form-container sign-up-container">

        <el-form :model="form" :rules="rules" ref="registForm" size="large" class="login-form">
          <el-form-item>
            <h1 class="form-title">注册</h1>
          </el-form-item>
          <el-form-item prop="email">
            <el-input class="form-input" v-model="form.email" placeholder="邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input class="form-input" v-model="form.password" type="password" show-password
              placeholder="密码"></el-input>
          </el-form-item>
          <el-form-item prop="repassword">
            <el-input class="form-input" v-model="form.repassword" type="password" show-password
              placeholder="确认密码"></el-input>
          </el-form-item>
          <el-form-item prop="verifyCode">
            <div class="input-button-container form-input">
              <el-input v-model="form.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
              <div @click="getMailVerifyCode()" :disabled="isCounting" class="button-vc input-flex myCenter pointer">
                {{ isCounting ? `${countdown}秒后重新发送` : '发送验证码' }}
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button class="form-button-register" @click="handleRegister()">注册</el-button>
          </el-form-item>
        </el-form>

      </div>

      <div class="form-container sign-in-container">
        <el-form :model="form" :rules="rules" ref="loginForm" size="large" class="login-form">
          <el-form-item>
            <h1 class="form-title">登录</h1>
          </el-form-item>
          <el-form-item prop="email">
            <el-input class="form-input" v-model="form.email" placeholder="邮箱"></el-input>
          </el-form-item>
          <div v-if="isPasswordLogin">
            <el-form-item prop="password">
              <el-input class="form-input" v-model="form.password" type="password" show-password
                placeholder="密码"></el-input>
            </el-form-item>
            <el-form-item prop="verifyCode">
              <div class="input-button-container form-input">
                <el-input v-model="form.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
                <div @click="getVerifyCode()" class="button-vc">
                  <el-tooltip effect="dark" content="点击刷新" placement="bottom" hide-after="0">
                    <el-image :src="verifyCodeUrl" class="image"></el-image>
                  </el-tooltip>
                </div>
              </div>
            </el-form-item>
          </div>
          <div v-else>
            <el-form-item prop="verifyCode">
              <div class="input-button-container form-input">
                <el-input v-model="form.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
                <div @click="getMailVerifyCode()" :disabled="isCounting" class="button-vc input-flex myCenter pointer">
                  {{ isCounting ? `${countdown}秒后重新发送` : '发送验证码' }}
                </div>
              </div>
            </el-form-item>
          </div>

          <el-form-item>
            <el-button class="form-button-login" @click="handleLogin()">登录</el-button>
          </el-form-item>
          <el-form-item>
            <div v-if="isPasswordLogin" class="form-button pointer" href="#"
              @click="isPasswordLogin = !isPasswordLogin">验证码登录
            </div>
            <div v-else class="form-button pointer" href="#" @click="isPasswordLogin = !isPasswordLogin">密码登录</div>
            <div class="forget pointer" @click="router.push('/login/forget')">忘记密码？</div>
          </el-form-item>
        </el-form>

      </div>
      <div class="overlay-container">
        <div class="overlay">
          <div class="overlay-panel myCenter overlay-left">
            <h1>已有帐号？</h1>
            <p>请登录</p>
            <button class="ghost" @click="switchToLogin()">登录</button>
          </div>
          <div class="overlay-panel myCenter overlay-right">
            <h1>没有帐号？</h1>
            <p>立即注册吧</p>
            <button class="ghost" @click="switchToRegister()">注册</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getLoginEmailVerifyCodeService, getVerifyCodeService, userInfoService, userLoginService, userRegisterService, userVerifyCodeLoginService } from '@/api/login'
import { useUserStore } from '@/stores'
import { ElMessage, type FormInstance } from 'element-plus'
import { Md5 } from 'ts-md5'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

/* ---------- 表单 ---------- */
const loginForm = ref<FormInstance>()
const registForm = ref<FormInstance>()

const form = reactive({
  email: '',
  password: '',
  repassword: '',
  verifyCode: ''
})

/* ---------- 状态 ---------- */
const isPasswordLogin = ref(true)
const isCounting = ref(false)
const countdown = ref(0)

/* ---------- 校验规则 ---------- */
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,32}$/, message: '6-32 位非空字符', trigger: 'blur' }
  ],
  repassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_: any, v: string, cb: Function) =>
        v === form.password ? cb() : cb(new Error('两次密码不一致')),
      trigger: 'blur'
    }
  ],
  verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
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

/* ---------- 业务 ---------- */
const router = useRouter()
const userStore = useUserStore()

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

/* 注册 */
const handleRegister = async () => {
  try {
    await loginForm.value?.validate()
  } catch {
    ElMessage.error('请完善表单后再提交')
    return
  }

  try {
    const res = await userRegisterService({
      email: form.email,
      password: encrypt(form.password),
      verifyCode: form.verifyCode
    })
    ElMessage.success('注册成功')
    resetForm()
    switchToLogin()
  } catch (e: any) {
  }
}

/* 发送邮箱验证码 */
const getMailVerifyCode = async () => {
  if (isCounting.value) return
  try {
    await registForm.value?.validateField('email')
  } catch {
    ElMessage.error('请完善表单后再提交')
    return
  }

  try {
    await getLoginEmailVerifyCodeService({ email: form.email })
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (e: any) {
  }
}

/* 登录成功后续 */
const loginSuccess = async (token: { accessToken: string; refreshToken: string }) => {
  userStore.setTokens(token.accessToken, token.refreshToken)
  const { data: userRes } = await userInfoService()
  userStore.setUserState({ ...userRes.data, login: true })
}

/* 跳转逻辑 */
const redirectAfterLogin = () => {
  const last = userStore.getLastShowWeb()
  router.push(last && last !== '/login/forget' ? last : '/')
}

/* 重置 & 动画 */
const resetForm = () => {
  Object.assign(form, { email: '', password: '', repassword: '', verifyCode: '' })
  getVerifyCode()
}
const switchToLogin = () => document.querySelector('#loginAndRegist')?.classList.remove('right-panel-active')
const switchToRegister = () => document.querySelector('#loginAndRegist')?.classList.add('right-panel-active')

/* 验证码图片 */
const verifyCodeKey = ref('')
const verifyCodeUrl = ref('')
const getVerifyCode = async () => {
  const res = await getVerifyCodeService()
  verifyCodeKey.value = res.data.data.key
  verifyCodeUrl.value = res.data.data.image
}

/* 生命周期 */
onMounted(() => {
  if (userStore.getIsLogin()) router.replace('/home')
  getVerifyCode()
})

</script>

<style lang="scss" scoped>
.myCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-form {
  width: 300px;
  margin: 0 auto;
}

.form-title {
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem 0 2rem;
}

.form-input {
  display: flex;
  justify-content: center;
  align-items: center;
}

.form-button-login {
  margin: 0 auto;
  margin-top: 24px;
}

.form-button-register {
  margin: 0 auto;
}

.form-button {
  margin: 0 auto;

  &:hover {
    color: var(--color-pink);
  }
}

.in-up {
  opacity: 0.9;
  border-radius: 10px;
  box-shadow: 0 15px 30px var(--grey-9-a1), 0 10px 10px var(--grey-9-a1);
  position: relative;
  overflow: hidden;
  width: 750px;
  max-width: 100%;
  min-height: 450px;
  margin: 15% auto;
}


.form-container {
  position: absolute;
  height: 100%;

  transition: all 0.5s ease-in-out;
  background: var(--grey-0);
  flex-direction: column;
}

.sign-in-container {
  left: 0;
  width: 50%;
}

.sign-up-container {
  left: 0;
  width: 50%;
  opacity: 0;
}


.input-flex {
  flex: 1;
  margin-right: 10px;
}

.button-vc {
  width: 120px;
  height: 40px;
  padding: 0px;
  border-radius: 4px;
  background-color: var(--color-blue);
}

.forget {
  margin: 0 auto;

  &:hover {
    color: var(--color-blue);
  }
}

.in-up p {
  font-size: 14px;
  letter-spacing: 1px;
  margin: 20px 0 30px 0;
}

.in-up button {
  border-radius: 2rem;
  border: none;
  background: var(--lightRed);
  color: var(--grey-0);
  font-size: 16px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 2px;
}

.input-button-container {
  display: flex;
  align-items: center;
}

.in-up button:hover {
  animation: scale 0.8s ease-in-out;
}

.in-up button.ghost {
  background: transparent;
  border: 1px solid var(--grey-0);
}

.sign-up-container button {
  margin-top: 20px;
}

.overlay-container {
  position: absolute;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: all 0.5s ease-in-out;
}

.overlay {
  background: var(--gradualRed);
  color: var(--grey-0);
  position: relative;
  left: -100%;
  height: 100%;
  width: 200%;
}

.overlay-panel {
  position: absolute;
  top: 0;
  flex-direction: column;
  height: 100%;
  width: 50%;
  transition: all 0.5s ease-in-out;
}

.overlay-right {
  right: 0;
  transform: translateY(0);
}

.overlay-left {
  transform: translateY(-20%);
}

.in-up.right-panel-active .sign-in-container {
  transform: translateY(100%);
}

.in-up.right-panel-active .overlay-container {
  transform: translateX(-100%);
}

.in-up.right-panel-active .sign-up-container {
  transform: translateX(100%);
  opacity: 1;
}

.in-up.right-panel-active .overlay {
  transform: translateX(50%);
}

.in-up.right-panel-active .overlay-left {
  transform: translateY(0);
}

.in-up.right-panel-active .overlay-right {
  transform: translateY(20%);
}

.my-animation-hideToShow {
  animation-name: hideToShow
}

/* 显示 */
@keyframes hideToShow {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

:deep(.el-input__wrapper) {
  cursor: url('/src/assets/cursors/beam.cur'), not-allowed !important;
}

:deep(.el-input__inner) {
  cursor: url('/src/assets/cursors/beam.cur'), not-allowed !important;
}

:deep(.el-image__inner) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

:deep(.el-button) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

:deep(.el-button>span) {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}
</style>
