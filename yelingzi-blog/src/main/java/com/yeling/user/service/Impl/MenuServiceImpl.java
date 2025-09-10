package com.yeling.user.service.Impl;

import com.yeling.user.domian.entity.Menu;
import com.yeling.user.vo.response.MenuListResp;
import com.yeling.user.vo.response.MenuResp;
import com.yeling.user.mapper.MenuMapper;
import com.yeling.user.service.MenuService;
import com.yeling.user.vo.request.MenuReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<MenuResp> listMenu(Integer id) {

        // 1. 查询管理员有权限的菜单列表
        List<Menu> menus = menuMapper.findMenusByAdminId(id);

        // 2. 转换为MenuResp对象并构建树形结构
        return buildMenuTree(menus);
    }

    private List<MenuResp> buildMenuTree(List<Menu> menus) {
        // 创建临时存储Map
        Map<Integer, MenuResp> menuMap = new LinkedHashMap<>();
        List<MenuResp> rootMenus = new ArrayList<>();

        // 数据转换并建立索引
        for (Menu menu : menus) {
            MenuResp resp = convertToResp(menu);
            menuMap.put(resp.getId(), resp);
        }

        // 构建树形结构
        for (MenuResp resp : menuMap.values()) {
            Integer parentId = resp.getParentId();

            if (parentId == null || parentId == 0) {
                // 添加根节点
                rootMenus.add(resp);
            } else {
                // 添加子节点到父节点
                MenuResp parent = menuMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(resp);
                }
            }
        }

        // 排序处理
        sortMenus(rootMenus);
        return rootMenus;
    }

    private void sortMenus(List<MenuResp> menus) {
        if (menus == null) return;

        // 当前层级排序
        menus.sort(Comparator.comparingInt(MenuResp::getSortOrder));

        // 递归子节点排序
        for (MenuResp menu : menus) {
            if (menu.getChildren() != null) {
                sortMenus(menu.getChildren());
            }
        }
    }

    private MenuResp convertToResp(Menu menu) {
        MenuResp resp = new MenuResp();
        resp.setId(menu.getId());
        resp.setParentId(menu.getParentId());
        resp.setMenuName(menu.getMenuName());
        resp.setPath(menu.getPath());
        resp.setIcon(menu.getIcon());
        resp.setSortOrder(menu.getSortOrder());
        resp.setComponent(menu.getComponent());
        resp.setMenuType(menu.getMenuType());
        return resp;
    }
    public List<String> getUserAllowedPaths(Integer adminId) {
        try {
            List<String> patterns = menuMapper.findAllowedPathPatterns(adminId);

            return patterns.stream()
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询菜单权限异常 adminId={}", adminId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<MenuListResp> getMenuList(int id) {
        // 1. 查询所有菜单数据
        List<MenuListResp> allMenus = menuMapper.selectList(id);

        // 2. 按父节点ID分组，并排序每个父节点下的子节点
        Map<Integer, List<MenuListResp>> parentIdToChildren = new HashMap<>();
        for (MenuListResp menu : allMenus) {
            parentIdToChildren
                    .computeIfAbsent(menu.getParentId(), k -> new ArrayList<>())
                    .add(menu);
        }

        // 3. 对每个父节点下的子节点按sortOrder排序
        parentIdToChildren.values().forEach(children ->
                children.sort(Comparator.comparingInt(MenuListResp::getSortOrder))
        );

        // 4. 获取顶级菜单（parentId=0）并排序
        List<MenuListResp> topMenus = parentIdToChildren.getOrDefault(0, Collections.emptyList());
        topMenus.sort(Comparator.comparingInt(MenuListResp::getSortOrder));

        // 5. 递归构建树形结构
        topMenus.forEach(menu ->
                buildTree(menu, parentIdToChildren)
        );

        return topMenus;
    }

    private void buildTree(MenuListResp parent, Map<Integer, List<MenuListResp>> parentMap) {
        List<MenuListResp> children = parentMap.get(parent.getId());
        if (children == null) {
            parent.setChildren(Collections.emptyList());
            return;
        }
        parent.setChildren(children);
        children.forEach(child ->
                buildTree(child, parentMap)
        );
    }

    @Override
    public void delMenu(Long id){
        menuMapper.updateMenuIsDelById(1, id);
    }

    @Override
    public void renewMenu(Long id){
        menuMapper.updateMenuIsDelById(0, id);
    }

    @Override
    public void addMenu(MenuReq menuReq){
        menuMapper.addMenu(menuReq.getMenuName(), menuReq.getPath(), menuReq    .getIcon(), menuReq.getSortOrder(),
                menuReq.getPathPattern(), menuReq.getComponent(), menuReq.getMenuType(), menuReq.getParentId());
    }

    @Override
    public void updateMenu(MenuReq menuReq){
        menuMapper.updateMenu(menuReq.getMenuName(), menuReq.getPath(), menuReq.getIcon(), menuReq.getSortOrder(),
                menuReq.getPathPattern(), menuReq.getComponent(), menuReq.getMenuType(), menuReq.getParentId(), menuReq.getId());
    }


}
