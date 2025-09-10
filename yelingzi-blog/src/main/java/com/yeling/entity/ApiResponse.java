package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private Integer code;//响应码，1 代表成功; 0 代表失败
    private String msg;  //响应信息 描述字符串
    private Object data; //返回的数据

    //增删改 成功响应
    public static ApiResponse success(){
        return new ApiResponse(2000,"success",null);
    }
    //查询 成功响应
    public static ApiResponse success(Object data){
        return new ApiResponse(2000,"success",data);
    }
    //失败响应
    public static ApiResponse error(String msg){
        return new ApiResponse(0,msg,null);
    }

    public static ApiResponse error(Integer code, String msg){
        return new ApiResponse(code, msg,null);
    }
}
