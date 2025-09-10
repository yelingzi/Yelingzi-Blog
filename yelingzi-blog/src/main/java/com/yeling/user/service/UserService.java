package com.yeling.user.service;

import com.yeling.user.vo.request.ResetPasswordReq;
import com.yeling.user.vo.response.LoginResp;
import com.yeling.user.vo.response.UserInfoResp;
import com.yeling.user.domian.entity.VerifyCodeEntity;
import com.yeling.user.vo.request.UserInfoReq;
import com.yeling.user.vo.request.UserLogin;

public interface UserService {

    LoginResp login(UserLogin user, String ip);

    Integer reg(UserLogin user);

    VerifyCodeEntity generateVerifyCode();

    UserInfoResp getInfo(int id);

    UserInfoResp getInfoById(Integer id);

    Integer updateUserInfo(UserInfoReq userInfoReq, String jwt);

    void getEmailVerifyCode(String email, boolean isRegistration);

    LoginResp captchaLogin(UserLogin user, String ip);

    LoginResp adminLogin(UserLogin user, String ip);

    void getForgetEmailVerifyCode(String email, String ip);

    String checkResetCaptcha(UserLogin u);

    void resetPassword(ResetPasswordReq req);

    String refresh(Integer id);

    LoginResp adminRefresh(Integer id);

}
