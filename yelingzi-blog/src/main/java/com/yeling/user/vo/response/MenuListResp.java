package com.yeling.user.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuListResp {

    /**
     * 菜单id
     */
    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;

    /**
     * 名称
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
     * 排序
     */
    private Integer sortOrder;
    /**
     * 接口路径
     */
    private String pathPattern;
    /**
     * 文件路径
     */
    private String component;
    /**
     * 菜单类型 1目录 2菜单
     */
    private Integer menuType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 是否删除
     */
    private Integer isDel;

    private List<MenuListResp> children;

}
