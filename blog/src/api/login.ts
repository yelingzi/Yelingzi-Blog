import request from '@/utils/request'

//注册接口
export const userRegisterService = (row: { email: string; password: string; verifyCode: string }) => {
  return request.post('/api/user/reg', row)
}

//登录接口
export const userLoginService = (row: {
  email: string
  password: string
  verifyCodeKey: string
  verifyCode: string
}) => {
  return request.post('/api/login', row)
}

//登录接口
export const userVerifyCodeLoginService = (row: { email: string; verifyCode: string }) => {
  return request.post('/api/user/login/email', row)
}

//获取用户的基本信息
export const userGetInfoService = () => {
  request.get('/api/my/userinfo')
}

//获取用户IP
export const getUserIpService = () => request.get('/api/ip')

//获取验证码
export const getVerifyCodeService = () => request.get('/api/captcha')

//获取用户的基本信息
export const userInfoService = () => request.get('/api/user/info')

//更新用户的基本信息
export const setUserInfoService = (row: {
  email: string
  nickname: string
  userAvatar: string
}) => request.post('/api/user/info/update', row)

//获取验证码
export const getEmailVerifyCodeService = (row: { email: string }) =>
  request.get('/api/email/reg', {
    params: {
      email: row.email,
    },
  })

//获取登录验证码
export const getLoginEmailVerifyCodeService = (row: { email: string }) =>
  request.get('/api/email/login', {
    params: {
      email: row.email,
    },
  })

export const viewService = () => request.get('/api/view')

export const userViewService = () => request.get('/api/user/view')

//获取忘记密码验证码
export const getForgetEmailVerifyCodeService = (row: { email: string }) =>
  request.get('/api/email/forget', {
    params: {
      email: row.email,
    },
  })

//忘记密码接口
export const userForgetVerifyCodeService = (row: { email: string; verifyCode: any }) => {
  return request.post('/api/forget', row)
}

//重置密码接口
export const resetPasswardService = (row: {
  email: string
  password: string
  passwordKey: string
}) => {
  return request.post('/api/reset', row)
}
