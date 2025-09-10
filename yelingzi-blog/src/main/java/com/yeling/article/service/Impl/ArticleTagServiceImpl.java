package com.yeling.article.service.Impl;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.Tag;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.TagReq;
import com.yeling.article.vo.response.TagResp;
import com.yeling.article.mapper.ArticleTagMapper;
import com.yeling.article.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;


    @Override
    public TagResp addTag(String tag, User user){


        Integer count = articleTagMapper.findNotNullByTagName(tag);
        if(count > 0){
            return null;
        }

        articleTagMapper.addTag(tag, user.getId(), user.getNickname());

        return articleTagMapper.findTagByTagName(tag);

    }

    @Override
    public void deleteTag(Integer id){
        articleTagMapper.deleteTagById(id);
    }

    @Override
    public PageResult<Tag> getArticleTagListByPage(int page, int pageSize){
        if(page <= 0){
            return null;
        }
        PageResult<Tag> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(articleTagMapper.findTagCount());
        pageResult.setData(articleTagMapper.findTagByPage((page - 1) * pageSize, pageSize));
        return pageResult;
    }

    @Override
    public PageResult<Tag> userGetArticleTagListByPage(int page, int pageSize, Integer userId){

        if(page <= 0 || pageSize <=0){
            return null;
        }
        PageResult<Tag> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(articleTagMapper.findTagCountByUserId(userId));
        pageResult.setData(articleTagMapper.findTagPageByUserId(userId, (page - 1) * pageSize, pageSize));
        return pageResult;
    }

    @Override
    public List<Tag> getArticleTagList(){

        return articleTagMapper.findTag();
    }

    @Override
    public void updateTag(TagReq tagReq, User user){

        Integer count = articleTagMapper.findNotNullById(tagReq.getId());
        if(count == 0){
            return;
        }

        articleTagMapper.updateTagById(tagReq.getId(), tagReq.getTagName(), user.getId(), user.getNickname());

    }

    @Override
    public List<TagResp> getArticleTagRespList(){
        return articleTagMapper.findTagResp();
    }



}
