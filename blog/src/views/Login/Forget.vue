<template>
  <div class="center-container">
    <div v-if="resetPasswordSuccess" class="forget">
      <div class="success">
        <component class="success-icon" :is="successIcon" />
        <h1 class="form-title"><span>修改密码成功</span></h1>
        <div class="success-button">
          <el-button class="form-button" @click="toLogin">去登录</el-button>
        </div>
      </div>
    </div>
    <div v-else class="forget">
      <el-form v-if="resetPassword" :model="formModel" :rules="resetRules" ref="resetFormRef" size="large"
        class="login-form">
        <el-form-item>
          <h1 class="form-title"><span>设置密码</span></h1>
        </el-form-item>
        <el-form-item class="form-input" prop="password">
          <el-input v-model="formModel.password" placeholder="密码"></el-input>
        </el-form-item>
        <el-form-item class="form-input" prop="repassword">
          <el-input v-model="formModel.repassword" placeholder="确认密码"></el-input>
        </el-form-item>
        <el-form-item class="form-input">
          <el-button class="form-button" @click="submit(resetFormRef)">提交</el-button>
        </el-form-item>
      </el-form>
      <el-form v-else :model="formModel" :rules="rules" ref="formRef" size="large" class="login-form">
        <el-form-item>
          <h1 class="form-title"><span>重置密码</span></h1>
        </el-form-item>
        <el-form-item class="form-input" prop="email">
          <el-input v-model="formModel.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <div>
          <el-form-item class="form-input" prop="verifyCode">
            <div class="input-button-container">
              <el-input v-model="formModel.verifyCode" placeholder="请输入验证码" class="input-flex"></el-input>
              <div @click="getMailVerifyCode()" :disabled="isCounting" class="button-vc pointer">
                {{ isCounting ? `${countdown}秒后重新发送` : '发送验证码' }}
              </div>
            </div>
          </el-form-item>
        </div>
        <el-form-item class="form-input">
          <el-button class="form-button" @click="next(formRef)">下一步</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>

</template>

<script lang="ts" setup>
import { getForgetEmailVerifyCodeService, getLoginEmailVerifyCodeService, resetPasswardService, userForgetVerifyCodeService } from '@/api/login';
import { ElMessage, type FormInstance } from 'element-plus';
import { Md5 } from 'ts-md5';
import { defineAsyncComponent, ref } from 'vue'
import { useRouter } from 'vue-router';

const successIcon = defineAsyncComponent(() =>
  import('@/assets/icons/success.svg')
);
const router = useRouter()
// 整个的用于提交的form数据对象
const formModel = ref({
  email: '',
  password: '',
  repassword: '',
  verifyCode: '',
})
const formRef = ref()
const resetFormRef = ref<FormInstance>()
const countdown = ref(0); // 倒计时剩余时间
const isCounting = ref(false); // 是否正在倒计时
const resetPassword = ref(false)
const resetPasswordSuccess = ref(false)
const repasswordKey = ref("")

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/,
      message: '邮箱格式不正确',
      trigger: 'blur'
    }
  ],

  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
  ],
}

const resetRules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^\S{6,32}$/,
      message: '密码必须是 6-32位 的非空字符',
      trigger: 'blur'
    }
  ],
  repassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^\S{6,32}$/,
      message: '密码必须是 6-32位 的非空字符',
      trigger: 'blur'
    },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value !== formModel.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
}

const startCountdown = () => {
  countdown.value = 120;
  isCounting.value = true;

  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      isCounting.value = false;
    }
  }, 1000);
};

const getMailVerifyCode = async () => {
  if (isCounting.value) return;

  try {
    // 增加双重校验
    const emailValid = await new Promise<boolean>((resolve) => {
      formRef.value.validateField('email', (valid: boolean | PromiseLike<boolean>) => {
        resolve(valid)
      })
    })

    // 手动校验邮箱格式
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(formModel.value.email)) {
      throw new Error('邮箱格式不正确')
    }

    if (!emailValid) {
      throw new Error('表单验证未通过')
    }

    const res = await getForgetEmailVerifyCodeService({
      email: formModel.value.email
    })

    if (res.data.code === 1) {
      ElMessage.success('验证码发送成功')
      startCountdown()
    }
  } catch (error: any) {
    // 强制重置倒计时状态
    isCounting.value = false
    countdown.value = 120
  }
};

const check = async (form: FormInstance): Promise<boolean> => {
  return new Promise((resolve) => {
    form.validate((valid: boolean) => {
      if (!valid) {
        ElMessage.error('请检查表单内容');
      }
      resolve(valid);
    });
  });
};

const encryptData = (data: string) => {
  const encryptedData = Md5.hashStr(data);
  return encryptedData
}
const submit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  if (await check(formEl)) {
    const encryptedPassword = encryptData(formModel.value.password)
    const res = await resetPasswardService({
      email: formModel.value.email,
      password: encryptedPassword,
      passwordKey: repasswordKey.value
    })
    if (res.data.code === 1) {
      resetPassword.value = false
      resetPasswordSuccess.value = true
    } else {
      resetPassword.value = false
    }
  }

}
const next = async (formEl: FormInstance | undefined) => {
  if (!formEl) return

  if (await check(formEl)) {
    const res = await userForgetVerifyCodeService({
      email: formModel.value.email, verifyCode: formModel.value.verifyCode
    })
    if (res.data.code === 1) {
      repasswordKey.value = res.data.data
      resetPassword.value = true
    }
  }

}

const toLogin = () => {
  router.push('/login')
}

</script>

<style lang="scss" scoped>
.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}

.forget {
  border-radius: 10px;
  box-shadow: 0 15px 30px var(--grey-9-a1), 0 10px 10px var(--grey-9-a1);
  position: relative;
  overflow: hidden;
  width: 550px;
  max-width: 100%;
  min-height: 300px;
  margin: 15% auto;
  // background-image: linear-gradient(120deg, #f6d365 0%, #fda085 100%);
  background-color: var(--grey-0);
}

.form-input {
  width: 300px;
  margin: 24px auto 0;
}

.form-title {
  color: var(--note-hover);
  width: 300px;
  margin: 0 auto;
  margin-top: 24px;

  span {
    justify-content: center;
    display: flex;
  }
}

.input-button-container {
  display: flex;
  align-items: center;
}

.form-button {
  margin: 0 auto;
  border-radius: 24px;
  border: none;
  background: var(--lightRed);
  color: var(--grey-0);
  font-size: 16px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 2px;
}

.button-vc {
  width: 260px;
  height: 40px;
  padding: 0px;
  border-radius: 4px;
  background-color: var(--color-blue);
  display: flex;
  justify-content: center;

  &:hover {
    background-color: #fda085;
  }
}

.success-icon {
  height: 100px;
  width: 100px;
  margin: 0 auto;
  width: 100%;
}

.success {
  margin: 0 auto;
  margin-top: 50px;
  width: 100%;
}

.success-button {
  width: 100%;
  margin: 0 auto;
  display: flex;
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
