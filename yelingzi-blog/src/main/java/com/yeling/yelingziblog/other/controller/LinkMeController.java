package com.yeling.yelingziblog.other.controller;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.common.dto.UserContext;
import com.yeling.yelingziblog.other.service.LinkMeService;
import com.yeling.yelingziblog.other.vo.request.LinkMeReq;
import com.yeling.yelingziblog.other.vo.response.LinkMeResp;
import com.yeling.yelingziblog.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class LinkMeController {

    @Autowired
    private LinkMeService linkMeService;


    /**
     * 保存图片
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/upload/image")
    public ApiResponse linkImageUpload(@RequestParam("image") MultipartFile multipartFile){
        log.info("添加联系我图片");

        String path = linkMeService.linkImageUpload(multipartFile);
        if(path.isEmpty()){
            return ApiResponse.error("图片上传失败");
        }
        return ApiResponse.success(path);
    }

    /**
     * 保存图片
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/linkme/message/add")
    public ApiResponse addLink(@RequestBody @Validated LinkMeReq linkMeReq){

        linkMeService.addLink(linkMeReq);

        return ApiResponse.success();
    }

    /**
     * 获取一页留言列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param status    状态
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/linkme/page")
    public ApiResponse getPageByStatus(@RequestParam int page, @RequestParam int pageSize, @RequestParam int status) {

        User user = UserContext.getUser();

        log.info("获取联系我留言列表,页数：{}，数据大小：{}, 状态：{},管理员ID：{},邮箱：{}", page, pageSize, status, user.getId(), user.getEmail());

        PageResult<LinkMeResp> talks = linkMeService.getPageListByStatus(page, pageSize, status);

        return ApiResponse.success(talks);
    }

    /**
     * 获取一页留言列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/linkme/page/status")
    public ApiResponse getListByPage(@RequestParam int page, @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取联系我留言列表,页数：{}，数据大小：{},管理员ID：{},邮箱：{}", page, pageSize, user.getId(), user.getEmail());

        PageResult<LinkMeResp> talks = linkMeService.getPageList(page, pageSize);

        return ApiResponse.success(talks);
    }


}
