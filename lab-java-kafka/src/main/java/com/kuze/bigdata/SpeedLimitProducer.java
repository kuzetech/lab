package com.kuze.bigdata;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class SpeedLimitProducer {

    public static final Logger logger = LoggerFactory.getLogger(SpeedLimitProducer.class);

    private static final int speedPerSec =  1;
    private static Boolean stopIndex = false;
    private static KafkaProducer producer;


    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String msgTemp = "{\"eventId\":\"login\",\"eventTime\":\"2022-01-01\",\"uid\":\"ppp\"}";

        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        Random random = new Random();

        int count = 0;
        int total = 0;

        producer = new KafkaProducer<Integer, String>(props);

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                logger.info("执行关闭钩子");
                stopIndex = true;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error("钩子线程被打断",e);
                }finally {
                    logger.info("关闭生产者");
                    producer.close();
                }
            }
        });

        while (!stopIndex) {
                String msg = msgTemp.replace("ppp", String.valueOf(random.nextInt(1000)));

                // 同步调用，Future.get()会阻塞，等待返回结果......
                // producer.send(new ProducerRecord<>("event", msg)).get();

                // 采用异步回调
                producer.send(new ProducerRecord<>("event", msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        // 不做任何回应
                    }
                });

                if(count>=speedPerSec){
                    total = total + speedPerSec;
                    String currentTime = ft.format(new Date());
                    System.out.println(currentTime + "---已经发送了" + total + "条数据");
                    count = 0;
                    Thread.sleep(1000);
                }else{
                    count++;
                }
        }

        logger.info("程序关闭");


    }
}


