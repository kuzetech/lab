package com.kuze.bigdata.study.protocol;

import io.netty.buffer.ByteBuf;

public interface CustomBody {
    ByteBuf encode();
    void decode(ByteBuf byteBuf);
}
