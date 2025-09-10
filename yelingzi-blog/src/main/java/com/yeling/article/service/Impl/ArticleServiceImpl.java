package com.yeling.article.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.Article;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentInfo;
import com.yeling.article.vo.response.*;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.ArticleReq;
import com.yeling.article.mapper.ArticleCategoryMapper;
import com.yeling.article.mapper.ArticleMapper;
import com.yeling.article.mapper.ArticleTagMapper;
import com.yeling.article.service.ArticleService;
import com.yeling.utils.ImageUtils;
import com.yeling.utils.JwtUtils;
import com.yeling.utils.MdUtils;

import com.yeling.utils.HtmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    /**
     *
     */
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;
    private final ObjectMapper objectMapper;

    public ArticleServiceImpl( ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Value("${file.upload.saveArticlePath}")
    private String savePath;

    @Value("${file.upload.relativeArticlePath}")
    private String relativePath;

    @Value("${file.upload.savePath}")
    private String imageSavePath;

    @Value("${file.upload.relativePath}")
    private String imageRelativePath;

    @Value("${file.upload.allowedTypes:image/jpg,image/jpeg,image/png,image/gif}")
    private String allowedTypes;

    @Value("${file.upload.maxSize:5242880}") // 默认最大5MB
    private long maxSize;

    @Transactional
    @Override
    public ArticleResp findArticleById(Integer id) {

        Article article = articleMapper.findArticleById(id);

        articleMapper.addArticleReadCount(id);


        ObjectMapper objectMapper = new ObjectMapper();

        ArticleResp articleResp = new ArticleResp();
        // 基本字段转换
        articleResp.setId(article.getId());
        articleResp.setNickname(article.getNickname());
        articleResp.setTitle(article.getTitle());
        articleResp.setContent(article.getContent());
        articleResp.setArticleCover(article.getArticleCover());
        articleResp.setState(article.getState());
        articleResp.setUserId(article.getUserId());
        articleResp.setUserAvatar(article.getUserAvatar());
        articleResp.setIsOriginal(article.getIsOriginal());
        articleResp.setOriginalUrl(article.getOriginalUrl());
        articleResp.setIsTop(article.getIsTop());
        articleResp.setStarCount(article.getStarCount());
        articleResp.setCreateTime(article.getCreateTime());
        articleResp.setUpdateTime(article.getUpdateTime());
        articleResp.setReadCount(article.getReadCount());
        articleResp.setCommentCount(article.getCommentCount());
        articleResp.setLikeCount(article.getLikeCount());

        // JSON字段转换
        try {
            // 分类字段
            if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                CategoryResp category = objectMapper.readValue(article.getCategory(), CategoryResp.class);
                articleResp.setCategory(category);
            }

            // 标签字段
            if (article.getTagList() != null && !article.getTagList().isEmpty()) {
                List<TagResp> tags = objectMapper.readValue(article.getTagList(), new TypeReference<List<TagResp>>() {});
                articleResp.setTagList(tags);
            }
        } catch (JsonProcessingException e) {
            // JSON解析失败时的处理逻辑
            e.printStackTrace();
        }
        return articleResp;

    }

    @Override
    @Transactional
    public ArticleResp userGetArticleById(Integer id, String jwt){

        Integer uid = JwtUtils.getUserId(jwt);

        articleMapper.addArticleReadCount(id);


        return articleMapper.userGetArticleById(id, uid);
    }

    @Override
    @Transactional
    public Integer add(ArticleReq articleReq, User user) {

        Article article = new Article();

        article.setArticleUrl(MdUtils.saveMarkdown(articleReq.getTitle(), articleReq.getContent(),
                "/md", savePath, relativePath));


        CategoryResp categoryResp = articleCategoryMapper.findCategoryById(articleReq.getCategory());
        if(categoryResp == null){
            return -1;
        }

        articleCategoryMapper.updateCategoryArticleCountById(categoryResp.getId(), 1);

        // 处理分类和标签并转换为 JSON 格式的字符串
        article.setCategory(processCategory(categoryResp));
        article.setTagList(processTags(articleReq.getTagList()));

        if (article.getTagList() == null) {
            return -2;
        }
        updateTagArticleCount(articleReq.getTagList());

        article.setBrief(MdUtils.extractPlainText(articleReq.getContent(), 120));
        article.setUserId(user.getId());
        article.setNickname(user.getNickname());
        article.setUserAvatar(user.getUserAvatar());
        article.setTitle(articleReq.getTitle());
        article.setContent(articleReq.getContent());
        article.setArticleCover(articleReq.getArticleCover());
        article.setIsOriginal(articleReq.getIsOriginal());
        article.setOriginalUrl(articleReq.getOriginalUrl());

        articleMapper.insert(article);

        List<TagResp> list = parseTagList(article.getTagList());

        for (TagResp tagResp : list){
            articleMapper.addArticleTagLink(tagResp.getId(), article.getId());
        }

        return 1;
    }

    @Override
    @Transactional
    public void edit(ArticleReq articleReq, User user){
        try{
            Article article = articleMapper.findArticleById(articleReq.getId());

            article.setArticleUrl(MdUtils.saveMarkdown(articleReq.getTitle(), articleReq.getContent(),
                    "/md", savePath, relativePath));

            article.setBrief(MdUtils.extractPlainText(articleReq.getContent(), 120));
            article.setUserId(user.getId());
            article.setNickname(user.getNickname());
            article.setUserAvatar(user.getUserAvatar());
            article.setTitle(articleReq.getTitle());
            article.setContent(articleReq.getContent());
            article.setArticleCover(articleReq.getArticleCover());
            article.setIsOriginal(articleReq.getIsOriginal());
            article.setOriginalUrl(articleReq.getOriginalUrl());


            CategoryResp categoryResp = articleCategoryMapper.findCategoryById(articleReq.getCategory());
            if(categoryResp == null){
                throw new RuntimeException("分类数据错误");
            }
            CategoryResp categoryResp1 = parseCategory(article.getCategory());
            if(!Objects.equals(categoryResp1.getId(), categoryResp.getId())){
                articleCategoryMapper.updateCategoryArticleCountById(categoryResp.getId(), 1);
                articleCategoryMapper.updateCategoryArticleCountById(categoryResp1.getId(), -1);
            }

            List<TagResp> tags = parseTagList(article.getTagList());

            // 处理分类和标签并转换为 JSON 格式的字符串
            article.setCategory(processCategory(categoryResp));
            article.setTagList(processTags(articleReq.getTagList()));

            if (article.getTagList() == null) {
                throw new RuntimeException("标签数据错误");
            }
            updateTagArticleCount(articleReq.getTagList());

            for(TagResp tag : tags){
                articleTagMapper.updateTagArticleCountById(tag.getId(), -1);
                articleMapper.delArticleTagLink(tag.getId(), article.getId());
            }

            articleMapper.update(article);

            List<TagResp> list = parseTagList(article.getTagList());

            for (TagResp tagResp : list){
                articleMapper.addArticleTagLink(tagResp.getId(), article.getId());
            }

        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public PageResult<ArticleResp> getArticleListByPageForAdmin(Integer page, Integer pageSize) {
        // 查询原始的 Article 数据
        List<Article> articleList = articleMapper.findArticleByPage((page - 1) * pageSize, pageSize);

        // 转换为 ArticleResp 列表
        List<ArticleResp> respList = new ArrayList<>();

        for (Article article : articleList) {
            ArticleResp resp = new ArticleResp();
            // 设置基础字段
            resp.setId(article.getId());
            resp.setTitle(article.getTitle());
            resp.setContent(article.getBrief());
            resp.setArticleCover(article.getArticleCover());
            resp.setStarCount(article.getStarCount());
            resp.setState(article.getState());
            resp.setReadCount(article.getReadCount());
            resp.setCommentCount(article.getCommentCount());
            resp.setUserId(article.getUserId());
            resp.setUserAvatar(article.getUserAvatar());
            resp.setNickname(article.getNickname());
            resp.setLikeCount(article.getLikeCount());
            resp.setIsOriginal(article.getIsOriginal());
            resp.setOriginalUrl(article.getOriginalUrl());
            resp.setIsTop(article.getIsTop());
            // 解析 JSON 字段
            resp.setCategory(parseCategory(article.getCategory()));
            resp.setTagList(parseTagList(article.getTagList()));
            // 设置其他字段...
            resp.setCreateTime(article.getCreateTime());
            resp.setUpdateTime(article.getUpdateTime());
            respList.add(resp);
        }

        // 构建分页结果
        PageResult<ArticleResp> result = new PageResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(articleMapper.findArticleCount());
        result.setData(respList);
        return result;
    }

    @Override
    public PageResult<ArticleResp> userGetArticleListByPage(Integer page, Integer pageSize, Integer userId){
        // 查询原始的 Article 数据
        List<Article> articleList = articleMapper.findArticleByPageByUserId((page - 1) * pageSize, pageSize, userId);

        // 转换为 ArticleResp 列表
        List<ArticleResp> respList = new ArrayList<>();

        for (Article article : articleList) {
            ArticleResp resp = new ArticleResp();
            // 设置基础字段
            resp.setId(article.getId());
            resp.setTitle(article.getTitle());
            resp.setContent(article.getBrief());
            resp.setArticleCover(article.getArticleCover());
            resp.setStarCount(article.getStarCount());
            resp.setState(article.getState());
            resp.setReadCount(article.getReadCount());
            resp.setCommentCount(article.getCommentCount());
            resp.setUserId(article.getUserId());
            resp.setUserAvatar(article.getUserAvatar());
            resp.setNickname(article.getNickname());
            resp.setLikeCount(article.getLikeCount());
            resp.setIsOriginal(article.getIsOriginal());
            resp.setOriginalUrl(article.getOriginalUrl());
            resp.setIsTop(article.getIsTop());
            // 解析 JSON 字段
            resp.setCategory(parseCategory(article.getCategory()));
            resp.setTagList(parseTagList(article.getTagList()));
            // 设置其他字段...
            resp.setCreateTime(article.getCreateTime());
            resp.setUpdateTime(article.getUpdateTime());
            respList.add(resp);
        }

        // 构建分页结果
        PageResult<ArticleResp> result = new PageResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(articleMapper.findArticleCountByUserId(userId));
        result.setData(respList);
        return result;
    }

    @Override
    public PageResult<ArticleResp> getArticleListByPage(Integer page, Integer pageSize) {
        // 计算分页查询的起始索引
        Integer start = (page - 1) * pageSize;

        // 查询总记录数
        Integer total = articleMapper.countArticleRespList();

        if (total == null) {
            total = 0;
        }

        if (start >= total) {
            return null;
        }

        // 确保 pageSize 不超过总记录数减去起始索引
        Integer actualPageSize = Math.min(pageSize, total - start);
        log.info("总查询文章："+actualPageSize);

        // 先查询顶置文章
        List<Article> topArticles = articleMapper.findTopArticleRespListByPage(start, actualPageSize);
        Integer topArticleCount = topArticles.size();
        log.info("顶置文章："+topArticleCount);
        log.info("查询下标："+start);
        // 如果顶置文章数量不足 pageSize，则补充最新文章
        List<Article> latestArticles = new ArrayList<>();
        if (topArticleCount < actualPageSize && topArticleCount > 0) {
            Integer remaining = actualPageSize - topArticleCount;
            latestArticles = articleMapper.findLatestArticleRespListByPage(0, remaining);
        } else if (topArticleCount == 0) {
            Integer topCount = articleMapper.findTopArticleCount();
            int i = topCount != null ? topCount : 0;
            log.info("顶置文章总数："+i);
            latestArticles = articleMapper.findLatestArticleRespListByPage(start - i, actualPageSize);

        }

        // 合并顶置文章和最新文章
        List<Article> combinedArticles = new ArrayList<>(topArticles);
        combinedArticles.addAll(latestArticles);

        // 将Article对象转换为ArticleResp对象
        List<ArticleResp> articleRespList = convertToArticleRespList(combinedArticles);

        // 构造分页结果
        PageResult<ArticleResp> pageResult = new PageResult<>();
        pageResult.setData(articleRespList);
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);


        pageResult.setTotal(total);

        return pageResult;
    }

    private List<ArticleResp> convertToArticleRespList(List<Article> articles) {
        List<ArticleResp> articleRespList = new ArrayList<>();

        for (Article article : articles) {

            articleRespList.add(ArticleToArticleResp(article));
        }

        return articleRespList;
    }

    private ArticleResp ArticleToArticleResp(Article article){
        ObjectMapper objectMapper = new ObjectMapper();

        ArticleResp articleResp = new ArticleResp();
        // 基本字段转换
        articleResp.setId(article.getId());
        articleResp.setNickname(article.getNickname());
        articleResp.setTitle(article.getTitle());
        articleResp.setContent(article.getBrief());
        articleResp.setArticleCover(article.getArticleCover());
        articleResp.setState(article.getState());
        articleResp.setUserId(article.getUserId());
        articleResp.setUserAvatar(article.getUserAvatar());
        articleResp.setIsOriginal(article.getIsOriginal());
        articleResp.setOriginalUrl(article.getOriginalUrl());
        articleResp.setIsTop(article.getIsTop());
        articleResp.setStarCount(article.getStarCount());
        articleResp.setCreateTime(article.getCreateTime());
        articleResp.setUpdateTime(article.getUpdateTime());
        articleResp.setReadCount(article.getReadCount());
        articleResp.setCommentCount(article.getCommentCount());
        articleResp.setLikeCount(article.getLikeCount());

        // JSON字段转换
        try {
            // 分类字段
            if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                CategoryResp category = objectMapper.readValue(article.getCategory(), CategoryResp.class);
                articleResp.setCategory(category);
            }

            // 标签字段
            if (article.getTagList() != null && !article.getTagList().isEmpty()) {
                List<TagResp> tags = objectMapper.readValue(article.getTagList(), new TypeReference<List<TagResp>>() {});
                articleResp.setTagList(tags);
            }
        } catch (JsonProcessingException e) {
            // JSON解析失败时的处理逻辑
            e.printStackTrace();
        }
        return articleResp;
    }

    @Override
    public String uploadArticleImage(MultipartFile multipartFile){
        return uploadImage(multipartFile, "/article/image");
    }

    @Override
    public String uploadArticleCover(MultipartFile multipartFile){
        return uploadImage(multipartFile, "/article/cover");
    }

    public String uploadImage(MultipartFile multipartFile, String path){
        return ImageUtils.uploadImage(multipartFile, path, imageSavePath, imageRelativePath, allowedTypes, maxSize);
    }


    @Override
    public List<ArticleResp> getArticleList(){
        return articleMapper.findArticleRespList();
    }

    @Override
    public List<ArticleResp> getRecommendArticleList(){
        List<Article> articles = articleMapper.findArticleRespListByLikeCountDescPage(3);
        return convertToArticleRespList(articles);
    }

    private String processCategory(CategoryResp categoryResp){
        JSONObject categoryObject = new JSONObject();
        categoryObject.put("id", categoryResp.getId());
        categoryObject.put("categoryName", categoryResp.getCategoryName());
        return  categoryObject.toString();
    }


    /**
     * 将标签字符串转换为 JSON 格式
     *
     * @param tagList 标签字符串，格式为 "1%%2%%3"
     * @return JSON 格式的标签字符串
     */
    private String processTags(String tagList) {
        JSONArray tagArray = new JSONArray();

            String[] tagIds = tagList.split("%%");
            for (String tagId : tagIds) {
                try {
                    Integer id = Integer.parseInt(tagId);
                    TagResp tag = articleTagMapper.findTagById(id);
                    if (tag != null) {
                        JSONObject tagObject = new JSONObject();
                        tagObject.put("id", tag.getId());
                        tagObject.put("tagName", tag.getTagName());
                        tagArray.add(tagObject);
                    }
                } catch (NumberFormatException e) {
                    // 处理非法的标签 ID
                    return null;
                }
            }

        return tagArray.toString();
    }

    /**
     * 截断字符串至指定长度，并添加省略号
     *
     * @param content 需要截断的字符串
     * @param maxLength 最大允许长度
     * @return 截断后的字符串
     */
    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }

    /**
     * 更新标签表的文章数
     *
     * @param tagListStr 标签字符串，格式为 "1%%2%%3"
     */
    private void updateTagArticleCount(String tagListStr) {
        if (tagListStr == null || tagListStr.trim().isEmpty()) {
            return;
        }

        String[] tagIds = tagListStr.split("%%");

        for (String tagId : tagIds) {
            try {
                Integer id = Integer.parseInt(tagId.trim());
                articleTagMapper.updateTagArticleCountById(id, 1);
            } catch (NumberFormatException e) {
                // 处理非法的标签 ID
                throw new IllegalArgumentException("非法的标签 ID: " + tagId, e);
            }
        }

    }

    private CategoryResp parseCategory(String categoryJson) {
        if (categoryJson == null || categoryJson.trim().isEmpty()) {
            return null;
        }
        try {
            JSONObject json = JSONObject.parseObject(categoryJson);
            CategoryResp category = new CategoryResp();
            category.setId(json.getIntValue("id"));
            category.setCategoryName(json.getString("categoryName"));
            return category;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<TagResp> parseTagList(String tagListJson) {
        List<TagResp> tagList = new ArrayList<>();
        if (tagListJson == null || tagListJson.trim().isEmpty()) {
            return tagList;
        }
        try {
            JSONArray jsonArray = JSONArray.parseArray(tagListJson);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                TagResp tag = new TagResp();
                tag.setId(json.getIntValue("id"));
                tag.setTagName(json.getString("tagName"));
                tagList.add(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagList;
    }



    @Override
    public void deleteArticle(Long id){
        articleMapper.updateArticleState(1,id);
    }

    @Override
    public void regainArticle(Long id){
        articleMapper.updateArticleState(0,id);
    }

    @Override
    public void updateArticleTop(Long id, Integer isTop){
        articleMapper.updateArticleTop(id, isTop);
    }

    @Override
    public List<ArticleSortByCategoryResp> getArticleListFromCategory() {
        List<SimpleArticleResp> simpleArticleRespList = articleMapper.findSimpleArticleList();

        // 创建一个映射，用于按分类分组文章
        Map<Integer, ArticleSortByCategoryResp> categoryMap = new LinkedHashMap<>();

        for (SimpleArticleResp article : simpleArticleRespList) {
            // 解析分类信息
            CategoryResp category = parseCategory(article.getCategory());

            if (category != null) {
                // 使用分类 ID 作为映射的键
                Integer categoryId = category.getId();

                // 如果映射中不存在该分类，则创建一个新的 ArticleSortByCategoryResp 对象
                if (!categoryMap.containsKey(categoryId)) {
                    ArticleSortByCategoryResp articleSortByCategoryResp = new ArticleSortByCategoryResp();
                    articleSortByCategoryResp.setId(categoryId);
                    articleSortByCategoryResp.setCategoryName(category.getCategoryName());
                    articleSortByCategoryResp.setSimpleArticleList(new ArrayList<>());
                    categoryMap.put(categoryId, articleSortByCategoryResp);
                }

                // 将文章添加到对应分类的文章列表中
                categoryMap.get(categoryId).getSimpleArticleList().add(article);
            }
        }

        // 将映射中的值转换为列表
        return new ArrayList<>(categoryMap.values());
    }







    //点赞
    @Override
    @Transactional
    public Integer articleLike(Integer id, Integer userId){

        Integer userLikeByArticleId = articleMapper.userLikeByArticleId(id, userId);
        if(userLikeByArticleId == 0){

            articleMapper.articleLike(id, userId);
            //点赞加1
            articleMapper.updateArticleLike(id, 1);
            return 1;
        }


        return -1;

    }

    @Transactional
    @Override
    public Integer articleUnlike(Integer id, Integer userId){

        Integer userLikeByArticleId = articleMapper.userLikeByArticleId(id, userId);

        if(userLikeByArticleId == 1){
            articleMapper.articleUnlike(id, userId);

            //点赞减1
            articleMapper.updateArticleLike(id, -1);
            return 1;
        }

        return -1;

    }

    //评论
    @Transactional
    @Override
    public String articleComment(ArticleComment articleComment, String jwt){
        //获取时间
        LocalDateTime localDateTime = LocalDateTime.now();

        //去标签
        articleComment.setContent(HtmlUtils.stripHtmlTags(articleComment.getContent()));

        //敏感词过滤
        articleComment.setContent(SensitiveWordHelper.replace(articleComment.getContent()));

        Integer id = JwtUtils.getUserId(jwt);

        articleComment.setUserId(id);

        articleMapper.articleComment(articleComment);
        return articleComment.getContent();
    }

    @Override
    public List<ArticleCommentInfo> getArticleComment(Integer id) {
        List<ArticleCommentInfo> articleCommentInfos = articleMapper.getArticleComment(id);
        List<ArticleCommentInfo> articleCommentInfoList = new ArrayList<>();
        Map<Integer, ArticleCommentInfo> parentCommentMap = new HashMap<>();

        // 第一次遍历，分离父评论和子评论
        for (ArticleCommentInfo comment : articleCommentInfos) {
            if (comment.getParentId() == 0) {
                // 添加父评论到列表和映射中
                articleCommentInfoList.add(comment);
                parentCommentMap.put(comment.getId(), comment);
            } else {
                // 添加子评论到父评论的子列表中
                ArticleCommentInfo parentComment = parentCommentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getChildren().add(comment);
                }
            }
        }

        return articleCommentInfoList;
    }

    @Override
    public List<TagFindArticleResp> getArticleListByTag(Integer tagId) {
        // 通过标签ID查询所有文章ID
        List<Integer> articleIds = articleMapper.findArticleIdByTagId(tagId);

        if (articleIds.isEmpty()) {
            return List.of();
        }

        log.info(articleIds.toString());
        // 通过文章ID列表一次性查询所有文章
        List<Article> articles = articleMapper.findArticlesByIds(articleIds);

        // 使用流式操作转换为响应对象
        return articles.stream()
                .map(this::convertToTagFindArticleResp)
                .collect(Collectors.toList());
    }

    private TagFindArticleResp convertToTagFindArticleResp(Article article) {
        TagFindArticleResp resp = new TagFindArticleResp();
        resp.setContent(truncateContent(article.getBrief(), 60));
        resp.setArticleCover(article.getArticleCover());
        resp.setId(article.getId());
        resp.setTitle(article.getTitle());
        resp.setCreateTime(article.getCreateTime());

        // 使用Jackson解析JSON
        try {
            resp.setCategory(objectMapper.readValue(article.getCategory(), CategoryResp.class));
            resp.setTagList(objectMapper.readValue(article.getTagList(), new TypeReference<List<TagResp>>() {}));
        } catch (Exception e) {
            // 处理JSON解析异常
            log.error("Failed to parse JSON: {}", e.getMessage(), e);
        }

        return resp;
    }

    @Override
    public List<ArticleResp> searchArticle(String search){
        List<Article> article = articleMapper.findArticleRespListBySearch(search);

        return convertToArticleRespList(article);
    }

    @Override
    public Integer getArticleLike(Integer id, Integer userId){
        return articleMapper.userLikeByArticleId(id, userId);
    }


    @Override
    @Transactional
    public void addArticleComment(Integer id){
        //评论数加一
        articleMapper.articleCommentPublish(id, 1);
        Integer articleId = articleMapper.getArticleIdByComment(id);
        articleMapper.updateArticleCommentCount(articleId, 1);
    }

    @Override
    public List<ArticleComment> getAllArticleComment(Integer page){
        //获取数据
        page = (page - 1) * 10;
        return articleMapper.getAllArticleComment(page, 10);
    }

    @Override
    public Integer getAllArticleCommentCount(){
        return articleMapper.getAllArticleCommentCount();
    }

    @Override
    public List<ArticleComment> getUnPublishArticleComment(Integer page){
        page = (page - 1) * 10;
        return articleMapper.getUnPublishArticleComment(page, 10);
    }

    @Override
    public Integer getUnPublishArticleCommentCount(){
        return articleMapper.getUnPublishArticleCommentCount();
    }

}