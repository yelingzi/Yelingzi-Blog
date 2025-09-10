package com.yeling.other.service.Impl;

import com.yeling.other.domian.entity.LeaveMessage;
import com.yeling.other.domian.entity.UpdateLogAdd;
import com.yeling.other.domian.entity.UpdateLogAdmin;
import com.yeling.other.mapper.BulletinBoardMapper;
import com.yeling.other.domian.entity.UpdateLog;
import com.yeling.other.service.BulletinBoardService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BulletinBoardImpl implements BulletinBoardService {

    @Autowired
    private BulletinBoardMapper bulletinBoardMapper;

    public List<UpdateLog> updateLogList(){ return bulletinBoardMapper.updateList();}

    public List<UpdateLogAdmin> adminUpdateLogList(){ return bulletinBoardMapper.AdminUpdateLogList();}

    /**
     * 添加更新日志
     */
    @Override
    public void addLog(UpdateLogAdd updateLogAdd){
        String result = String.join("#", updateLogAdd.getContent());
        bulletinBoardMapper.addLog(updateLogAdd.getTitle(),result);
    }

    @Override
    public void editLog(UpdateLogAdd updateLogAdd){
        String result = String.join("#", updateLogAdd.getContent());
        bulletinBoardMapper.editLog(updateLogAdd.getId(),updateLogAdd.getTitle(),result);
    }

    @Override
    public void pinnedLog(Integer id){
        bulletinBoardMapper.pinnedLog(1,id);
    }

    @Override
    public void unpinnedLog(Integer id){
        bulletinBoardMapper.pinnedLog(0,id);
    }

    /**
     * 删除日志
     */
    @Override
    public void delUpdateLog(Integer id){
        bulletinBoardMapper.delUpdateLog(1,id);
    }

    /**
     * 提交bug
     */
    @Override
    public void leaveMessage(String s){
        LocalDateTime localDateTime = LocalDateTime.now();
        LeaveMessage leaveMessage = new LeaveMessage();
        leaveMessage.setTime(localDateTime);
        leaveMessage.setContent(stripHtmlTags(s));
        leaveMessage.setIsRead("未读");
        bulletinBoardMapper.addLeaveMessage(leaveMessage);
    }

    public String stripHtmlTags(String html) {
        // 解析HTML字符串
        Document doc = Jsoup.parse(html);

        // 获取文档中的纯文本内容，不包括HTML标签，并返回纯文本内容
        return doc.text();
    }


    @Override
    public List<LeaveMessage> getLeaveMessage(Integer num){
        Integer integer = num * 10;
        return bulletinBoardMapper.getLeaveMessage(integer);
    }

    @Override
    public List<LeaveMessage> getUnreadLeaveMessage(Integer num){
        Integer integer = num * 10;
        String read = "未读";
        return bulletinBoardMapper.getUnreadLeaveMessage(integer,read);
    }

    @Transactional
    @Override
    public LeaveMessage setUnreadLeaveMessage(Integer id){
        LeaveMessage leaveMessage = bulletinBoardMapper.getIdLeaveMessage(id);
        leaveMessage.setIsRead("已读");
        bulletinBoardMapper.setReadLeaveMessage(leaveMessage.getIsRead(),id);

        return leaveMessage;
    }

    @Transactional
    @Override
    public List<Integer> getLeaveMessageCount(){
        List<Integer> integers = new ArrayList<>();
        integers.add(bulletinBoardMapper.getLeaveMessageCount());
        integers.add(bulletinBoardMapper.getUnreadLeaveMessageCount());
        return integers;
    }


}
