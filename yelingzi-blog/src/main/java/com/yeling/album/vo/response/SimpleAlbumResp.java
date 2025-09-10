package com.yeling.album.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAlbumResp {

    private Integer id;

    private String albumName;

    private String albumDesc;

    private String albumCover;

    private String createTime;

}
