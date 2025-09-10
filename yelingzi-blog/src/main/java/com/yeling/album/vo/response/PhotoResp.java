package com.yeling.album.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoResp {

    private Integer id;

    private String photoName;

    private String photoUrl;

    private Integer userId;

    private String nickname;

    private Integer albumId;

    private LocalDateTime createTime;


}
