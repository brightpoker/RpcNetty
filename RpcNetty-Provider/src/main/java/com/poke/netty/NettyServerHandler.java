package com.poke.netty;

import com.alibaba.fastjson.JSON;
import com.poke.entity.Request;
import com.poke.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @ClassName NettyServerHandler
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 8:31 下午
 */
@ChannelHandler.Sharable
@Log4j
@AllArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Map<String, Object> serviceMap;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端连接成功" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = (Request) JSON.parseObject(msg.toString(), Request.class);

        if (request.getMethodName().equals("heartBeat")) {
            log.info("客户端心跳信息..." + ctx.channel().remoteAddress());
        } else {
            log.info("RPC客户端请求接口:"+request.getClassName()+"   方法名:"+ request.getMethodName());
            Response response = new Response();
            response.setRequestId(request.getId());
            try {
                Object result = handler(request);
                response.setData(result);
            } catch (Throwable e) {
                e.printStackTrace();
                response.setCode(1);
                response.setError_msg(e.toString());
                log.error("RPC Server handle request error",e);
            }
            ctx.writeAndFlush(response);
        }
    }

    private Object handler(Request request) throws Throwable{
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);

        if (serviceBean != null) {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Object[] parameters = request.getParameters();
            Class<?>[] parameterTypes = request.getParameterTypes();
            
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, getParameters(parameterTypes,parameters));
        } else {
            throw new Exception("未找到服务接口,请检查配置!:" + className + "#" + request.getMethodName());
        }
    }

    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return parameters;
        }
        return IntStream.range(0, parameterTypes.length)
                .mapToObj(i -> JSON.parseObject(parameters[i].toString(), parameterTypes[i]))
                .collect(Collectors.toList())
                .toArray();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)   {
        log.info("客户端断开连接!" + ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("客户端已超过60秒未读写数据,关闭连接." + ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(cause.getMessage());
        ctx.close();
    }
}
