package com.yeling.user.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.exception.*;
import com.yeling.user.vo.request.ResetPasswordReq;
import com.yeling.user.vo.response.LoginResp;
import com.yeling.user.vo.response.UserInfoResp;
import com.yeling.user.service.EmailService;
import com.yeling.user.service.UserService;
import com.yeling.user.vo.request.UserInfoReq;
import com.yeling.user.vo.request.UserLogin;
import com.yeling.utils.IpUtils;
import com.yeling.utils.JwtUtils;
import com.yeling.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/api/login")
    public ApiResponse login(@RequestBody UserLogin u, HttpServletRequest request, HttpServletResponse response) {
        log.info("用户登录,用户名{}", u.getEmail());


        String ip = IpUtils.getIP_(request, response);
        LoginResp login = userService.login(u, ip);
        return ApiResponse.success(login);

    }

    @PostMapping ("/api/reg")
    public ApiResponse reg(@RequestBody UserLogin user)
    {
        log.info("用户注册,用户名{}",user.getEmail());

        Integer integer = userService.reg(user);

        if(integer == 3){
            return ApiResponse.success("注册用户");
        } else if (integer == 1){
            return ApiResponse.error("验证码过期");
        } else if (integer == 2){
            return ApiResponse.error("验证码错误");
        }
        return ApiResponse.error("用户已存在！");

    }

    @GetMapping("/api/captcha")
    public ApiResponse generateVerifyCode() {

        log.info("获取验证码");
        return ApiResponse.success(userService.generateVerifyCode());
    }

    @GetMapping("/api/user/info")
    public ApiResponse getUserInfo() {

        User user = UserContext.getUser();

        log.info("获取用户数据，用户 ID 为：{}", user.getId());
        UserInfoResp userInfoResp = userService.getInfo(user.getId());
        if (userInfoResp == null) {
            log.error("用户数据不存在，ID: {}", user.getId());
            return ApiResponse.error("用户数据不存在");
        }
        return ApiResponse.success(userInfoResp);
    }


    @PostMapping ("/api/user/info/update")
    public ApiResponse updateUserInfo(@RequestBody UserInfoReq userInfoReq, @RequestHeader("Authorization") String jwtToken){

        Integer id = userService.updateUserInfo(userInfoReq, jwtToken);

        if(id == 0){
            return ApiResponse.error("修改信息失败");
        } else if (id == -1) {
            return ApiResponse.error("上传数据异常");
        } else if (id == -2) {
            return ApiResponse.error("昵称已存在");
        } else if (id == -3) {
            return ApiResponse.error("昵称180天只能修改1次");
        }

        return ApiResponse.success();
    }

    @GetMapping("/api/email/reg")
    public ApiResponse emailReg(@RequestParam("email") String email) {

        userService.getEmailVerifyCode(email, true);
        return ApiResponse.success();

    }

    @GetMapping("/api/email/login")
    public ApiResponse emailLogin(@RequestParam("email") String email) {

        userService.getEmailVerifyCode(email, false);
        return ApiResponse.success();

    }


    @PostMapping("/api/login/captcha")
    public ApiResponse captchaLogin(@RequestBody UserLogin u, HttpServletRequest request, HttpServletResponse response)
    {
        log.info("用户登录,用户名{}",u.getEmail());
        String ip = IpUtils.getIP_(request, response);
        LoginResp login = userService.captchaLogin(u, ip);
        return ApiResponse.success(login);

    }

    @PostMapping("/api/login/admin")
    public ApiResponse adminLogin(@RequestBody UserLogin u, HttpServletRequest request, HttpServletResponse response)
    {
        log.info("管理员登录,管理员名{}",u.getEmail());
        String ip = IpUtils.getIP_(request, response);
        LoginResp login = userService.adminLogin(u, ip);
        return ApiResponse.success(login);

    }

    @GetMapping("/api/email/forget")
    public ApiResponse emailForget(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {

        String ip = IpUtils.getIP_(request, response);
        userService.getForgetEmailVerifyCode(email, ip);
        return ApiResponse.success();

    }

    @PostMapping("/api/forget")
    public ApiResponse checkResetCaptcha(@RequestBody UserLogin u) {

        String token = userService.checkResetCaptcha(u);
        return ApiResponse.success(token);

    }

    @PostMapping("/api/reset")
    public ApiResponse resetPassword(@RequestBody ResetPasswordReq req) {
        log.info("重置密码,邮箱:{}", req.getEmail());

        try {
            userService.resetPassword(req);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }

        return ApiResponse.success();
    }

    @PostMapping("/api/auth/refresh")
    public ApiResponse refresh(@RequestHeader("x-refresh-token") String refreshToken) {
        Integer id = JwtUtils.getUserIdByRefresh(refreshToken);
        String newAccessToken = userService.refresh(id);
        return ApiResponse.success(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/api/token/refresh")
    public ApiResponse adminRefresh(@RequestHeader("x-refresh-token") String refreshToken) {
        Integer id = JwtUtils.getUserIdByRefresh(refreshToken);
        LoginResp loginResp = userService.adminRefresh(id);
        return ApiResponse.success(loginResp);
    }

}

