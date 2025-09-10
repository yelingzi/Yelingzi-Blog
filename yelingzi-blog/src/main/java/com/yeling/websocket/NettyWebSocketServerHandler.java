package com.yeling.websocket;


import cn.hutool.json.JSONUtil;
import com.yeling.chat.domain.dto.PushMessageDto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ConcurrentHashMap<ChannelHandlerContext, String> clients = new ConcurrentHashMap<>();

    // 新增：userId -> channel（方便业务层按 userId 推送）
    private static final ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功");
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
        log.info("断开连接");
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        // 1. 移除 channel -> userId
        String userIdStr = clients.remove(ctx);

        // 2. 移除 userId -> channel
        if (userIdStr != null) {
            userChannelMap.remove(Integer.valueOf(userIdStr));
        }

        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {


            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            Integer userId = NettyUtil.getAttr(ctx.channel(), NettyUtil.UserID);

            // 1. 原来的 channel -> userId
            clients.put(ctx, String.valueOf(userId));

            // 2. 新增：userId -> channel
            if(userId == 0){
                userChannelMap.put("游客" + token, ctx.channel());
            }else{
                userChannelMap.put(userId.toString(), ctx.channel());
            }

            log.info("连接成功, token = {}, userId = {}", token, userId);

        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause);
        ctx.channel().close();
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String message = msg.text();
        log.info("Received message: " + message);

        for (ChannelHandlerContext client : clients.keySet()) {
            if (client != ctx) {
                client.writeAndFlush(new TextWebSocketFrame(message));
            }
        }

    }

    /**
     * 根据 uid 推送，uid=0 时用 nickname 当系统号
     */
    public static boolean sendToUser(String uid, PushMessageDto dto) {
        Channel ch = userChannelMap.get(uid);
        if (ch != null && ch.isActive()) {
            ch.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));
            return true;
        }
        return false;
    }

    /**
     * 向所有在线客户端广播消息
     * @param pushMessageResp 推送消息统一返回体
     */
    public static void broadcastAll(PushMessageDto pushMessageResp) {
        String json = JSONUtil.toJsonStr(pushMessageResp);
        TextWebSocketFrame frame = new TextWebSocketFrame(json);

        // 遍历所有在线 channel
        for (Channel ch : userChannelMap.values()) {
            if (ch != null && ch.isActive()) {
                ch.writeAndFlush(frame.retain());   // retain 防止多次释放
            }
        }
        // 用完手动释放一次引用计数
        ReferenceCountUtil.release(frame);
    }


}
