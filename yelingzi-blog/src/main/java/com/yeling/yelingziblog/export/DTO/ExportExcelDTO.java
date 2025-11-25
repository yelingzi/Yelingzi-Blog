package com.yeling.yelingziblog.export.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportExcelDTO implements Serializable {
    private String tableName;
    private Integer userId;
    private Class<?> dataClass;

}
