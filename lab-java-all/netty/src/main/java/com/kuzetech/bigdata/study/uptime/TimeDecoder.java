package com.kuzetech.bigdata.study.uptime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TimeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        UnixTime time = new UnixTime(in.readUnsignedInt());
        System.out.println("message decode " + time);
        // out.add(in.readBytes(4));
        out.add(time);
    }


}
