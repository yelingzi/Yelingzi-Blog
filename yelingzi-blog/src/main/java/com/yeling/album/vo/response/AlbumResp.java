package com.yeling.album.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResp {

    private Integer id;

    private String albumName;

    private String albumDesc;

    private String albumCover;

    private Integer userId;

    private String nickname;

    private String createTime;

    private List<PhotoResp> photoList;
}
