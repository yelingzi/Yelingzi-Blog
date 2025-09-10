package com.yeling.talk.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkReq {

    private Integer id;

    private String title;

    @NotBlank(message = "说说内容不能为空")
    private String content;


    private String imageUrl;

    private Integer isTop;

    private Integer state;

}
