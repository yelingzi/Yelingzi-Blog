package com.yeling.other.controller;

import com.yeling.entity.ApiResponse;
import com.yeling.other.domian.entity.Verse;
import com.yeling.other.service.ForecourtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ForecourtController {

    @Autowired
    private ForecourtService forecourtService;

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/ip")
    public ApiResponse getIp(HttpServletRequest request, HttpServletResponse response)
    {
        log.info("获取IP");

        String ip = forecourtService.getIpAddr(request, response);
        log.info("ip:{}",ip);
        return ApiResponse.success(ip);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/admin/verse/add")
    public ApiResponse addVerse(@RequestBody Verse verse){
        Verse v = forecourtService.fundVerse(verse.getVerse());

        if( v == null){
            forecourtService.addVerse(verse);
            return ApiResponse.success();
        }else{
            return ApiResponse.error("诗句已存在！");
        }

    }


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/verse/show")
    public ApiResponse showVerse(){

        List<Verse> verses = forecourtService.showVerse();

        return ApiResponse.success(verses);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/verse/show/{ip}")
    public ApiResponse getIPVerse(@PathVariable String ip){

        List<Verse> verses = forecourtService.getIpVerse(ip);

        return ApiResponse.success(verses);

    }

}
