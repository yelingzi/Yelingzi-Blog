package com.yeling.yelingziblog.common.service.Impl;

import com.yeling.yelingziblog.article.mapper.ArticleMapper;
import com.yeling.yelingziblog.common.service.ImageService;
import com.yeling.yelingziblog.common.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ArticleMapper articleMapper;


    @Autowired
    private ImageUtils imageUtils;

    @Value("${file.upload.maxSize:5242880}") // 默认最大5MB
    private long maxSize;

//    @Override
//    public List<Image> getArticle(Integer start, Integer num) {
//        List<Article> articles = articleMapper.getArticleCover(start, num);
//        List<Image> images = articles.stream()
//                .map(this::createImage)
//                .collect(Collectors.toList());
//        return images;
//    }
//
//    private Image createImage(Article article) {
//        String imagePath = article.getPictureCover();
//        try {
//            Path path = Paths.get(imagePath);
//            byte[] data = Files.readAllBytes(path);
//
//            Image image = new Image();
//            image.setData(data);
//
//            return image;
//        } catch (IOException e) {
//            // 处理异常
//            return null; // 或者抛出自定义异常
//        }
//    }

    public String uploadImage(MultipartFile multipartFile, String save){

        return imageUtils.uploadImage(multipartFile, save, maxSize);
    }

    @Override
    public String addOrderCover(MultipartFile multipartFile){
        return imageUtils.uploadImage(multipartFile, "order_menu/", maxSize);
    }


}
