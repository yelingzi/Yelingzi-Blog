package com.yeling.other.vo.response;

import com.alibaba.fastjson.JSON;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResp {

    private Integer id;
    private Integer userId;
    private String pageName;
    private String routePath;
    private JSON configJson;
}
