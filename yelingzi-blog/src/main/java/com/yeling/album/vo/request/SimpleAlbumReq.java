package com.yeling.album.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAlbumReq {

    private Integer id;

    private String albumName;
}
