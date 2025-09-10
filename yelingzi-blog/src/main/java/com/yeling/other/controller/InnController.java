package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.other.domian.entity.Inn;
import com.yeling.other.domian.entity.InnLedger;
import com.yeling.other.service.InnService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class InnController {

    @Autowired
    private InnService innService;


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/inn/room")
    public ApiResponse getInn(){

        List<Inn> inns  = innService.getInn();

        return ApiResponse.success(inns);
    }

    @CrossOrigin
    @PostMapping("/api/user/order/inn")
    public ApiResponse submitInn(@RequestBody InnLedger innLedger, @RequestHeader("Authorization") String jwtToken){

        log.info("入住房间：{}",innLedger.getRoomName());

        User user = UserContext.getUser();


        Map<String, Object> inns = innService.submitInn(innLedger, user.getId());
        if(inns.isEmpty()){
            return ApiResponse.error("无此房间或房间已满！");
        }
        return ApiResponse.success(inns);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/inn/room")
    public ApiResponse getInnRoom(){

        List<InnLedger> innLedgers  = innService.getInnRoom();

        return ApiResponse.success(innLedgers);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/inn/room/add")
    public ApiResponse addMenu(@RequestBody InnLedger innLedger){

        log.info("添加房间:{}，房间号:{}",innLedger.getRoomName(),innLedger.getRoom());
        if(innService.addInnRoom(innLedger)){
            return ApiResponse.success();
        }
        return ApiResponse.error("房间已存在");
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping ("/api/admin/inn/room/del/{id}")
    public ApiResponse addMenu(@PathVariable Integer id){

        log.info("删除房间:{}",id);
        innService.delInnRoom(id);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/inn/room/correct")
    public ApiResponse innCountCorrect(){

        innService.innCountCorrect();

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/inn/add")
    public ApiResponse addMenu(@RequestBody Inn inn){

        log.info("添加房间类型:{}",inn.getRoomName());
        if(innService.addInnRoomClassify(inn)){
            return ApiResponse.success();
        }
        return ApiResponse.error("房间已存在");
    }


    @DeleteMapping ("/api/admin/inn/del/{id}")
    public ApiResponse delInn(@PathVariable Integer id){

        log.info("删除房间类型:{}",id);
        innService.delInn(id);

        return ApiResponse.success();
    }

}
