package com.yeling.yelingziblog.other.mapper;

import com.yeling.yelingziblog.other.dto.LinkMeDTO;
import com.yeling.yelingziblog.other.entity.LinkMe;
import com.yeling.yelingziblog.other.vo.request.LinkMeReq;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LinkMeMapper {

    @Insert("""
            INSERT INTO link_me(content, email, images)
            VALUES (#{content}, #{email}, #{images})
            """)
    void addLink(LinkMeDTO linkMeDTO);

    @Select("""
            SELECT * FROM link_me ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<LinkMe> findLinkListByPage(int page, int pageSize);

    @Select("""
            SELECT * FROM link_me WHERE status=#{status} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<LinkMe> findLinkPageByStatus(int status, int page, int pageSize);

    @Select("""
            SELECT COUNT(*) FROM link_me
            """)
    int findLinkCount();

    @Select("""
            SELECT COUNT(*) FROM link_me WHERE status=#{status}
            """)
    int findLinkCountByStatus(int status);
}
