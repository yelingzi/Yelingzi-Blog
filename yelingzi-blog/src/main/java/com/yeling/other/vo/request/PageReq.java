package com.yeling.other.vo.request;


import com.alibaba.fastjson.JSON;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageReq {

    private Integer id;

    private Integer userId;

    @NotBlank(message = "页面名称不能为空")
    private String pageName;

    @NotBlank(message = "路由不能为空")
    private String routePath;

    @NotNull(message = "Json内容不能为空")
    private Object configJson;

    @NotNull(message = "状态不能为空")
    @Range(min = 1, max = 3, message = "状态值必须是1-3之间的整数")
    private Integer status;
}
