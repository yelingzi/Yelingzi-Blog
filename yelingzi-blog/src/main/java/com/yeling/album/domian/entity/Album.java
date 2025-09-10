package com.yeling.album.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    private Integer id;

    private String albumName;

    private String albumDesc;

    private String albumCover;

    private Integer userId;

    private String nickname;

    private String createTime;

    private Integer isDel;

}
