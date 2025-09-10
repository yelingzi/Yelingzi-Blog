package com.yeling.album.service;

import com.yeling.album.domian.entity.Album;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.album.vo.request.AlbumReq;
import com.yeling.album.vo.request.PhotoReq;
import com.yeling.album.vo.request.SimpleAlbumReq;
import com.yeling.album.vo.response.AlbumResp;
import com.yeling.album.vo.response.SimpleAlbumResp;
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

}
