package com.yeling.yelingziblog.user.controller;

import com.yeling.yelingziblog.common.vo.response.SingleDataSearchResp;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.common.dto.UserContext;
import com.yeling.yelingziblog.user.vo.request.EmailLoginReq;
import com.yeling.yelingziblog.user.vo.request.ResetPasswordReq;
import com.yeling.yelingziblog.user.vo.response.LoginResp;
import com.yeling.yelingziblog.user.vo.response.UserInfoResp;
import com.yeling.yelingziblog.user.service.UserService;
import com.yeling.yelingziblog.user.vo.request.UserInfoReq;
import com.yeling.yelingziblog.user.vo.request.UserLogin;
import com.yeling.yelingziblog.common.utils.IpUtils;
import com.yeling.yelingziblog.common.utils.JwtUtils;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping(value = "/api/login", produces = "application/json;charset=utf-8")
    public ApiResponse login(@RequestBody UserLogin u, HttpServletRequest request, HttpServletResponse response) {
        log.info("用户登录,用户名{}", u.getEmail());

        log.info("Request content type: {}", request.getContentType());
        log.info("Response content type: {}", response.getContentType());
        String ip = IpUtils.getIpAddr(request);
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
        log.info("获取邮箱验证码,邮箱:{}", email);
        userService.getEmailVerifyCode(email, true);
        return ApiResponse.success();

    }

    @GetMapping("/api/email/login")
    public ApiResponse emailLogin(@RequestParam("email") String email) {

        userService.getEmailVerifyCode(email, false);
        return ApiResponse.success();

    }


    @PostMapping("/api/login/email")
    public ApiResponse captchaLogin(@RequestBody EmailLoginReq req, HttpServletRequest request, HttpServletResponse response)
    {
        log.info("用户登录,用户名{}",req.getEmail());
        String ip = IpUtils.getIpAddr(request);

        LoginResp login = userService.captchaLogin(req, ip);
        return ApiResponse.success(login);

    }

    @PostMapping("/api/login/admin")
    public ApiResponse adminLogin(@RequestBody UserLogin u, HttpServletRequest request, HttpServletResponse response)
    {
        log.info("管理员登录,管理员名{}",u.getEmail());
        String ip = IpUtils.getIpAddr(request);

        LoginResp login = userService.adminLogin(u, ip);
        return ApiResponse.success(login);

    }

    @GetMapping("/api/email/forget")
    public ApiResponse emailForget(@RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {

        String ip = IpUtils.getIpAddr(request);

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

    /**
     * 模糊查询文章标题列表
     */
    @GetMapping(value = "/api/admin/user/search/nickname")
    public ApiResponse getArticleCategoryListBySearch(@RequestParam String search) {
        User user = UserContext.getUser();

        log.info("搜索用户昵称列表：{}，管理员Id：{},邮箱:{}", search, Objects.requireNonNull(user).getId(),user.getEmail());

        List<SingleDataSearchResp> pageResult = userService.getUserInfoSingleDataListBySearch(search, "nickname");

        return ApiResponse.success(pageResult);
    }

}

