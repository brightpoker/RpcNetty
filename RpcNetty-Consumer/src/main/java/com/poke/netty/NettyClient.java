package com.poke.netty;

import com.alibaba.fastjson.JSONArray;
import com.poke.connection.ConnectManage;
import com.poke.entity.Request;
import com.poke.entity.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * @ClassName NettyClient
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 12:43 上午
 */
@Component
public class NettyClient {

    @Autowired
    ConnectManage connectManage;

    @Autowired
    NettyClientHandler handler;

    private final static EventLoopGroup group = new NioEventLoopGroup(1);
    private Bootstrap bootstrap = new Bootstrap();

    public NettyClient() {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler",handler);
                    }
                });
    }

    public Object send(Request request) throws InterruptedException{
        Channel channel = connectManage.chooseChannel();

        if (channel!=null && channel.isActive()) {
            SynchronousQueue<Object> queue = handler.sendRequest(request,channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        }else{
            Response res = new Response();
            res.setCode(1);
            res.setError_msg("未正确连接到服务器.请检查相关配置信息!");
            return JSONArray.toJSONString(res);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException{
        ChannelFuture future = bootstrap.connect(address);
        Channel channel = future.sync().channel();
        return channel;
    }

}
