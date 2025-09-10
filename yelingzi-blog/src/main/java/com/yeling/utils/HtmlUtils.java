package com.yeling.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class HtmlUtils {

    public static String stripHtmlTags(String html) {

        if(html.isEmpty()){
            return "";
        }
        // 解析HTML字符串
        Document doc = Jsoup.parse(html);

        // 获取文档中的纯文本内容，不包括HTML标签，并返回纯文本内容
        return doc.text();
    }

    /**
     * 保存 Html 文件
     *
     * @param title         Html 文件的标题
     * @param content       Html 文件的内容
     * @param fileName      自定义文件名（可选）
     * @param save          保存的子目录
     * @param savePath      文件保存的根路径
     * @param relativePath  文件的相对路径前缀
     * @param useUUID       是否使用UUID生成文件名
     * @return 文件的相对路径
     */
    public static String saveHtml(
            String title,
            String content,
            String fileName,
            String save,
            String savePath,
            String relativePath,
            boolean useUUID) {



        // 判断目录是否存在并创建
        File rootDir = new File(savePath + save);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        // 生成新文件名
        String newName;
        if (fileName != null && !fileName.isEmpty() && !useUUID) {
            newName = fileName + ".html";
        } else {
            newName = UUID.randomUUID().toString() + ".html";
        }

        File saveFile = new File(rootDir, newName);

        // 写入文件内容
        try {
            Files.write(saveFile.toPath(), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }

        return relativePath + save + newName;
    }

    /**
     * 简化调用，使用默认参数
     */
    public static String saveHtml(
            String title,
            String content,
            String save,
            String savePath,
            String relativePath) {
        return saveHtml(title, content, null, save, savePath, relativePath,  true );
    }



}
