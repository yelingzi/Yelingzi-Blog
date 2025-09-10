package com.yeling.user.service;

import com.yeling.user.domian.entity.User;
import com.yeling.user.vo.request.UserLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户管理
 */
public interface AdminService {

    Boolean reg(User admin);

}
