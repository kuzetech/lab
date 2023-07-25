package com.kuzetech.bigdata.study;

public class Reactor {

    /*
        kafka 是如何处理 client 请求的
        关于如何处理请求，我们很容易想到的方案有两个

        1.顺序处理请求。如果写成伪代码，大概是这个样子
            while (true) {
                Request request = accept(connection);
                handle(request);
            }
        这个方法实现简单，但是有个致命的缺陷，那就是吞吐量太差。
        由于只能顺序处理每个请求，因此，每个请求都必须等待前一个请求处理完毕才能得到处理。这种方式只适用于请求发送非常不频繁的系统

        2. 每个请求使用单独线程处理。也就是说，我们为每个入站请求都创建一个新的线程来异步处理。我们一起来看看这个方案的伪代码
            while (true) {
                Request = request = accept(connection);
                Thread thread = new Thread(() -> {handle(request);});
                thread.start();
            }
        这个方法反其道而行之，完全采用异步的方式。系统会为每个入站请求都创建单独的线程来处理。
        这个方法的好处是，它是完全异步的，每个请求的处理都不会阻塞下一个请求。
        但缺陷也同样明显。为每个请求都创建线程的做法开销极大，在某些场景下甚至会压垮整个服务。还是那句话，这个方法只适用于请求发送频率很低的业务场景

        3. 既然这两种方案都不好，那么，Kafka 是如何处理请求的呢？用一句话概括就是，Kafka 使用的是 Reactor 模式
           Reactor 模式是事件驱动架构的一种实现方式，特别适合应用于处理多个客户端并发向服务器端发送请求的场景
    */



}
