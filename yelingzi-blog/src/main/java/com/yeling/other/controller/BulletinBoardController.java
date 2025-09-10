package com.yeling.other.controller;

import com.yeling.other.domian.entity.LeaveMessage;
import com.yeling.other.domian.entity.UpdateLog;
import com.yeling.other.domian.entity.UpdateLogAdd;
import com.yeling.other.domian.entity.UpdateLogAdmin;
import com.yeling.other.service.BulletinBoardService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class BulletinBoardController {

    @Autowired
    private BulletinBoardService bulletinBoardService;

    /**
     * 查询日志
     */

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/updateLog")
    public ApiResponse updateLogList(){
        List<UpdateLog> list = bulletinBoardService.updateLogList();

        return ApiResponse.success(list);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/updateLog")
    public ApiResponse updateLogListData(){
        List<UpdateLogAdmin> list = bulletinBoardService.adminUpdateLogList();

        return ApiResponse.success(list);
    }

    /**
     * 添加日志
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/admin/updateLog/add")
    public ApiResponse addLog(@RequestBody UpdateLogAdd updateLogAdd){

        log.info("添加日志：{}",updateLogAdd.getTitle());
        bulletinBoardService.addLog(updateLogAdd);
        return ApiResponse.success();
    }

    /**
     * 编辑日志
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/admin/updateLog/edit")
    public ApiResponse editLog(@RequestBody UpdateLogAdd updateLogAdd){

        log.info("编辑日志：{}",updateLogAdd.getTitle());
        bulletinBoardService.editLog(updateLogAdd);
        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/updateLog/pinned/{id}")
    public ApiResponse pinnedLog(@PathVariable Integer id){

        log.info("置顶日志：{}",id);
        bulletinBoardService.pinnedLog(id);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/updateLog/unpinned/{id}")
    public ApiResponse unpinnedLog(@PathVariable Integer id){

        log.info("取消置顶日志：{}",id);
        bulletinBoardService.unpinnedLog(id);

        return ApiResponse.success();
    }


    /**
     * 删除文章
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/updateLog/del/{id}")
    public ApiResponse deleteArticle(@PathVariable Integer id){
        log.info("删除日志{}",id);

        bulletinBoardService.delUpdateLog(id);
        return ApiResponse.success();
    }


    /**
     * 留言
     *
     *
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/api/message/leave")
    public ApiResponse leaveMessage(@RequestBody String leaveMessage){

        log.info("留言" + leaveMessage);
        bulletinBoardService.leaveMessage(leaveMessage);
        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/message/leave")
    public ApiResponse getLeaveMessageCount(){

        log.info("查看留言总数");
        List<Integer> integers = bulletinBoardService.getLeaveMessageCount();
        return ApiResponse.success(integers);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/message/leave/{num}")
    public ApiResponse getLeaveMessage(@PathVariable Integer num){

        log.info("查看留言第{}页",num);
        List<LeaveMessage> leaveMessages = bulletinBoardService.getLeaveMessage(num);
        return ApiResponse.success(leaveMessages);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping ("/api/admin/message/unread/{num}")
    public ApiResponse getUnreadLeaveMessage(@PathVariable Integer num){

        log.info("查看未读留言第{}页",num);
        List<LeaveMessage> leaveMessages = bulletinBoardService.getUnreadLeaveMessage(num);
        return ApiResponse.success(leaveMessages);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping ("/api/admin/message/read/{id}")
    public ApiResponse setUnreadLeaveMessage(@PathVariable Integer id){

        log.info("已读留言：{}",id);
        LeaveMessage leaveMessage = bulletinBoardService.setUnreadLeaveMessage(id);
        return ApiResponse.success(leaveMessage);
    }

}
