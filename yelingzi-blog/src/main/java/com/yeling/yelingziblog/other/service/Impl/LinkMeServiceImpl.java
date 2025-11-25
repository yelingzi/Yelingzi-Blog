package com.yeling.yelingziblog.other.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.common.service.EmailService;
import com.yeling.yelingziblog.common.utils.ImageUtils;
import com.yeling.yelingziblog.other.dto.LinkMeDTO;
import com.yeling.yelingziblog.other.entity.LinkMe;
import com.yeling.yelingziblog.other.mapper.LinkMeMapper;
import com.yeling.yelingziblog.other.service.LinkMeService;
import com.yeling.yelingziblog.other.vo.request.LinkMeReq;
import com.yeling.yelingziblog.other.vo.response.LinkMeResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LinkMeServiceImpl implements LinkMeService {

    @Autowired
    private LinkMeMapper linkMeMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private EmailService emailService;

    private final String SAVE_SUB_DIR = "linkme";

    @Value("${link.me.email}")
    private String email;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String linkImageUpload(MultipartFile file) {
        return imageUtils.uploadImage(file, SAVE_SUB_DIR);
    }

    @Override
    public void addLink(LinkMeReq linkMeReq){

        LinkMeDTO linkMeDTO = new LinkMeDTO(linkMeReq.getContent(), linkMeReq.getEmail(), convertToDatabaseColumn(linkMeReq.getImages()));

        linkMeMapper.addLink(linkMeDTO);
        log.info("添加留言：{}，邮箱：{}，图片数量：{}",
                linkMeReq.getContent(), linkMeReq.getEmail(),
                linkMeReq.getImages() != null ? linkMeReq.getImages().length : 0);

        // 发送通知邮件
        sendEmail(linkMeReq);
    }

    private void sendEmail(LinkMeReq linkMeReq){
        try {
            // 准备模板变量
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("content", linkMeReq.getContent());
            templateVariables.put("email", linkMeReq.getEmail());
            templateVariables.put("imageCount", linkMeReq.getImages() != null ? linkMeReq.getImages().length : 0);
            templateVariables.put("createTime", new Date());

            // 发送模板邮件
            emailService.sendTemplateEmailAsync(
                    email,
                    "【叶玲子的网页】新的留言通知",
                    "LinkMeNotification.html",
                    templateVariables,
                    "LINK_ME_NOTIFICATION"
            );

            log.info("留言通知邮件已发送到队列，用户邮箱: {}", linkMeReq.getEmail());

        } catch (Exception e) {
            log.error("发送留言通知邮件失败: {}", e.getMessage());
            // 这里可以选择不抛出异常，因为邮件发送失败不应该影响主要的留言功能
        }
    }

    @Override
    public PageResult<LinkMeResp> getPageListByStatus(Integer page, Integer pageSize, Integer status){
        List<LinkMe> list = linkMeMapper.findLinkPageByStatus(status, page, pageSize);

        List<LinkMeResp> links = list.stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());

        return new PageResult<>(linkMeMapper.findLinkCountByStatus(status), page, pageSize, links);
    }


    @Override
    public PageResult<LinkMeResp> getPageList(Integer page, Integer pageSize){
        List<LinkMe> list = linkMeMapper.findLinkListByPage(page, pageSize);

        List<LinkMeResp> links = list.stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());

        return new PageResult<>(linkMeMapper.findLinkCount(), page, pageSize, links);
    }


    private String convertToDatabaseColumn(String[] attribute) {
        if (attribute == null || attribute.length == 0) return null;
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("数组转JSON失败", e);
        }
    }

    private String[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new String[0];
        try {
            return mapper.readValue(dbData, String[].class);
        } catch (Exception e) {
            throw new IllegalStateException("JSON转数组失败", e);
        }
    }

    private LinkMeResp convertToResp(LinkMe entity) {
        LinkMeResp resp = new LinkMeResp();
        // 复制其他属性
        resp.setId(entity.getId());
        resp.setContent(entity.getContent());
        resp.setEmail(entity.getEmail());
        resp.setCreateTime(entity.getCreateTime());
        resp.setUpdateTime(entity.getUpdateTime());
        resp.setStatus(entity.getStatus());

        // 转换 images 字段
        resp.setImages(convertToEntityAttribute(entity.getImages()));

        return resp;
    }

}
