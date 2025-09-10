package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Verse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ForecourtMapper {

    @Select("select * from forecourt where verse = #{string}")
    Verse fundVerse(String string);

    @Insert("insert into forecourt(province,city,verse) " +
            "values (#{province},#{city},#{verse})")
    void addVerse(Verse verse);

    @Select("select * from forecourt")
    List<Verse> showVerse();

    @Select("select verse from forecourt where province = #{ip}")
    List<Verse> getIpVerse(String ip);

}
