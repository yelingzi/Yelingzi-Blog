package com.yeling.user.controller;


import com.yeling.user.domian.entity.User;
import com.yeling.user.service.AdminService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
public class AdminController {

    /**
     *用户管理
     */
    @Autowired
    private AdminService adminService;

    @PostMapping ("/api/admin/zc")
    public ApiResponse reg(@RequestBody User admin)
    {
        log.info("管理员注册");

        if(adminService.reg(admin)){
            return ApiResponse.success("注册管理员");
        }
        return ApiResponse.error("管理员已存在！");

    }

    /**
     * 添加敏感词
     */
//    @PostMapping("/api/admin/sensitive-word/add")
//    public Result addSensitiveWord(@RequestParam String text, @RequestHeader("Authorization") String jwtToken){
//
//        String role = JwtUtils.getRoleByJWT(jwtToken);
//        if(!role.equals("AdminAndUser")) {
//            return Result.error("权限不足！");
//        }
//
//        adminService.addSensitiveWord(text);
//        return Result.success();
//    }

}
