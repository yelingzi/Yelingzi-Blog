package com.yeling.other.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeling.entity.UserContext;
import com.yeling.other.domian.entity.Page;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.PageReq;
import com.yeling.common.vo.request.ViewPageReq;
import com.yeling.other.service.PageService;
import com.yeling.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 新增页面
     *
     * @param pageReq  页面数据
     * @param jwtToken JWT令牌
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/user/page/add")
    public ApiResponse add(@Valid @RequestBody PageReq pageReq) {
        User user = UserContext.getUser();
        try {

            pageService.addPage(pageReq, user);
            return ApiResponse.success();

        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败 - 请求数据：{}，错误详情：",
                    pageReq.getConfigJson(), e);
            return ApiResponse.error("页面配置数据格式异常");

        } catch (IllegalArgumentException e) {
            log.warn("非法参数：{}", e.getMessage());
            return ApiResponse.error( e.getMessage());

        } catch (Exception e) {
            log.error("系统异常");
            return ApiResponse.error("系统异常");
        }
    }


    /**
     * 查看页面
     *
     * @param pageReq  页面数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/page/config")
    public ApiResponse getPage(@Valid @RequestBody ViewPageReq pageReq) {

        log.info("查看页面");
        try {
            Map<String, Object> configData = pageService.getPage(pageReq);
            return ApiResponse.success(configData);
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败: {}", e.getMessage());
            return ApiResponse.error("配置数据格式异常");
        }
    }

    /**
     * 查看页面
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/page/{id}")
    public ApiResponse getPage(@PathVariable Integer id) {

        User user = UserContext.getUser();

        log.info("查看页面");
        try {
            Map<String, Object> configData = pageService.getPageById(id, user.getId());
            return ApiResponse.success(configData);
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败: {}", e.getMessage());
            return ApiResponse.error("配置数据格式异常");
        }
    }

    /**
     * 查看页面
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/page/update")
    public ApiResponse updatePage(@Valid @RequestBody PageReq pageReq) {

        User user = UserContext.getUser();

        log.info("更新页面");
        try {

            pageService.updatePage(pageReq, user.getId());
            return ApiResponse.success();

        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败 - 请求数据：{}，错误详情：",
                    pageReq.getConfigJson(), e);
            return ApiResponse.error("页面配置数据格式异常");

        } catch (IllegalArgumentException e) {
            log.warn("非法参数：{}", e.getMessage());
            return ApiResponse.error( e.getMessage());

        } catch (Exception e) {
            log.error("系统异常");
            return ApiResponse.error("系统异常");
        }
    }

    /**
     * 查看页面
     *
     * @param page  页数
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/page/list")
    public ApiResponse getUserPageList(@RequestParam int page,
                                       @RequestParam int pageSize) {
        User user = UserContext.getUser();
        log.info("查看页面一列用户页面，用户id:{}", user.getId());

        PageResult<Page> list = pageService.userGetPageList(page, pageSize, user.getId());

        if(list.getTotal() == 0){
            return ApiResponse.error("获取数据失败");
        }

        return ApiResponse.success(list);

    }

}
