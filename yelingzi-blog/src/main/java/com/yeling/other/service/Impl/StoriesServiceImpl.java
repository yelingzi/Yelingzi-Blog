package com.yeling.other.service.Impl;

import com.yeling.user.domian.entity.User;
import com.yeling.other.domian.entity.Stories;
import com.yeling.other.mapper.StoriesMapper;
import com.yeling.user.mapper.UserMapper;
import com.yeling.other.service.StoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoriesServiceImpl implements StoriesService {

    @Autowired
    private StoriesMapper storiesMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Integer addStory(Stories stories, User user){
        //获取时间
        LocalDateTime localDateTime = LocalDateTime.now();
        stories.setPublishTime(localDateTime);

        //设置文章作者id
        stories.setUserId(user.getId());

        //设置文章作者名
        String name = userMapper.getNiChengByUid(user.getId());
        if(name.isEmpty()){
            return 0;
        }

        stories.setUserName(name);
        storiesMapper.addStory(stories);
        return 1;
    }

    @Override
    public void deleteStory(Integer id){
        storiesMapper.deleteStory(id);
    }

    @Override
    public List<Stories> showPageStories(Integer num){
        Integer integer = num * 5;
        return storiesMapper.showPage(integer, 5);
    }

    @Override
    public List<Stories> showStories(Integer num){
        Integer integer = (num - 1) * 10;
        return storiesMapper.showPage(integer, 10);
    }

    @Override
    public Integer getStoriesCount(){
        return storiesMapper.getStoriesCount();
    }

}
