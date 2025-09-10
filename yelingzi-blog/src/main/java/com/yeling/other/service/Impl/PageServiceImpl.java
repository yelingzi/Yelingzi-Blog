package com.yeling.other.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.other.domian.entity.Page;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.PageReq;
import com.yeling.common.vo.request.ViewPageReq;
import com.yeling.other.mapper.PageMapper;
import com.yeling.other.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void addPage(PageReq pageReq, User user) throws JsonProcessingException {
        Page page = new Page();
        page.setUserId(user.getId());
        page.setPageName(pageReq.getPageName());
        page.setRoutePath(pageReq.getRoutePath());
        page.setStatus(pageReq.getStatus());
        // 关键修改：将对象序列化为JSON字符串
        String configJson = objectMapper.writeValueAsString(pageReq.getConfigJson());
        page.setConfigJson(configJson);
        pageMapper.addPage(page);
    }

    @Override
    public void updatePage(PageReq pageReq, Integer userId) throws JsonProcessingException{
        Page page = new Page();
        page.setUserId(userId);
        page.setPageName(pageReq.getPageName());
        page.setRoutePath(pageReq.getRoutePath());
        page.setStatus(pageReq.getStatus());
        // 关键修改：将对象序列化为JSON字符串
        String configJson = objectMapper.writeValueAsString(pageReq.getConfigJson());
        page.setConfigJson(configJson);
        pageMapper.updatePage(page);
    }

    @Override
    public Map<String, Object> getPage(ViewPageReq viewPageReq) throws JsonProcessingException {
        // 获取原始 JSON 字符串
        String jsonStr = pageMapper.findPageByPathAndUserId(
                viewPageReq.getPath(),
                viewPageReq.getUserId()
        );

        // 空值处理
        if (jsonStr == null) {
            return Collections.emptyMap();
        }

        // 使用 Jackson 反序列化
        return new ObjectMapper().readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
    }

    @Override
    public Map<String, Object> getPageById(Integer id, Integer userId) throws JsonProcessingException{
        // 获取原始 JSON 字符串
        String jsonStr = pageMapper.findPageById(id, userId);

        // 空值处理
        if (jsonStr == null) {
            return Collections.emptyMap();
        }

        // 使用 Jackson 反序列化
        return new ObjectMapper().readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
    }

    @Override
    public PageResult<Page> userGetPageList(int page, int pageSize, Integer userId){



        PageResult<Page> pageResult = new PageResult<>();

        if(page < 0 || pageSize <= 0){
            return pageResult;
        }

        pageResult.setData(pageMapper.findPageListByUserId(userId, (page-1)*pageSize, pageSize));
        pageResult.setTotal(pageMapper.findPageCountByUserId(userId));
        return pageResult;

    }

}
