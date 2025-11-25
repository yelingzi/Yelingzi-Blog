package com.yeling.yelingziblog.common.utils;

import com.yeling.yelingziblog.common.dto.NettyPushMessage;
import com.yeling.yelingziblog.websocket.NettyWebSocketServerHandler;

public class WsPushUtil {
    /**
     * 推给指定用户，uid=0 时用 nickname 当系统号
     */
    public static boolean push(String uid, NettyPushMessage msg) {
        return NettyWebSocketServerHandler.sendMessage(uid, msg);
    }
}
