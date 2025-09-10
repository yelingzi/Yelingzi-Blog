package com.yeling.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class ImageUtils {


    /**
     * 上传图片
     *
     * @param multipartFile 文件对象
     * @param save          保存的子目录
     * @param savePath      文件保存的根路径
     * @param relativePath  文件的相对路径前缀
     * @param allowedTypes  允许的文件类型，逗号分隔
     * @param maxSize       允许的最大文件大小（字节）
     * @param useUUID       是否使用UUID生成文件名
     * @param validateType  是否验证文件类型
     * @param validateSize  是否验证文件大小
     * @return 文件的相对路径
     */
    public static String uploadImage(
            MultipartFile multipartFile,
            String save,
            String savePath,
            String relativePath,
            String allowedTypes,
            long maxSize,
            boolean useUUID,
            boolean validateType,
            boolean validateSize) {

        // 获取原始文件名
        String oriName = multipartFile.getOriginalFilename();
        if (oriName == null || oriName.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 验证文件类型
        if (validateType) {
            String contentType = multipartFile.getContentType();
            if (contentType == null || !Arrays.asList(allowedTypes.split(",")).contains(contentType)) {
                throw new IllegalArgumentException("不允许的文件类型");
            }
        }

        // 验证文件大小
        if (validateSize) {
            if (multipartFile.getSize() > maxSize) {
                throw new IllegalArgumentException("文件大小超过限制");
            }
        }

        // 判断目录是否存在并创建
        File rootDir = new File(savePath + save);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        // 生成新文件名
        String newName;
        if (useUUID) {
            newName = UUID.randomUUID().toString() + oriName.substring(oriName.lastIndexOf("."));
        } else {
            newName = oriName;
        }

        File saveFile = new File(rootDir, newName);

        // 转存到指定文件中
        try {
            multipartFile.transferTo(saveFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        return relativePath + save + "/" + newName;
    }

    /**
     * 简化调用，使用默认参数
     */
    public static String uploadImage(
            MultipartFile multipartFile,
            String save,
            String savePath,
            String relativePath,
            String allowedTypes,
            long maxSize) {
        return uploadImage(multipartFile, save, savePath, relativePath, allowedTypes, maxSize, true, true, true);
    }
}
