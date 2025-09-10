package com.yeling.user.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    /**
     *  邮件接受方
     */
    private String tos;
    /**
     *      邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;


}
