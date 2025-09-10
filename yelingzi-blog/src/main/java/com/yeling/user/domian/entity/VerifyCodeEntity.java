package com.yeling.user.domian.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeEntity {

    /**
     * 验证码Key
     */
    private String key;

    /**
     * 验证码图片，base64压缩后的字符串
     */
    private String image;

    /**
     * 验证码文本值
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String text;

}
