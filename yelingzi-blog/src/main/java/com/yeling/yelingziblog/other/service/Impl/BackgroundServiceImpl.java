package com.yeling.yelingziblog.other.service.Impl;

import com.yeling.yelingziblog.other.entity.Background;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.other.vo.response.BackgroundResp;
import com.yeling.yelingziblog.other.mapper.BackgroundMapper;
import com.yeling.yelingziblog.other.service.BackgroundService;
import com.yeling.yelingziblog.common.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BackgroundServiceImpl implements BackgroundService {


    @Value("${file.upload.maxSize:2097152}") // 默认最大2MB
    private long maxSize;

    @Autowired
    private BackgroundMapper backgroundMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public void uploadBgImage(MultipartFile multipartFile, User user){
        String path = imageUtils.uploadImage(multipartFile, "/bg", maxSize);

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
    @Cacheable(value = "background:list")
    public List<BackgroundResp> getBackgroundList(){
        return backgroundMapper.findBackgroundList(0, 5);
    }

}
