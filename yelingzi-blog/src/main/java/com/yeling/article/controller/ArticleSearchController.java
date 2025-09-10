package com.yeling.article.controller;

import com.yeling.article.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;






}
