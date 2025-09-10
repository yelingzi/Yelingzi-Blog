package com.yeling.talk.vo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkCommentReq {

    @Min(value = 0, message = "参数错误")
    private Integer parentId;

    @Min(value = 0, message = "参数错误")
    private Integer toId;

    private String toNickname;

    @NotBlank(message = "评论不能为空")
    private String content;

    @NotNull(message = "说说Id不能为空")
    @Min(value = 1, message = "说说Id必须大于 0")
    private Integer talkId;

}
