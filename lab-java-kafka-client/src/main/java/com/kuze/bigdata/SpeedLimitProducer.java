package com.kuze.bigdata;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SpeedLimitProducer {

    private static final int speedPerSec =  100;

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String msgTemp = "{\"eventId\":\"login\",\"eventTime\":\"2022-01-01\",\"uid\":\"ppp\"}";

        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

        Random random = new Random();

        int count = 0;
        int total = 0;

        KafkaProducer producer = new KafkaProducer<Integer, String>(properties);
        while (true) {
            try {
                String msg = msgTemp.replace("ppp", String.valueOf(random.nextInt(1000)));

                // 同步调用，Future.get()会阻塞，等待返回结果......
                // producer.send(new ProducerRecord<>("event", msg)).get();

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
                    TimeUnit.SECONDS.sleep(1);
                }else{
                    count++;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
