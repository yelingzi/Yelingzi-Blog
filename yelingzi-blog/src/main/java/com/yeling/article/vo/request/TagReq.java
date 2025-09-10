package com.yeling.article.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagReq {
    /**
     * 标签id
     */
    private Integer id;

    /**
     * 标签名
     */
    @NotBlank(message = "标签不能为空")
    private String tagName;

}
