package com.poke.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @ClassName JSONDecoder
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 8:15 下午
 */
public class JSONDecoder extends LengthFieldBasedFrameDecoder {
    public JSONDecoder(){
        super(65535, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buffer = (ByteBuf)super.decode(ctx, in);
        if (buffer == null) {
            return null;
        }
        int length = buffer.readableBytes();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        Object parse = JSON.parse(bytes);
        return parse;
    }
}
