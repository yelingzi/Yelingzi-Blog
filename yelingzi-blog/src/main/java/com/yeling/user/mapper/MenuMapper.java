package com.yeling.user.mapper;

import com.yeling.user.domian.entity.Menu;
import com.yeling.user.vo.response.MenuListResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper {

    @Select("""
            SELECT m.* FROM menu m 
            JOIN  admin_menu am ON m.id = am.menu_id 
            WHERE am.admin_id = #{adminId} AND m.is_del = 0 
            ORDER BY m.sort_order
            """)
    List<Menu> findMenusByAdminId(Integer adminId);

    @Select("""
        SELECT DISTINCT m.path_pattern 
        FROM menu m
        JOIN admin_menu am ON m.id = am.menu_id
        WHERE am.admin_id = #{adminId}
          AND m.is_del = 0
          AND m.path_pattern IS NOT NULL
          AND m.path_pattern != ''
        """)
    List<String> findAllowedPathPatterns(@Param("adminId") Integer adminId);

    @Select("""
            SELECT * FROM menu WHERE is_del=#{id}
            """)
    List<MenuListResp> selectList(int id);

    @Update("""
            UPDATE menu SET is_del=#{state} WHERE id=#{id}
            """)
    void updateMenuIsDelById(Integer state, Long id);

    @Insert("""
            insert into menu(menu_name,path,icon,sort_order,path_pattern,component,menu_type,parent_id)
            values (#{menuName}, #{path}, #{icon}, #{sortOrder}, #{pathPattern}, #{component}, #{menuType}, #{parentId})
            """)
    void addMenu(String menuName, String path, String icon, Integer sortOrder, String pathPattern, String component, Integer menuType, Integer parentId);

    @Update("""
            UPDATE menu SET menu_name=#{menuName}, path=#{path}, icon=#{icon}, sort_order=#{sortOrder}, path_pattern=#{pathPattern},
            component=#{component}, menu_type=#{menuType}, parent_id=#{parentId}
            WHERE id=#{id}
            """)
    void updateMenu(String menuName, String path, String icon, Integer sortOrder, String pathPattern, String component, Integer menuType, Integer parentId, Integer id);

}
