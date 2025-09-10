package com.yeling.album.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReq {

    private Integer id;

    private String photoName;

    @NotBlank(message = "照片路径不能为空")
    private String photoUrl;

    private Integer albumId;


}
