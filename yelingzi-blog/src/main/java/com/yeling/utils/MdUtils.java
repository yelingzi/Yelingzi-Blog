package com.yeling.utils;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MdUtils {

    /**
     * 保存 Markdown 文件
     *
     * @param title         Markdown 文件的标题
     * @param content       Markdown 文件的内容
     * @param fileName      自定义文件名（可选）
     * @param save          保存的子目录
     * @param savePath      文件保存的根路径
     * @param relativePath  文件的相对路径前缀
     * @param useUUID       是否使用UUID生成文件名
     * @return 文件的相对路径
     */
    public static String saveMarkdown(
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
            newName = fileName + ".md";
        } else {
            newName = UUID.randomUUID().toString() + ".md";
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
    public static String saveMarkdown(
            String title,
            String content,
            String save,
            String savePath,
            String relativePath) {
        return saveMarkdown(title, content, null, save, savePath, relativePath,  true );
    }

    public static String extractPlainText(String content, int maxLength) {
        if (content == null) return "";

        // 1. 使用commonmark将Markdown转换为HTML
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(parser.parse(content));

        // 2. 使用Jsoup解析HTML并保留指定标签
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("p, span, div");

        StringBuilder textBuilder = new StringBuilder();
        for (Element element : elements) {
            textBuilder.append(element.text()).append(" ");
        }
        String text = textBuilder.toString().trim();

        // 3. 规范化文本
        text = text.replaceAll("\\s+", " ").trim();

        // 4. 智能截断
        if (text.length() > maxLength) {
            // 查找截断位置，允许在空格、逗号或句号处截断
            Pattern pattern = Pattern.compile("[，\\。\\s]");
            Matcher matcher = pattern.matcher(text);
            int truncateAt = maxLength;
            int lastMatch = 0;

            while (matcher.find()) {
                int matchStart = matcher.start();
                if (matchStart <= maxLength && matchStart > lastMatch) {
                    lastMatch = matchStart;
                    truncateAt = matchStart;
                }
            }

            if (truncateAt > 0) {
                text = text.substring(0, truncateAt) + "...";
            } else {
                text = text.substring(0, maxLength) + "...";
            }
        }

        return text;
    }

}
