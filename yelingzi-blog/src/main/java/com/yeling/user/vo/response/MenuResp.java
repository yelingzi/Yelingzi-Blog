package com.yeling.user.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单Response
 *
 * @author ican
 * @date 2022/12/05 22:26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResp {

    /**
     * 菜单id
     */
    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;


    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 文件路径
     */
    private String component;

    /**
     * 菜单排序
     */
    private Integer sortOrder;

    /**
     * 菜单类型 1目录 2菜单
     */
    private Integer menuType;

    /**
     * 子菜单列表
     */
    private List<MenuResp> children;

}