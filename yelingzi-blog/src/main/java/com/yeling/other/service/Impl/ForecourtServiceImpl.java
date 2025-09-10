package com.yeling.other.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.other.domian.entity.Verse;
import com.yeling.other.mapper.ForecourtMapper;
import com.yeling.other.service.ForecourtService;
import com.yeling.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ForecourtServiceImpl implements ForecourtService {

    @Autowired
    private ForecourtMapper forecourtMapper;

    @Override
    public Verse fundVerse(String string){
        return forecourtMapper.fundVerse(string);
    }

    @Override
    public void addVerse(Verse verse)
    {
        forecourtMapper.addVerse(verse);
    }

    @Override
    public List<Verse> showVerse(){
        return forecourtMapper.showVerse();
    }

    @Override
    public List<Verse> getIpVerse(String ip){
        if(Objects.equals(ip, "Unknown") || ip == null){
            return forecourtMapper.getIpVerse("其它");
        }
        return forecourtMapper.getIpVerse(ip);
    }




    public String getIpAddr(HttpServletRequest request, HttpServletResponse response) {


        String ip = IpUtils.getIP_(request, response);

        // 设置API的URL
        String url = "https://ip.taobao.com/outGetIpInfo?ip=" + ip +"&accessKey=alibaba-inc";

        RestTemplate template = new RestTemplate();

        // 发起HTTP GET请求，获取指定URL的响应实体
        String responses = template.getForObject(url, String.class);

        // 使用Jackson解析JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = null;
        try {
            json = mapper.readTree(responses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 从JSON中提取省份信息
        JsonNode regionNameNode = json.get("region");
        String province = (regionNameNode != null) ? regionNameNode.asText() : "Unknown";

        return province;
    }

}
