package com.yeling.common.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewPageReq {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "路径参数不能为空")
    private String path;

}
