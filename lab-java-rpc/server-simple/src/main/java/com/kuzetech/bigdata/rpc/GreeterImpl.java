package com.kuzetech.bigdata.rpc;

import com.kuzetech.bigdata.rpc.base.GreeterGrpc;
import com.kuzetech.bigdata.rpc.base.HelloReply;
import com.kuzetech.bigdata.rpc.base.HelloRequest;
import io.grpc.stub.StreamObserver;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
