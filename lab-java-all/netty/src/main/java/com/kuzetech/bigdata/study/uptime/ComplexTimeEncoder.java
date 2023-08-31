package com.kuzetech.bigdata.study.uptime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ComplexTimeEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        UnixTime m = (UnixTime) msg;
        ByteBuf encoded = ctx.alloc().buffer(4);
        encoded.writeInt((int)m.value());
        // 当编码的数据实际上被写入网络时，Netty 会将其标记为成功或失败
        ctx.write(encoded, promise);

        //  we did not call ctx.flush().
        //  There is a separate handler method void flush(ChannelHandlerContext ctx) which is purposed to override the flush() operation
    }
}
