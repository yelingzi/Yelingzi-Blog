package com.yeling.yelingziblog.common.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
@Slf4j
@Service
public class IpUtils {

    private static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0]|[01]?[0-9][0-9]?)$";

    public static String getIpAddr(HttpServletRequest request) {
        //获取请求头"x-forwarded-for"对应的value
        String ip = request.getHeader("x-forwarded-for");
        //如果获取的ip值为空
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //则获取请求头"Proxy-Client-IP"对应的value
            ip = request.getHeader("Proxy-Client-IP");
        }
        //如果获取的ip值仍为空
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //则获取请求头"WL-Proxy-Client-IP"对应的value
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        //如果以上方式获取的ip值都为空
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //则直接获取ip地址
            ip = request.getRemoteAddr();
        }

        //返回ip地址
        return ip;
    }

    /**
     * 根据IP地址获取地理位置信息
     * @param ip IP地址
     * @return 格式化的地理位置字符串（省份-城市）
     */
    public static String getAddrName(String ip) {
        // 1. 参数校验
        if (ip == null || ip.trim().isEmpty()) {
            log.warn("IP地址不能为空");
            return "未知";
        }

        // 2. 验证IP地址格式
        if (!isValidIpAddress(ip)) {
            log.warn("无效的IP地址格式: {}", ip);
            return "未知";
        }

        // 3. 构建请求URL（建议将API地址和密钥配置化）
        String apiUrl = "https://api.example.com/location?ip=" + ip;

        try {
            // 4. 发送HTTP请求并获取JSON响应
            JSONObject json = readJsonFromUrl(apiUrl);

            // 5. 检查API返回状态(FastJSON2方式)
            Integer status = json.getInteger("status");
            if (status != null && status != 0) {
                log.warn("地理位置API请求失败: {}", json.getString("message"));
                return "未知";
            }

            // 6. 安全地解析JSON结构(FastJSON2方式)
            JSONObject content = json.getJSONObject("content");
            if (content == null) {
                log.warn("无效的API响应格式: 缺少content字段");
                return "未知";
            }

            JSONObject addrDetail = content.getJSONObject("address_detail");
            if (addrDetail == null) {
                log.warn("无效的API响应格式: 缺少address_detail字段");
                return "未知";
            }

            // 7. 获取省份和城市信息(FastJSON2方式)
            String province = addrDetail.getString("province");
            String city = addrDetail.getString("city");

            // 8. 处理可能的null值
            province = province != null ? province : "未知省份";
            city = city != null ? city : "未知城市";

            // 9. 格式化返回结果
            return String.format("%s-%s", province, city);

        } catch (JSONException e) {
            log.warn("解析JSON响应失败", e);
            return "未知";
        } catch (IOException e) {
            log.warn("发送HTTP请求失败", e);
            return "未知";
        }
    }

    // IP地址验证方法
    private static boolean isValidIpAddress(String ip) {
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }

    // 从URL读取JSON的方法（示例实现）
    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(Integer.parseInt(sb.toString()));
        }
    }

}
