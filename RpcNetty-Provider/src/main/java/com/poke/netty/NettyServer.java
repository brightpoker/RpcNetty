package com.poke.netty;

import com.poke.annotation.RpcService;
import com.poke.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName NettyServer
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 6:42 下午
 */
@Component
@Log4j
public class NettyServer implements InitializingBean, ApplicationContextAware {

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(8);

    private Map<String, Object> serviceMap = new HashMap<>();

    @Autowired
    ServiceRegistry serviceRegistry;

    @Value("${rpc.server.address}")
    private String serverAddress;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);

        for (Object serviceBean: beans.values()) {
            Class<?> clazz = serviceBean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> face: interfaces){
                String interfaceName = face.getName();
                log.info("加载服务类:" + interfaceName);
                serviceMap.put(interfaceName, serviceBean);
            }
        }
        log.info("已加载全部接口");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private void start() {
        final NettyServerHandler handler = new NettyServerHandler(serviceMap);
        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(handler);
                            }
                        });
                String[] str = serverAddress.split(":");
                String host = str[0];
                int port = Integer.parseInt(str[1]);
                ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
                log.info("RPC 服务器启动.监听端口:" + port);
                serviceRegistry.register(serverAddress);
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();

    }
}
