package com.yeling.other.mapper;

import com.yeling.other.domian.entity.ClassesTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ClassesTableMapper {

    /**
     * 根据semester查询课程表
     */
    @Select("select * from classestable where semester=#{semester}")
    List<ClassesTable> list(String semester);

    @Insert("insert into classestable(name,fir_week,fir_start,fir_end,sec_week,sec_start,sec_end," +
            "week_num_start,week_num_end,semester) values " +
            "(#{name},#{firWeek},#{firStart},#{firEnd},#{secWeek},#{secStart},#{secEnd},#{weekNumStart},#{weekNumEnd},#{semester})")
    void addClassTable(ClassesTable classesTable);

    @Select("select * from classestable")
    List<ClassesTable> getClassTable();

    @Select("select COUNT(*) from classestable where id = #{id}")
    Long getClassTableById(Integer id);

    @Update("update classestable set name=#{name},fir_week=#{firWeek},fir_start=#{firStart}," +
            "fir_end=#{firEnd},sec_week=#{secWeek},sec_start=#{secStart},sec_end=#{secEnd},week_num_start=#{weekNumStart}," +
            "week_num_end=#{weekNumEnd},semester=#{semester} where id = #{id}")
    void updateClassTable(ClassesTable classesTable);
}
