package com.yeling.user.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    @Insert("insert into sensitive_word(words) values (#{text}) ")
    void addSensitiveWord(String text);


}
