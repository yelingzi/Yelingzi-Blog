<template>
    <div class="login-wrapper">
        <div class="form-wrap">
            <el-row>
                <el-col :span="13">
                    <el-image :src="image" fit="cover" />
                </el-col>
                <el-col :span="11" class="form-col">
                    <div class="form-container">
                        <el-form :model="form" :rules="rules" ref="loginForm" size="large">
                            <h1 class="form-title">叶玲子的博客后台</h1>
                            <el-form-item prop="email">
                                <el-input class="form-input" v-model="form.email" placeholder="邮箱" />
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input class="form-input" v-model="form.password" type="password" show-password
                                    placeholder="密码" />
                            </el-form-item>
                            <el-form-item prop="verifyCode">
                                <div class="input-button-container form-input">
                                    <el-input v-model="form.verifyCode" placeholder="请输入验证码"
                                        class="input-flex"></el-input>
                                    <div @click="getVerifyCode()" class="button-vc">
                                        <el-tooltip effect="dark" content="点击刷新" placement="bottom" hide-after="0">
                                            <el-image :src="verifyCodeUrl" class="image"></el-image>
                                        </el-tooltip>
                                    </div>
                                </div>
                            </el-form-item>
                            <el-form-item class="btn-container">
                                <el-button type="primary" @click="login">登录</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>

</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { Md5 } from 'ts-md5'
import { useUserStore } from '@/stores'
import { injectDynamicRoutes } from '@/router'
import {
    getVerifyCodeService,
    userInfoService,
    userLoginService,
    userMenuListService
} from '@/api/login'
import image from '@/assets/images/1.jpg'

/* ----------------  数据  ---------------- */
const router = useRouter()
const userStore = useUserStore()
const loginForm = ref<FormInstance>()

const form = reactive({
    email: '123456@123456.com',
    password: '123456',
    verifyCode: ''
})

const verifyCodeKey = ref('')
const verifyCodeUrl = ref('')

/* --------------  校验规则  -------------- */
const rules = {
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '邮箱格式不正确', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { pattern: /^\S{6,15}$/, message: '密码必须是 6-15 位的非空字符', trigger: 'blur' }
    ],
    verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

/* --------------  计算属性  -------------- */
const canSubmit = computed(() => form.email && form.password && form.verifyCode)

/* --------------  方法  -------------- */
const encrypt = (v: string) => Md5.hashStr(v).toString()

async function login() {
    try {
        await loginForm.value?.validate()
    } catch {
        ElMessage.error('请完善表单后再提交')
        return
    }

    try {
        const { data } = await userLoginService({
            email: form.email,
            password: encrypt(form.password),
            verifyCodeKey: verifyCodeKey.value,
            verifyCode: form.verifyCode
        })

        await loginSuccess(data.data) // 存储用户信息 & 跳首页
        ElMessage.success('登录成功')
        resetForm()
    } catch (e: any) {
        ElMessage.error(e?.response?.data?.message || '登录失败')
        getVerifyCode() // 刷新验证码
    }
}

async function loginSuccess(token: any) {
    userStore.setTokens(token.accessToken, token.refreshToken)

    const [{ data: userRes }, { data: menuRes }] = await Promise.all([
        userInfoService(),
        userMenuListService()
    ])

    userStore.setUserState({ ...userRes.data, login: true })
    userStore.setMenuList(menuRes.data)
    injectDynamicRoutes(menuRes.data)

    await router.replace('/home')
}

function resetForm() {
    Object.assign(form, {
        email: '123456@123456.com',
        password: '123456',
        verifyCode: ''
    })
}

async function getVerifyCode() {
    const res = await getVerifyCodeService()
    verifyCodeKey.value = res.data.data.key
    verifyCodeUrl.value = res.data.data.image
}

/* --------------  生命周期  -------------- */
onMounted(() => { 
    getVerifyCode()
})

</script>

<style lang="scss" scoped>
.login-wrapper {
    width: 99vw;
    height: 98vh;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
}

.form-wrap {
    width: 900px;
    height: 500px;
    background-color: #eee;
    border: #ddd 1px solid;
    border-radius: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 15px 30px var(--grey-9-a1), 0 10px 10px var(--grey-9-a1);
    position: relative;
    overflow: hidden;
    // 新增保持宽高比例
    aspect-ratio: 16/9; // 根据你的设计比例调整

    // 添加响应式尺寸
    @media (max-width: 900px) {
        width: 95vw;
        height: auto;
        min-height: 300px;
    }

    .form-col {
        display: flex;
        align-items: center;
        padding: 20px; // 添加内边距避免内容贴边
    }

    .form-container {
        width: 100%; // 确保表单容器宽度填满
        padding: 0 20px; // 添加左右内边距
    }

    .form-title {
        text-align: center;
        margin-bottom: 2rem;
        font-size: 1.5rem;
        color: #333;
        width: 100%;
    }

    .form-input {
        width: 100%; // 输入框宽度填满容器
    }

    .input-button-container {
        display: flex;
        align-items: center;
    }

    .button-vc {
        width: 134px;
        height: 40px;
        padding: 0px;
        border-radius: 4px;
        background-color: #FFF;
    }

    .btn-container {
        :deep(.el-form-item__content) {
            display: flex;
            justify-content: center;
        }

        .el-button {
            width: 100%; // 按钮宽度填满
            max-width: 100px; // 设置最大宽度
        }
    }
}

.myCenter {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%; // 添加高度保证垂直居中
}
</style>