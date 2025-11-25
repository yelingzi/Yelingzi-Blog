package com.yeling.yelingziblog.album.service.Impl;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.album.vo.request.AlbumReq;
import com.yeling.yelingziblog.album.vo.request.PhotoReq;
import com.yeling.yelingziblog.album.vo.request.SimpleAlbumReq;
import com.yeling.yelingziblog.album.vo.response.AlbumResp;
import com.yeling.yelingziblog.album.vo.response.SimpleAlbumResp;
import com.yeling.yelingziblog.album.mapper.AlbumMapper;
import com.yeling.yelingziblog.album.service.AlbumService;
import com.yeling.yelingziblog.common.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {


    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private ImageUtils imageUtils;


    @Override
    public void addAlbum(AlbumReq albumReq, User user){
        albumMapper.addAlbum(albumReq.getAlbumName(), albumReq.getAlbumCover(), albumReq.getAlbumDesc(),
                user.getId(), user.getNickname());
    }

    @Override
    public String uploadAlbumCover(MultipartFile multipartFile) {
        return imageUtils.uploadImage(multipartFile, "/album");
    }

    @Override
    public PageResult<Album> getAlbumListByPage(Integer page, Integer pageSize){
        PageResult<Album> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(albumMapper.findAlbumCount());
        pageResult.setData(albumMapper.findAlbumByPage((page - 1) * pageSize, pageSize));
        return pageResult;
    }

    @Override
    public List<SimpleAlbumReq> getSimpleAlbumList(){
        return albumMapper.findSimpleAlbum();
    }

    @Override
    public AlbumResp getAlbumData(Integer albumID) {
        AlbumResp albumResp = albumMapper.findAlbumById(albumID);
        if (albumResp == null) {
            return null;
        }
        albumResp.setPhotoList(albumMapper.findPhotoByAlbumId(albumID));
        return albumResp;
    }

    @Override
    public String uploadAlbumImage(MultipartFile multipartFile, Integer albumID){
        return imageUtils.uploadImage(multipartFile, "/album/" + albumID);
    }

    @Override
    public Integer addAlbumData(PhotoReq photoReq, User user) {

        albumMapper.addPhotoData(photoReq.getPhotoName(), photoReq.getPhotoUrl(), photoReq.getAlbumId(),
                user.getId(), user.getNickname());

        return albumMapper.getLastInsertId();

    }

    @Override
    public List<SimpleAlbumResp> getSimpleAlbum(){
        return albumMapper.findSimpleAlbumList();
    }

    @Override
    @Cacheable(value = "album:hot")
    public List<SimpleAlbumResp> getSimpleAlbumOfPhotoCount(){
        return albumMapper.findSimpleAlbumListOrderByPhotoCount();
    }

    @Override
    public void deleteAlbum(Integer id){
        albumMapper.updateAlbumStateById(1, id);
    }

    @Override
    public void regainTalk(Integer id){
        albumMapper.updateAlbumStateById(0, id);
    }

}
