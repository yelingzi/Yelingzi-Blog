package com.yeling.yelingziblog.album.controller;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.album.vo.request.AlbumReq;
import com.yeling.yelingziblog.album.vo.request.PhotoReq;
import com.yeling.yelingziblog.album.vo.request.SimpleAlbumReq;
import com.yeling.yelingziblog.album.vo.response.AlbumResp;
import com.yeling.yelingziblog.album.vo.response.SimpleAlbumResp;
import com.yeling.yelingziblog.album.service.AlbumService;
import com.yeling.yelingziblog.common.dto.UserContext;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 保存相册封面
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/album/upload/cover")
    public ApiResponse albumCoverUpload(@RequestParam("image") MultipartFile multipartFile){

        User user = UserContext.getUser();
        log.info("添加相册封面图片，管理员ID：{}", user.getId());

        String path = albumService.uploadAlbumCover(multipartFile);
        return path.isEmpty() ? ApiResponse.error("图片上传失败") : ApiResponse.success(path);
    }

    /**
     * 新增相册
     *
     * @param albumReq  相册数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/album/add")
    public ApiResponse addAlbum(@RequestBody AlbumReq albumReq) {

        User user = UserContext.getUser();

        albumService.addAlbum(albumReq, user);

        return ApiResponse.success();
    }

    /**
     * 获取一页相册列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/album/page")
    public ApiResponse getAlbumTagListByPage(@RequestParam int page, @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取相册列表,页数：{}，数据大小：{},管理员ID：{},邮箱：{}", page, pageSize,user.getId(), user.getEmail());

        PageResult<Album> talks = albumService.getAlbumListByPage(page, pageSize);
        if (talks == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(talks);
    }

    /**
     * 获取简单的相册列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/album/list")
    public ApiResponse getAlbumList() {
        log.info("管理员获取简单的相册列表");

        List<SimpleAlbumReq> albumList = albumService.getSimpleAlbumList();
        if (albumList == null) {
            return ApiResponse.error("获取简单的相册列表失败");
        }
        return ApiResponse.success(albumList);
    }

    /**
     * 获取相册信息
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/album/data")
    public ApiResponse getAlbumData(@RequestParam Integer albumID) {
        log.info("获取相册信息，相册ID{}", albumID);

        AlbumResp albumResp = albumService.getAlbumData(albumID);
        if (albumResp == null) {
            return ApiResponse.error("获取相册列表失败");
        }
        return ApiResponse.success(albumResp);
    }

    /**
     * 保存图片
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/album/upload/image")
    public ApiResponse albumImageUpload(@RequestParam Integer albumID, @RequestParam("image") MultipartFile multipartFile){
        User user = UserContext.getUser();
        log.info("添加相册封面图片，管理员ID：{}", user.getId());

        String path = albumService.uploadAlbumImage(multipartFile, albumID);
        if(path.isEmpty()){
            return ApiResponse.error("图片上传失败");
        }
        return ApiResponse.success(path);
    }

    /**
     * 保存图片数据
     *
     * @param photoReq   图片数据
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/album/photo/add")
    public ApiResponse addAlbumData(@RequestBody PhotoReq photoReq){

        User user = UserContext.getUser();

        log.info("添加相册数据，相册：{}，管理员ID：{}", photoReq.getAlbumId() , user.getId());

        Integer id = albumService.addAlbumData(photoReq, user);
        if(id == null){
            return ApiResponse.error("添加图片失败");
        }

        return ApiResponse.success(id);
    }


    /**
     * 获取简单的相册列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/album/list")
    public ApiResponse getSimpleAlbumList() {
        log.info("获取简单的相册列表");

        List<SimpleAlbumResp> albumList = albumService.getSimpleAlbum();
        if (albumList == null) {
            return ApiResponse.error("获取简单的相册列表失败");
        }
        return ApiResponse.success(albumList);
    }

    /**
     * 获取热门的相册列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/album/photo/count")
    public ApiResponse getSimpleAlbumOfPhotoCount() {
        log.info("获取热门的相册列表");

        List<SimpleAlbumResp> albumList = albumService.getSimpleAlbumOfPhotoCount();

        return ApiResponse.success(albumList);
    }

    /**
     * 删除说说
     *
     * @param id       说说ID
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/album/del/{id}")
    public ApiResponse deleteArticleTag(@Validated @PathVariable Integer id) {

        User user = UserContext.getUser();

        log.info("删除相册Id:{}, 管理员：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        albumService.deleteAlbum(id);
        return ApiResponse.success();
    }

    /**
     * 复原说说
     *
     * @param params       说说数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/album/regain")
    public ApiResponse regainArticleTag(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("复原相册Id:{}, 管理员：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        albumService.regainTalk(id);
        return ApiResponse.success();
    }

}
