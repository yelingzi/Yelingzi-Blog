package com.yeling.user.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单Request
 *
 * @author ican
 * @date 2022/12/08 11:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuReq {

    /**
     * 菜单id
     */
    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;

    /**
     * 名称，不能为空
     */
    @NotBlank(message = "菜单名称不能为空")
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
     * 排序，必须为大于 0 的整数
     */
    @NotNull(message = "排序不能为空")
    @Min(value = 1, message = "排序必须大于 0")
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
     * 菜单类型 1 目录 2 菜单，只能为 1 或者 2
     */
    @NotNull(message = "菜单类型不能为空")
    @Min(value = 1, message = "菜单类型必须大于0")
    @Max(value = 2, message = "菜单类型必须小于3")
    private Integer menuType;




}
