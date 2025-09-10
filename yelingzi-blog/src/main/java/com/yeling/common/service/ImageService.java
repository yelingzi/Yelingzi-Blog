package com.yeling.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

//    List<Image> getArticle(Integer start, Integer num);

    String uploadImage(MultipartFile multipartFile, String save);

    String addOrderCover(MultipartFile multipartFile);

}
