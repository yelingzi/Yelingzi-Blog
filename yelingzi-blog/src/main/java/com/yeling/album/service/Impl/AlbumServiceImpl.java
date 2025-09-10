package com.yeling.album.service.Impl;

import com.yeling.album.domian.entity.Album;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.album.vo.request.AlbumReq;
import com.yeling.album.vo.request.PhotoReq;
import com.yeling.album.vo.request.SimpleAlbumReq;
import com.yeling.album.vo.response.AlbumResp;
import com.yeling.album.vo.response.SimpleAlbumResp;
import com.yeling.album.mapper.AlbumMapper;
import com.yeling.album.service.AlbumService;
import com.yeling.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Value("${file.upload.savePath}")
    private String savePath;

    @Value("${file.upload.relativePath}")
    private String relativePath;

    @Value("${file.upload.allowedTypes:image/jpg,image/jpeg,image/png}")
    private String allowedTypes;

    @Value("${file.upload.maxSize:10485760}") // 默认最大10MB
    private long maxSize;

    @Autowired
    private AlbumMapper albumMapper;


    @Override
    public void addAlbum(AlbumReq albumReq, User user){
        albumMapper.addAlbum(albumReq.getAlbumName(), albumReq.getAlbumCover(), albumReq.getAlbumDesc(),
                user.getId(), user.getNickname());
    }

    @Override
    public String uploadAlbumCover(MultipartFile multipartFile) {
        return ImageUtils.uploadImage(multipartFile, "/album", savePath, relativePath, allowedTypes, maxSize);
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
        return ImageUtils.uploadImage(multipartFile, "/album/" + albumID, savePath, relativePath, allowedTypes, maxSize);
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
    public List<SimpleAlbumResp> getSimpleAlbumOfPhotoCount(){
        return albumMapper.findSimpleAlbumListOrderByPhotoCount();
    }

}
