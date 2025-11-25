package com.yeling.yelingziblog.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ImageUtils {

    @Value("${file.upload.savePath}")
    private String defaultSavePath;

    @Value("${file.upload.relativePath}")
    private String defaultRelativePath;

    @Value("${file.upload.allowedTypes:image/jpg,image/jpeg,image/png}")
    private String defaultAllowedTypes;

    @Value("${file.upload.maxSize:10485760}")
    private long defaultMaxSize;

    // 预计算允许的文件类型集合
    private Set<String> getAllowedTypesSet(String allowedTypes) {
        return Arrays.stream(allowedTypes.split(","))
                .collect(Collectors.toSet());
    }

    /**
     * 图片上传配置类
     */
    public static class UploadConfig {
        private String saveSubDir;
        private String savePath;
        private String relativePath;
        private String allowedTypes;
        private Long maxSize;
        private Boolean useUUID;
        private Boolean validateType;
        private Boolean validateSize;

        // Builder 模式方法
        public static UploadConfig builder() {
            return new UploadConfig();
        }

        public UploadConfig saveSubDir(String saveSubDir) {
            this.saveSubDir = saveSubDir;
            return this;
        }

        public UploadConfig savePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public UploadConfig relativePath(String relativePath) {
            this.relativePath = relativePath;
            return this;
        }

        public UploadConfig allowedTypes(String allowedTypes) {
            this.allowedTypes = allowedTypes;
            return this;
        }

        public UploadConfig maxSize(Long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public UploadConfig useUUID(Boolean useUUID) {
            this.useUUID = useUUID;
            return this;
        }

        public UploadConfig validateType(Boolean validateType) {
            this.validateType = validateType;
            return this;
        }

        public UploadConfig validateSize(Boolean validateSize) {
            this.validateSize = validateSize;
            return this;
        }

        public UploadConfig build() {
            // 可以在这里添加必要的验证
            if (saveSubDir == null || saveSubDir.trim().isEmpty()) {
                throw new IllegalArgumentException("保存子目录不能为空");
            }
            return this;
        }
    }

    /**
     * 主要上传方法
     */
    public String uploadImage(MultipartFile file, UploadConfig config) {
        // 参数校验
        validateFile(file);

        // 使用配置或默认值
        String savePath = Optional.ofNullable(config.savePath).orElse(defaultSavePath);
        String relativePath = Optional.ofNullable(config.relativePath).orElse(defaultRelativePath);
        String allowedTypes = Optional.ofNullable(config.allowedTypes).orElse(defaultAllowedTypes);
        long maxSize = Optional.ofNullable(config.maxSize).orElse(defaultMaxSize);
        boolean useUUID = Optional.ofNullable(config.useUUID).orElse(true);
        boolean validateType = Optional.ofNullable(config.validateType).orElse(true);
        boolean validateSize = Optional.ofNullable(config.validateSize).orElse(true);

        // 文件验证
        validateFileContent(file, allowedTypes, maxSize, validateType, validateSize);

        // 创建目录
        File targetDir = createDirectory(savePath, config.saveSubDir);

        // 生成文件名
        String fileName = generateFileName(file.getOriginalFilename(), useUUID);

        // 保存文件
        saveFile(file, new File(targetDir, fileName));

        // 返回相对路径
        return buildRelativePath(relativePath, config.saveSubDir, fileName);
    }

    /**
     * 简化方法 - 使用默认配置上传图片
     */
    public String uploadImage(MultipartFile file, String saveSubDir) {
        UploadConfig config = UploadConfig.builder()
                .saveSubDir(saveSubDir)
                .build();
        return uploadImage(file, config);
    }

    /**
     * 简化方法 - 自定义验证规则
     */
    public String uploadImage(MultipartFile file, String saveSubDir,
                              boolean validateType, boolean validateSize) {
        UploadConfig config = UploadConfig.builder()
                .saveSubDir(saveSubDir)
                .validateType(validateType)
                .validateSize(validateSize)
                .build();
        return uploadImage(file, config);
    }

    /**
     * 简化方法 - 自定义文件大小限制
     */
    public String uploadImage(MultipartFile file, String saveSubDir, long maxSize) {
        UploadConfig config = UploadConfig.builder()
                .saveSubDir(saveSubDir)
                .maxSize(maxSize)
                .build();
        return uploadImage(file, config);
    }

    // ========== 私有方法 ==========

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }

    private void validateFileContent(MultipartFile file, String allowedTypes,
                                     long maxSize, boolean validateType, boolean validateSize) {
        if (validateType) {
            String contentType = file.getContentType();
            Set<String> allowedTypesSet = getAllowedTypesSet(allowedTypes);
            if (contentType == null || !allowedTypesSet.contains(contentType)) {
                throw new IllegalArgumentException("不支持的文件类型: " + contentType);
            }
        }

        if (validateSize && file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("文件大小超过限制: %d bytes (最大允许: %d bytes)",
                            file.getSize(), maxSize));
        }
    }

    private File createDirectory(String savePath, String saveSubDir) {
        File dir = new File(savePath, saveSubDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建目录失败: " + dir.getAbsolutePath());
        }
        return dir;
    }

    private String generateFileName(String originalFilename, boolean useUUID) {
        if (!useUUID) {
            return originalFilename;
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        return UUID.randomUUID().toString() + extension;
    }

    private void saveFile(MultipartFile file, File targetFile) {
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败: " + targetFile.getAbsolutePath(), e);
        }
    }

    private String buildRelativePath(String relativePath, String saveSubDir, String fileName) {
        // 确保路径格式正确
        String cleanRelativePath = relativePath.endsWith("/") ?
                relativePath : relativePath + "/";
        String cleanSubDir = saveSubDir.startsWith("/") ?
                saveSubDir.substring(1) : saveSubDir;
        cleanSubDir = cleanSubDir.endsWith("/") ?
                cleanSubDir.substring(0, cleanSubDir.length() - 1) : cleanSubDir;

        return cleanRelativePath + cleanSubDir + "/" + fileName;
    }

    // ========== Getter 方法 ==========

    public String getDefaultSavePath() {
        return defaultSavePath;
    }

    public String getDefaultRelativePath() {
        return defaultRelativePath;
    }

    public String getDefaultAllowedTypes() {
        return defaultAllowedTypes;
    }

    public long getDefaultMaxSize() {
        return defaultMaxSize;
    }
}