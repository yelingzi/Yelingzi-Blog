package com.yeling.album.mapper;

import com.yeling.album.domian.entity.Album;
import com.yeling.album.vo.request.SimpleAlbumReq;
import com.yeling.album.vo.response.AlbumResp;
import com.yeling.album.vo.response.PhotoResp;
import com.yeling.album.vo.response.SimpleAlbumResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@Mapper
public interface AlbumMapper {

    @Select("""
            SELECT COUNT(*) FROM album 
            """)
    int findAlbumCount();

    @Select("""
            SELECT COUNT(*) FROM album WHERE is_del=#{state}
            """)
    int findAlbumCountByState(Integer isDel);

    @Insert("""
            insert into album(album_name,album_cover,album_desc,user_id,nickname)
            values (#{albumName}, #{albumCover}, #{albumDesc}, #{userId}, #{nickname})
            """)
    void addAlbum(String albumName, String albumCover, String albumDesc, Integer userId, String nickname);

    @Update("""
            UPDATE album SET is_del=#{state} WHERE id=#{id}
            """)
    void updateAlbumStateById(Integer isDel, Integer id);

    @Select("""
            SELECT * FROM album ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Album> findAlbumByPage(int page, int pageSize);

    @Select("""
            SELECT id,album_name FROM album WHERE is_del=0 ORDER BY create_time
            """)
    List<SimpleAlbumReq> findSimpleAlbum();

    @Select("""
            SELECT id,album_name,album_cover,album_desc,user_id,nickname,create_time FROM album WHERE is_del=0 AND id=#{id}
            """)
    AlbumResp findAlbumById(Integer id);

    @Select("""
            SELECT id,photo_name,photo_url,user_id,nickname,create_time,album_id FROM photo WHERE is_del=0 AND album_id=#{id}
            """)
    List<PhotoResp> findPhotoByAlbumId(Integer id);


    @Insert("""
            insert into photo(photo_name,photo_url,album_id,user_id,nickname)
            values (#{photoName}, #{photoUrl}, #{albumId}, #{userId}, #{nickname})
            """)
    void addPhotoData(String photoName, String photoUrl, Integer albumId, Integer userId, String nickname);

    @Select("SELECT LAST_INSERT_ID()")
    Integer getLastInsertId();

    @Select("""
            SELECT id,album_name,album_cover,album_desc,create_time FROM album WHERE is_del=0
            """)
    List<SimpleAlbumResp> findSimpleAlbumList();

    @Select("""
            SELECT id,album_name,album_cover,album_desc,create_time FROM album WHERE is_del=0 ORDER BY photo_count DESC LIMIT 0, 5
            """)
    List<SimpleAlbumResp> findSimpleAlbumListOrderByPhotoCount();

}
