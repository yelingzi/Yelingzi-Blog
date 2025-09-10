package com.yeling.article.service;

import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.domian.entity.Tag;
import com.yeling.user.domian.entity.User;
import com.yeling.article.vo.request.TagReq;
import com.yeling.article.vo.response.TagResp;


import java.util.List;

public interface ArticleTagService {

    TagResp addTag(String tag, User user);

    void deleteTag(Integer id);

    PageResult<Tag> getArticleTagListByPage(int page, int pageSize);

    PageResult<Tag> userGetArticleTagListByPage(int page, int pageSize, Integer userId);

    List<Tag> getArticleTagList();

    void updateTag(TagReq tagReq, User user);

    List<TagResp> getArticleTagRespList();


}
