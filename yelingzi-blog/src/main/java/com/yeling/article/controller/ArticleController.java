package com.yeling.article.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentInfo;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.ArticleReq;
import com.yeling.article.vo.response.ArticleResp;
import com.yeling.article.vo.response.ArticleSortByCategoryResp;
import com.yeling.article.vo.response.TagFindArticleResp;
import com.yeling.article.service.ArticleService;
import com.yeling.entity.UserContext;
import com.yeling.utils.IpUtils;
import com.yeling.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;



    /**
     *
     * 用id查看文章
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/article/{id}")
    public ApiResponse getArticle(@Min(value = 1, message = "文章Id必须大于 0") @PathVariable Integer id) {
        log.info("查看文章：{}", id);
        ArticleResp a = articleService.findArticleById(id);

        if (a != null){
            return ApiResponse.success(a);

        }
        return ApiResponse.error("获取文章错误!");
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/user/article/{id}")
    public ApiResponse userGetArticle(@PathVariable Integer id, @RequestHeader("Authorization") String jwtToken) {
        log.info("查看文章：{}", id);
        ArticleResp a = articleService.userGetArticleById(id, jwtToken);

        if (a != null){
            return ApiResponse.success(a);

        }
        return ApiResponse.error("获取文章错误!");
    }

    /**
     * 新增文章
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article")
    public ApiResponse add(@Valid @RequestBody ArticleReq article) {

        User user = UserContext.getUser();

        log.info("新增文章：{}, 作者：{},名{}", article.getTitle(), Objects.requireNonNull(user).getId(),user.getNickname());
        //调用service新增文章
        Integer i = articleService.add(article, user);
        if(i == -1){
            return ApiResponse.error("文章分类参数错误");
        } else if (i == -2) {
            return ApiResponse.error("文章标签参数错误");
        }
        return ApiResponse.success();
    }

    /**
     * 更新文章
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/update")
    public ApiResponse editArticle(@Valid @RequestBody ArticleReq article) {

        User user = UserContext.getUser();

        log.info("更新文章：{}, 作者：{},名{}", article.getTitle(), Objects.requireNonNull(user).getId(),user.getNickname());

        try {
            articleService.edit(article, user);
        } catch (Exception e) {
            if(e.getMessage().length() > 30){
                return ApiResponse.error(500, "服务出错！");
            }
            return ApiResponse.error(e.getMessage());
        }

        return ApiResponse.success();
    }

    /**
     * 新增文章封面
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/upload/cover")
    public ApiResponse uploadArticleCover(@RequestParam("image") MultipartFile multipartFile) {
        User user = UserContext.getUser();
        log.info("添加文章封面:{}", user.getId());

        String path = articleService.uploadArticleCover(multipartFile);
        if(path.isEmpty()){
            return ApiResponse.error("封面上传失败");
        }
        return ApiResponse.success(path);
    }


    /**
     * 保存文章图片
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/upload/image")
    public ApiResponse articleImageUpload(@RequestParam("image") MultipartFile multipartFile){
        User user = UserContext.getUser();
        log.info("添加文章图片:{}", user.getId());

        String path = articleService.uploadArticleImage(multipartFile);
        if(path.isEmpty()){
            return ApiResponse.error("图片上传失败");
        }
        return ApiResponse.success(path);
    }

    /**
     * 获取一页文章列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/article/page")
    public ApiResponse getArticleTagListByPage(@RequestParam int page,
                                               @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取文章列表,页数：{},大小：{}, 管理员ID:{}, 邮箱：{}", page, pageSize, user.getId(), user.getEmail());

        PageResult<ArticleResp> articlePageResult = articleService.getArticleListByPageForAdmin(page, pageSize);
        if (articlePageResult == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(articlePageResult);
    }


    /**
     * 获取一页文章列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/page")
    public ApiResponse getArticleTagListByPage(@RequestParam int page,
                                               @RequestParam int pageSize,
                                               HttpServletRequest request) {


        String ip = IpUtils.getIpAddr(request);

        log.info("获取文章列表,页数：{},大小：{}, ip:{}", page, pageSize, ip);

        PageResult<ArticleResp> articlePageResult = articleService.getArticleListByPage(page, pageSize);
        if (articlePageResult == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(articlePageResult);
    }

    /**
     * 获取文章列表
     *
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/list")
    public ApiResponse getArticleList(HttpServletRequest request) {


        String ip = IpUtils.getIpAddr(request);

        log.info("获取文章列表, ip:{}", ip);

        List<ArticleResp> articleResp = articleService.getArticleList();
        if (articleResp == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(articleResp);
    }

    /**
     * 获取文章列表
     *
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/recommend/list")
    public ApiResponse getRecommendArticleList() {


        List<ArticleResp> articleResp = articleService.getRecommendArticleList();
        if (articleResp == null) {
            return ApiResponse.error("获取推荐文章列表失败");
        }
        return ApiResponse.success(articleResp);
    }

    /**
     * 按照分类获取文章列表
     *
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/category/list")
    public ApiResponse getArticleListFromCategory(HttpServletRequest request) {

        String ip = IpUtils.getIpAddr(request);

        log.info("按照分类获取文章列表, ip:{}", ip);

        List<ArticleSortByCategoryResp> articleResp = articleService.getArticleListFromCategory();
        if (articleResp == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(articleResp);
    }



    /**
     * 删除文章
     *
     * @param params  文章数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/del")
    public ApiResponse delArticle(@RequestBody Map<String, Object> params) {

        Long id = Long.valueOf(params.get("id").toString());

        User user = UserContext.getUser();

        log.info("删除文章:{}, 管理员ID:{}, 邮箱：{}", id,user.getId(), user.getEmail());

        articleService.deleteArticle(id);
        return ApiResponse.success();
    }

    /**
     * 复原文章
     *
     * @param params  文章数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/regain")
    public ApiResponse regainArticle(@RequestBody Map<String, Object> params) {

        Long id = Long.valueOf(params.get("id").toString());

        User user = UserContext.getUser();

        log.info("复原文章:{}, 管理员ID:{}, 邮箱：{}", id, user.getId(), user.getEmail());

        articleService.regainArticle(id);
        return ApiResponse.success();
    }


    /**
     * 取消文章顶置
     *
     * @param params  文章数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/top")
    public ApiResponse updateArticleTop(@RequestBody Map<String, Object> params) {

        Long id = Long.valueOf(params.get("id").toString());
        Integer isTop = Integer.valueOf(params.get("isTop").toString());

        User user = UserContext.getUser();
        if(isTop == 1){
            log.info("置顶文章:{}, 管理员ID:{}, 邮箱：{}",id, user.getId(), user.getEmail());
        }else{
            log.info("取消置顶文章:{}, 管理员ID:{}, 邮箱：{}",id, user.getId(), user.getEmail());
        }


        articleService.updateArticleTop(id, isTop);
        return ApiResponse.success();
    }

    /**
     * 根据文章标签获取文章
     *
     * @param tagId    标签Id
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/tag")
    public ApiResponse getArticleListByTag(@RequestParam Integer tagId) {


        log.info("根据文章标签:{},获取文章", tagId);

        List<TagFindArticleResp> list = articleService.getArticleListByTag(tagId);

        if (list == null) {
            return ApiResponse.error("根据文章标签获取文章失败");
        }
        return ApiResponse.success(list);
    }

    /**
     * 搜索文章
     *
     * @param search    搜索文本
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/article/search")
    public ApiResponse searchArticle(@RequestParam String search)
    {
        log.info("搜索文章，搜索内容:{},", search);
        List<ArticleResp> article = articleService.searchArticle(search);
        return ApiResponse.success(article);
    }


//    @CrossOrigin(origins = "http://localhost:5173")
//    @GetMapping("/api/user/article/page")
//    public  Result userGetArticle(@RequestParam Integer num, @RequestParam String classify, @RequestHeader("Authorization") String jwtToken)
//    {
//
//        log.info("获取文章第{}页，类型为{}",num,classify);
//        List<ArticleInfo> article = articleService.getUserPageArticle(num,classify,jwtToken);
//        if(article.isEmpty()){
//            return Result.error("获取失败");
//        }
//        return Result.success(article);
//    }

    @GetMapping ("/api/user/article/like")
    public ApiResponse getArticleLike(@RequestParam Integer id){

        User user = UserContext.getUser();
        log.info("获取点赞状态，文章ID：{}，用户ID：{}", id, user.getId());

        Integer i = articleService.getArticleLike(id, user.getId());
         if(i == -1){
            return ApiResponse.error("获取点赞数据失败");
        }
        return ApiResponse.success(i);
    }


    @GetMapping ("/api/user/article/like/{id}")
    public ApiResponse articleLike(@PathVariable Integer id){
        User user = UserContext.getUser();
        log.info("点赞文章ID：{}，用户ID：{}", id, user.getId());
        Integer i = articleService.articleLike(id, user.getId());
        if(i == 0){
            return ApiResponse.error("数据错误,点赞失败");
        }else if(i == -1){
            return ApiResponse.error("已经对该文章点过赞了,请刷新重试");
        }
        return ApiResponse.success();
    }

    @GetMapping ("/api/user/article/unlike/{id}")
    public ApiResponse articleUnlike(@PathVariable Integer id){

        User user = UserContext.getUser();
        log.info("取消点赞文章ID：{}，用户ID：{}", id, user.getId());

        Integer a = articleService.articleUnlike(id, user.getId());
        if(a == 0){
            return ApiResponse.error("用户信息有误");
        }else if(a == -1){
            return ApiResponse.error("取消点赞失败，还未对该文章点赞");
        }
        return ApiResponse.success();
    }





    /**
     * 获取评论
     */
    @GetMapping ("/api/article/comment/{id}")
    public ApiResponse getArticleComment(@PathVariable Integer id){
        List<ArticleCommentInfo> articleCommentInfos = articleService.getArticleComment(id);

        if(articleCommentInfos == null){
            return ApiResponse.error("获取评论失败");
        }

        return ApiResponse.success(articleCommentInfos);

    }

    /**
     * 审核评论通过
     */
    @GetMapping("/api/admin/article/comment/{id}")
    public ApiResponse addArticleComment(@PathVariable Integer id){

        articleService.addArticleComment(id);

        return ApiResponse.success();
    }

    /**
     * 获取一页评论
     */
    @GetMapping ("/api/admin/article/comment/public/{page}")
    public ApiResponse getAllArticleComment(@PathVariable Integer page){
        List<ArticleComment> articleComment = articleService.getAllArticleComment(page);

        if(articleComment == null){
            return ApiResponse.error("获取评论失败");
        }

        return ApiResponse.success(articleComment);

    }

    /**
     * 获取一页评论
     */
    @GetMapping ("/api/admin/article/comment/public/count")
    public ApiResponse getAllArticleCommentCount(){
        Integer num = articleService.getAllArticleCommentCount();

        if(num == 0){
            return ApiResponse.error("获取评论失败");
        }

        return ApiResponse.success(num);

    }

    /**
     * 获取未通过审核一页评论
     */
    @GetMapping ("/api/admin/article/comment/un_publish/{page}")
    public ApiResponse getUnPublishArticleComment(@PathVariable Integer page){
        List<ArticleComment> articleComment = articleService.getUnPublishArticleComment(page);

        if(articleComment == null){
            return ApiResponse.error("获取评论失败");
        }

        return ApiResponse.success(articleComment);

    }

    /**
     * 获取未通过审核一页评论
     */
    @GetMapping ("/api/admin/article/comment/un_publish/count")
    public ApiResponse getUnPublishArticleCommentCount(){
        Integer num = articleService.getUnPublishArticleCommentCount();

        if(num == 0){
            return ApiResponse.success();
        }else if (num < 0){
            return ApiResponse.error("获取评论失败");
        }

        return ApiResponse.success(num);

    }

}
