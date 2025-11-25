import request from '@/utils/request'

//登录接口
export const userLoginService = (row: {
  email: string
  password: string
  verifyCodeKey: string
  verifyCode: string
}) => {
  return request.post('/api/login/admin', row)
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
  userName: string
  niCheng: string
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

//获取菜单栏
export const userMenuListService = () => request.get('/api/admin/menu')
