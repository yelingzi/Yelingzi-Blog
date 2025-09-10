package com.yeling.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.yeling.exception.BaseException;
import com.yeling.exception.JwtExpiredException;
import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.exception.JwtInvalidException;
import com.yeling.utils.JwtUtils;
import com.yeling.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@CrossOrigin(origins = "http://localhost:5173")
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired // 自动注入 RedisTemplate
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置CORS头
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, token");

        // 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String url = request.getRequestURL().toString();
        log.info("请求的url: {}", url);


        // 获取并校验Token
        String jwt = request.getHeader("Authorization");
        if (!StringUtils.hasLength(jwt)) {
            throw new BaseException(401, "缺失Token");
        }

        // 解析JWT获取用户信息
        User user;
        try {
            user = JwtUtils.getUser(jwt);
            UserContext.setUser(user);
        } catch (JwtExpiredException | JwtInvalidException e) {
            throw new JwtExpiredException("accessTokenExpired");
        }

        // 管理员接口校验
        if (url.contains("/api/admin/")) {
            // 获取用户角色和菜单权限
            String roleKey = "user_role:" + user.getId();
            String role = redisTemplate.opsForValue().get(roleKey);
            String menuPermissionKey = "user:menu:" + user.getId();
            Set<String> allowedPaths = redisTemplate.opsForSet().members(menuPermissionKey);

            log.info("管理员ID：{}, 权限：{}",  user.getId(), role);

            if(url.contains(" http://localhost:8080/api/admin/menu") || url.contains(" http://localhost:8080/api/admin/refresh")){
                log.info("放行url: {}", url);
                return true;
            }

            if ("admin".equals(role)) {
                String adminTokenKey = "admin_token:" + user.getId();
                String storedAdminToken = redisTemplate.opsForValue().get(adminTokenKey);

                if (!jwt.equals(storedAdminToken)) {
                    log.info("管理员令牌不匹配，可能在其他设备登录，管理员ID：{}", user.getId());
                    throw new BaseException(401, "权限不足");
                }
            }


            // 校验1：请求方法权限（通过角色控制）
            if (!checkRequestMethod(role, request.getMethod())) {
                log.info("请求方法权限不通过，管理员ID：{}", user.getId());
                throw new BaseException(401, "权限不足");
            }

            // 校验2：菜单接口权限（通过路径匹配）
//            if (!checkMenuPermission(request.getRequestURI(), allowedPaths)) {
//                log.info("菜单接口权限不通过，管理员ID：{}", user.getId());
//                sendError(response, "权限不足");
//                return false;
//            }
        }

        // 用户接口直接放行
        return true;
    }

    // 校验请求方法是否允许（根据角色配置）
    private boolean checkRequestMethod(String role, String method) {
        Map<String, List<String>> roleMethodRules = Map.of(
                "admin", List.of("GET", "POST", "PUT", "DELETE"),
                "test", List.of("GET")
        );
        return roleMethodRules.getOrDefault(role, List.of()).contains(method.toUpperCase());
    }

    // 校验菜单接口权限（路径匹配）
    private boolean checkMenuPermission(String requestPath, Set<String> allowedPaths) {
        PathMatcher pathMatcher = new AntPathMatcher();
        return allowedPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }


}
