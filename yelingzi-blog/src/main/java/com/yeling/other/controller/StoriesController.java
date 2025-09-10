package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.entity.ApiResponse;
import com.yeling.other.domian.entity.Stories;
import com.yeling.other.service.StoriesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class StoriesController {

    @Autowired
    private StoriesService storiesService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/stories/add")
    public ApiResponse addStories(@RequestBody Stories stories){

        User user = UserContext.getUser();
        Integer i = storiesService.addStory(stories, user);
        if(i == 0){
            return ApiResponse.error("故事发布失败");
        }
        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/stories/del/{id}")
    public ApiResponse deleteStories(@PathVariable Integer id){

        log.info("删除故事{}",id);
        storiesService.deleteStory(id);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/stories/{num}")
    public ApiResponse showStories(@PathVariable Integer num){

        log.info("查看朋友圈");
        List<Stories> stories = storiesService.showPageStories(num);

        return ApiResponse.success(stories);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/stories/{num}")
    public ApiResponse showTheStories(@PathVariable Integer num){

        log.info("查看故事");
        List<Stories> stories = storiesService.showStories(num);

        return ApiResponse.success(stories);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/stories/count")
    public ApiResponse getStoriesCount() {

        Integer integer = storiesService.getStoriesCount();

        return ApiResponse.success(integer);
    }


}
