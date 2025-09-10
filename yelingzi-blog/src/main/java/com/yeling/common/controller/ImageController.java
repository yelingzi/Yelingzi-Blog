package com.yeling.common.controller;

import com.yeling.entity.ApiResponse;
import com.yeling.common.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;


    @Value("${file.upload.savePath}")
    private String savePath;

    @Value("${file.upload.relativePath}")
    private String relativePath;


//    /**
//     * 文章封面
//     */
//    @CrossOrigin(origins = "http://localhost:5173")
//    @GetMapping("/api/image/article")
//    public  Result imageArticleCover(@RequestParam Integer start, @RequestParam Integer num)
//    {
//
//        log.info("获取文章封面图片{}开始，{}张",start,num);
//        return Result.success(imageService.getArticle(start,num));
//    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/admin/image")
    public ApiResponse image(@RequestParam("image") MultipartFile multipartFile,
                             @RequestParam("path") String save)  {

        String image = imageService.uploadImage(multipartFile,save);

        if (!image.isEmpty()) {
            // 修改成功  重定向到list接口
            return ApiResponse.success(image);
        }
        return ApiResponse.error("上传失败");


    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/desk/cover")
    public ApiResponse addMenu(@RequestParam("dishCover") MultipartFile multipartFile){

        log.info("添加菜品图片:");
        String string = imageService.addOrderCover(multipartFile);

        return ApiResponse.success(string);
    }



}
