package com.yeling.yelingziblog.user.service;

import com.yeling.yelingziblog.common.vo.response.SingleDataSearchResp;
import com.yeling.yelingziblog.user.vo.request.EmailLoginReq;
import com.yeling.yelingziblog.user.vo.request.ResetPasswordReq;
import com.yeling.yelingziblog.user.vo.response.LoginResp;
import com.yeling.yelingziblog.user.vo.response.UserInfoResp;
import com.yeling.yelingziblog.user.entity.VerifyCodeEntity;
import com.yeling.yelingziblog.user.vo.request.UserInfoReq;
import com.yeling.yelingziblog.user.vo.request.UserLogin;

import java.util.List;

public interface UserService {

    LoginResp login(UserLogin user, String ip);

    Integer reg(UserLogin user);

    VerifyCodeEntity generateVerifyCode();

    UserInfoResp getInfo(int id);

    UserInfoResp getInfoById(Integer id);

    Integer updateUserInfo(UserInfoReq userInfoReq, String jwt);

    void getEmailVerifyCode(String email, boolean isRegistration);

    LoginResp captchaLogin(EmailLoginReq req, String ip);

    LoginResp adminLogin(UserLogin user, String ip);

    void getForgetEmailVerifyCode(String email, String ip);

    String checkResetCaptcha(UserLogin u);

    void resetPassword(ResetPasswordReq req);

    String refresh(Integer id);

    LoginResp adminRefresh(Integer id);

    List<SingleDataSearchResp> getUserInfoSingleDataListBySearch(String search, String type);

    Integer getUserIdByNickname(String nickname);

}
