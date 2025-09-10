package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Background;
import com.yeling.other.vo.response.BackgroundResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BackgroundMapper {

    @Insert("""
            insert into home_bg
            (url, user_id, nickname) 
            values (#{url}, #{userId}, #{nickname})
            """)
    void insert(String url, Integer userId, String nickname);

    @Select("""
            SELECT COUNT(*) FROM home_bg WHERE state=#{state}
            """)
    int findBackgroundCount(Integer state);


    @Select("""
            SELECT *
            FROM home_bg WHERE state=#{state} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Background> findBgList(Integer page, Integer pageSize, Integer state);

    @Update("""
            UPDATE home_bg SET state=#{state} WHERE id=#{id}
            """)
    void updateBackgroundState(Integer id, Integer state);

    @Select("""
            SELECT id,url
            FROM home_bg WHERE state=0 ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<BackgroundResp> findBackgroundList(Integer page, Integer pageSize);
}
