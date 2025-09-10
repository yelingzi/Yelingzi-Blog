package com.yeling.other.service;

import com.yeling.other.domian.entity.Background;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.response.BackgroundResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BackgroundService {

    void uploadBgImage(MultipartFile multipartFile, User user);

    PageResult<Background> getBackgroundListByPage(Integer page, Integer pageSize);

    PageResult<Background> getBackgroundDelListByPage(Integer page, Integer pageSize);

     void delTalkComment(Integer id);

    void showTalkComment(Integer id);

    List<BackgroundResp> getBackgroundList();

}
