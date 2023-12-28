package com.kuzetech.bigdata.kafka.multiWorker;

public class Main {

    /*
    * 这种方式有丢失数据的风险
    * 假设
    * T1线程在t0时间消费分区0的位移=100的消息M1
    * T2线程在t1时间消费分区0的位移=101的消息M2
    * T2线程在t3时优先完成处理，于是上报位移101给Handler
    * 但此时T1线程尚未处理完成。t4时handler提交位移101，之后T1线程发生错误，抛出异常导致位移100的消息消费失败，但由于位移已经提交到101，故消息丢失~。
    * */
    public static void main(String[] args) {
        String brokerList = "localhost:9092";
        String topic = "test-topic";
        String groupID = "test-group";

        final ConsumerThreadHandler<byte[], byte[]> handler = new ConsumerThreadHandler<>(brokerList, groupID, topic);

        final int cpuCount = Runtime.getRuntime().availableProcessors();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.consume(cpuCount);
            }
        };
        new Thread(runnable).start();

        try {
            // 20秒后自动停止该测试程序
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            // swallow this exception
        }
        System.out.println("Starting to close the consumer...");
        handler.close();
    }
}
