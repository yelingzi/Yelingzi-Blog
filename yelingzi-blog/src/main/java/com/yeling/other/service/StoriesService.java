package com.yeling.other.service;

import com.yeling.user.domian.entity.User;
import com.yeling.other.domian.entity.Stories;

import java.util.List;

public interface StoriesService {
    Integer addStory(Stories stories, User user);

    void deleteStory(Integer num);

    List<Stories> showPageStories(Integer id);

    List<Stories> showStories(Integer id);

    Integer getStoriesCount();



}
