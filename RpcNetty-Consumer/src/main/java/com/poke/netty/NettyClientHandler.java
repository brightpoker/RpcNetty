package com.poke.netty;

import com.alibaba.fastjson.JSON;
import com.poke.connection.ConnectManage;
import com.poke.entity.Request;
import com.poke.entity.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @ClassName NettyClientHandler
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 12:57 上午
 */
@Log4j
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ConnectManage manage;

    private ConcurrentHashMap<String,SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接到服务器:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address =(InetSocketAddress) ctx.channel().remoteAddress();
        log.info("与RPC服务器断开连接."+address);
        ctx.channel().close();
        manage.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSON.parseObject(msg.toString(), Response.class);
        String requestId = response.getRequestId();
        SynchronousQueue<Object> queue = queueMap.get(requestId);
        queue.put(response);
        queueMap.remove(requestId);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception {
        log.info("已超过30秒未与RPC服务器进行读写操作!将发送心跳消息...");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.ALL_IDLE){
                Request request = new Request();
                request.setMethodName("heartBeat");
                ctx.channel().writeAndFlush(request);
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    public SynchronousQueue<Object> sendRequest(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.info("RPC通信服务器发生异常" + cause);
        ctx.channel().close();
    }
}
