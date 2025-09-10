package com.yeling.common.mapper;

import com.yeling.common.domian.entity.ViewInfo;
import com.yeling.common.domian.entity.Views;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ViewInfoMapper {

    @Insert("""
            insert into views(view_count, create_time)
            values (#{viewCount}, #{createTime})
            """)
    void addView(Views views);

    @Select("""
            SELECT view_count FROM views
            """)
    List<Integer> findViewsCountList();

    @Select("""
            SELECT * FROM views WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
            """)
    List<Views> findViewsList();

    @Insert("<script>" +
            "insert into view_info(ip, city, create_time, user_id, nickname) " +
            "values " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.ip}, #{item.city}, #{item.createTime}, #{item.userId}, #{item.nickname})" +
            "</foreach>" +
            "</script>")
    void batchInsert(List<ViewInfo> viewInfos);

    @Select("""
            SELECT * FROM view_info ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<ViewInfo> findViewInfoListByPage(Integer page, Integer pageSize);

}
