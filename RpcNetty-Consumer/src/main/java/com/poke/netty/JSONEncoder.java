package com.poke.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @ClassName JSONEncoder
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 12:55 上午
 */
public class JSONEncoder extends MessageToMessageEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(o);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        list.add(buffer);
    }
}
