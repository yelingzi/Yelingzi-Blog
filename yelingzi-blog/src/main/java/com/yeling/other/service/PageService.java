package com.yeling.other.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeling.other.domian.entity.Page;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.PageReq;
import com.yeling.common.vo.request.ViewPageReq;

import java.util.Map;

public interface PageService {

    void addPage(PageReq pageReq, User user) throws JsonProcessingException;

    void updatePage(PageReq pageReq, Integer userId) throws JsonProcessingException;

    Map<String, Object> getPage(ViewPageReq pageReq) throws JsonProcessingException;

    Map<String, Object> getPageById(Integer id, Integer userId) throws JsonProcessingException;

    PageResult<Page> userGetPageList(int page, int pageSize, Integer userId);

}
