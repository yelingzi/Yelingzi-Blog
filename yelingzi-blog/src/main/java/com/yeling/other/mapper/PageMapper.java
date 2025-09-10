package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PageMapper {

    @Insert("""
            INSERT INTO page(user_id, page_name, route_path, config_json, status)
            VALUES (#{userId},#{pageName},#{routePath},#{configJson},#{status})
            """)
    void addPage(Page page);

    @Update("""
            UPDATE page SET page_name=#{pageName},route_path=#{routePath},config_json#{configJson} WHERE id=#{id} AND user_id=#{userId}
            """)
    void updatePage(Page page);

    @Select("""
            SELECT config_json FROM page WHERE route_path=#{path} AND user_id=#{userId} AND status = 1
            """)
    String findPageByPathAndUserId(String path, Integer userId);

    @Select("""
            SELECT config_json FROM page WHERE id=#{id} AND user_id=#{userId}
            """)
    String findPageById(Integer id, Integer userId);

    @Select("""
            SELECT id, user_id, page_name, route_path, status, create_time FROM page WHERE  user_id=#{userId} AND status = 1
            ORDER BY create_time DESC limit #{start},#{pageSize}
            """)
    List<Page> findPageListByUserId(Integer userId, int start, int pageSize);

    @Select("""
            SELECT COUNT(*) FROM page WHERE  user_id=#{userId} AND status = 1
            """)
    Integer findPageCountByUserId(Integer userId);

}
