package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.user.vo.response.MenuListResp;
import com.yeling.user.vo.response.MenuResp;
import com.yeling.user.service.MenuService;
import com.yeling.user.vo.request.MenuReq;
import com.yeling.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class MenuController {


    @Autowired
    private MenuService menuService;

    @GetMapping("/api/admin/menu")
    public ApiResponse listMenu() {

        User user = UserContext.getUser();

        List<MenuResp> menuResp = menuService.listMenu(user.getId());

        if(menuResp.isEmpty()){
            return ApiResponse.error("获取菜单失败");
        }

        return ApiResponse.success(menuResp);
    }

    @GetMapping("/api/admin/authority/menu/list")
    public ApiResponse getMenuList(@RequestParam int id) {

        User user = UserContext.getUser();

        log.info("获取菜单列表，管理员Id：{},邮箱:{}", user.getId(), user.getEmail());

        List<MenuListResp> menuResp = menuService.getMenuList(id);

        if(menuResp.isEmpty()){
            return ApiResponse.error("获取菜单失败");
        }

        return ApiResponse.success(menuResp);
    }

    /**
     * 删除菜单
     *
     * @param params  菜单数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/authority/menu/del")
    public ApiResponse delMessage(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Long id = Long.valueOf(params.get("id").toString());

        log.info("删除菜单：{}, 管理员ID：{},邮箱:{}", id, user.getId(), user.getEmail());

        menuService.delMenu(id);
        return ApiResponse.success();
    }

    /**
     * 恢复菜单
     *
     * @param params  菜单数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/authority/menu/renew")
    public ApiResponse renewMessage(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Long id = Long.valueOf(params.get("id").toString());

        log.info("恢复菜单：{}, 管理员ID：{},邮箱:{}", id, user.getId(), user.getEmail());

        menuService.renewMenu(id);
        return ApiResponse.success();
    }


    /**
     * 添加菜单
     *
     * @param menuReq  菜单数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/authority/menu/add")
    public ApiResponse addMessage(@Valid @RequestBody MenuReq menuReq) {


        User user = UserContext.getUser();


        log.info("添加菜单, 管理员ID：{},邮箱:{}", user.getId(), user.getEmail());

        menuService.addMenu(menuReq);
        return ApiResponse.success();
    }

    /**
     * 更新菜单
     *
     * @param menuReq  菜单数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/authority/menu/update")
    public ApiResponse updateMessage(@Valid @RequestBody MenuReq menuReq) {


        User user = UserContext.getUser();


        log.info("更新菜单：{}, 管理员ID：{},邮箱:{}", menuReq.getId(), user.getId(), user.getEmail());

        menuService.updateMenu(menuReq);
        return ApiResponse.success();
    }


}
