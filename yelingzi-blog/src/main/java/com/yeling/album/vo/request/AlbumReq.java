package com.yeling.album.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumReq {

    private Integer id;

    @NotBlank(message = "相册名称不能为空")
    private String albumName;

    @NotBlank(message = "相册描述不能为空")
    private String albumDesc;

    @NotBlank(message = "相册封面不能为空")
    private String albumCover;



}
