package com.yeling.user.service;

import com.yeling.user.vo.response.MenuListResp;
import com.yeling.user.vo.response.MenuResp;
import com.yeling.user.vo.request.MenuReq;

import java.util.List;

public interface MenuService {
    List<MenuResp> listMenu(Integer id);

    List<String> getUserAllowedPaths(Integer adminId);

    List<MenuListResp> getMenuList(int id);

    void delMenu(Long id);

    void renewMenu(Long id);

    void addMenu(MenuReq menuReq);

    void updateMenu(MenuReq menuReq);

}
