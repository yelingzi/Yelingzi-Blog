package com.yeling.yelingziblog.other.service;

import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.other.vo.request.LinkMeReq;
import com.yeling.yelingziblog.other.vo.response.LinkMeResp;
import org.springframework.web.multipart.MultipartFile;

public interface LinkMeService {

    String linkImageUpload(MultipartFile multipartFile);

    void addLink(LinkMeReq linkMeReq);

    PageResult<LinkMeResp> getPageListByStatus(Integer page, Integer pageSize, Integer status);

    PageResult<LinkMeResp> getPageList(Integer page, Integer pageSize);

}
