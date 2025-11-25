package com.yeling.yelingziblog.album.service;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.album.vo.request.AlbumReq;
import com.yeling.yelingziblog.album.vo.request.PhotoReq;
import com.yeling.yelingziblog.album.vo.request.SimpleAlbumReq;
import com.yeling.yelingziblog.album.vo.response.AlbumResp;
import com.yeling.yelingziblog.album.vo.response.SimpleAlbumResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumService {

    void addAlbum(AlbumReq albumReq, User user);

    String uploadAlbumCover(MultipartFile multipartFile);

    PageResult<Album> getAlbumListByPage(Integer page, Integer pageSize);

    List<SimpleAlbumReq> getSimpleAlbumList();

    AlbumResp getAlbumData(Integer albumID);

    String uploadAlbumImage(MultipartFile multipartFile, Integer albumID);

    Integer addAlbumData(PhotoReq photoReq, User user);

    List<SimpleAlbumResp> getSimpleAlbum();

    List<SimpleAlbumResp> getSimpleAlbumOfPhotoCount();

    void deleteAlbum(Integer id);

    void regainTalk(Integer id);

}
