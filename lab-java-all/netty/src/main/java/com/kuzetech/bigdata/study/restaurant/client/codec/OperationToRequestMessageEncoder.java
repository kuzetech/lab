package com.kuzetech.bigdata.study.restaurant.client.codec;

import io.netty.channel.ChannelHandlerContext;
import com.kuzetech.bigdata.study.restaurant.common.Operation;
import com.kuzetech.bigdata.study.restaurant.common.RequestMessage;
import com.kuzetech.bigdata.study.restaurant.util.IdUtil;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);

        out.add(requestMessage);
    }
}
