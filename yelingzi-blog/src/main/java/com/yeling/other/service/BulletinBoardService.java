package com.yeling.other.service;

import com.yeling.other.domian.entity.UpdateLog;
import com.yeling.other.domian.entity.LeaveMessage;
import com.yeling.other.domian.entity.UpdateLogAdd;
import com.yeling.other.domian.entity.UpdateLogAdmin;

import java.util.List;

public interface BulletinBoardService {
    /**
     * 查询日志
     * @return
     */

    List<UpdateLog> updateLogList();

    List<UpdateLogAdmin> adminUpdateLogList();

    void addLog(UpdateLogAdd updateLogAdd);

    void editLog(UpdateLogAdd updateLogAdd);

    void delUpdateLog(Integer id);

    void pinnedLog(Integer id);

    void unpinnedLog(Integer id);

    void leaveMessage(String leaveMessage);

    List<LeaveMessage> getLeaveMessage(Integer num);

    List<LeaveMessage> getUnreadLeaveMessage(Integer num);

    LeaveMessage setUnreadLeaveMessage(Integer id);

    List<Integer> getLeaveMessageCount();

}
