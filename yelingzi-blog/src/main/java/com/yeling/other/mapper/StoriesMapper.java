package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Stories;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoriesMapper {

    @Insert("insert into stories(user_name,content,image_url,publish_time,user_id) " +
            "values (#{userName},#{content},#{imageUrl},#{publishTime},#{userId})")
    void addStory(Stories stories);

    @Select("SELECT * FROM stories ORDER BY publish_time DESC LIMIT #{page},#{num} ")
    List<Stories> showPage(Integer page, Integer num);

    @Select("SELECT COUNT(*) FROM stories")
    Integer getStoriesCount();

    @Delete("DELETE FROM stories WHERE id = #{id}")
    void deleteStory(Integer id);

}
