package com.yeling.other.service.Impl;

import com.yeling.other.domian.entity.Background;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.response.BackgroundResp;
import com.yeling.other.mapper.BackgroundMapper;
import com.yeling.other.service.BackgroundService;
import com.yeling.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BackgroundServiceImpl implements BackgroundService {

    @Value("${file.upload.savePath}")
    private String savePath;

    @Value("${file.upload.relativePath}")
    private String relativePath;

    @Value("${file.upload.allowedTypes:image/jpg,image/jpeg,image/png}")
    private String allowedTypes;

    @Value("${file.upload.maxSize:2097152}") // 默认最大2MB
    private long maxSize;

    @Autowired
    private BackgroundMapper backgroundMapper;


    @Override
    public void uploadBgImage(MultipartFile multipartFile, User user){
        String path = ImageUtils.uploadImage(multipartFile, "/bg", savePath, relativePath, allowedTypes, maxSize);

        backgroundMapper.insert(path, user.getId(), user.getNickname());

    }

    @Override
    public PageResult<Background> getBackgroundListByPage(Integer page, Integer pageSize){
        PageResult<Background> pageResult = new PageResult<>();
        pageResult.setPageSize(pageSize);
        pageResult.setPage(page);
        pageResult.setData(backgroundMapper.findBgList((page-1)*pageSize, pageSize, 0));
        pageResult.setTotal(backgroundMapper.findBackgroundCount(0));
        return pageResult;
    }

    @Override
    public PageResult<Background> getBackgroundDelListByPage(Integer page, Integer pageSize){
        PageResult<Background> pageResult = new PageResult<>();
        pageResult.setPageSize(pageSize);
        pageResult.setPage(page);
        pageResult.setData(backgroundMapper.findBgList((page-1)*pageSize, pageSize, 1));
        pageResult.setTotal(backgroundMapper.findBackgroundCount(1));
        return pageResult;
    }

    @Override
    public void delTalkComment(Integer id){
        backgroundMapper.updateBackgroundState(id, 1);
    }

    @Override
    public void showTalkComment(Integer id){
        backgroundMapper.updateBackgroundState(id, 0);
    }

    @Override
    public List<BackgroundResp> getBackgroundList(){
        return backgroundMapper.findBackgroundList(0, 5);
    }

}
