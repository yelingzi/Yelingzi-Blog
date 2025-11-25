package com.yeling.yelingziblog.export.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportDownloadResp {

    private String url;
    private String tableName;

}
