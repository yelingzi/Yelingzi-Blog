package com.yeling.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class IpUtils {

    private static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0]|[01]?[0-9][0-9]?)$";
    private final Searcher searcher;

    @Autowired
    public IpUtils(Searcher searcher) {
        this.searcher =  searcher;
    }

    public static String getIP_(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/html;charset=utf-8");

        /*设置响应头允许ajax跨域访问*/
        response.setHeader("Access-Control-Allow-Origin", "*");
        /* 星号表示所有的异域请求都可以接受， */

        response.setHeader("Access-Control-Allow-Methods", "GET,POST");

        return getIpAddr(request);
    }

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



    /**
     * 根据IP地址查询地理位置信息
     * @param ip IP地址
     * @return 格式为"省份-城市"的位置信息，如"广东省-深圳市"
     */
    public String searchIp(String ip) {
        try {
            // 1. 检查本地地址
            if (isLocalAddress(ip)) {
                return "本地";
            }

            // 2. 检查IPv4格式
            if (!isIPv4Address(ip)) {
                log.warn("非IPv4地址跳过查询: {}", ip);
                return "内网";
            }

            // 3. 查询IP地理位置 (示例格式："中国|0|广东省|深圳市|电信")
            String location = searcher.search(ip);

            // 4. 解析并格式化返回结果为"省份-城市"
            return parseLocation(location);
        } catch (Exception e) {
            log.warn("IP查询异常 | ip={}", ip, e);
            return "未知";
        }
    }

    /**
     * 解析IP查询结果格式："中国|0|广东省|深圳市|电信"
     */
    private String parseLocation(String rawLocation) {
        if (rawLocation == null || rawLocation.trim().isEmpty()) {
            return "未知";
        }

        String[] parts = rawLocation.split("\\|");
        if (parts.length >= 4) {
            // 格式：中国|0|省份|城市|运营商
            String province = parts[2].replace("省", "").replace("市", "");
            String city = parts[3].replace("市", "");

            // 处理直辖市情况（如北京市）
            if (province.equals(city) || parts[2].endsWith("市")) {
                return province + "-" + province;
            }
            return province + "-" + city;
        }

        return "未知";
    }

    private boolean isIPv4Address(String ip) {
        return ip.matches(IPV4_PATTERN);
    }

    private boolean isLocalAddress(String ip) {
        return ip.equals("127.0.0.1") ||
                ip.equals("0:0:0:0:0:0:0:1") ||
                ip.equals("::1");
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
