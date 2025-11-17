package com.kuzetech.bigdata.flink.kafka;

import com.kuzetech.bigdata.flink.kafka.model.KafkaSinkRecord;
import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KeySelectorApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment();

        List<KafkaSinkRecord> dataList = new ArrayList<>();
        dataList.add(new KafkaSinkRecord(new byte[0], "1"));
        dataList.add(new KafkaSinkRecord(new byte[0], "2"));
        dataList.add(new KafkaSinkRecord(new byte[0], "3"));
        dataList.add(new KafkaSinkRecord(new byte[0], "4"));
        dataList.add(new KafkaSinkRecord(new byte[0], "5"));

        DataStreamSource<KafkaSinkRecord> source = env.fromCollection(dataList, TypeInformation.of(KafkaSinkRecord.class));

        KafkaSink<KafkaSinkRecord> kafkaSink = KafkaSink.<KafkaSinkRecord>builder()
                .setBootstrapServers("localhost:9092")
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .setRecordSerializer((KafkaRecordSerializationSchema<KafkaSinkRecord>) (data, context, timestamp) -> {
                    if (data.getKey() == null) {
                        return new ProducerRecord<>("test", data.getValue().getBytes(StandardCharsets.UTF_8));
                    } else {
                        return new ProducerRecord<>("test", data.getKey(), data.getValue().getBytes(StandardCharsets.UTF_8));
                    }
                })
                .build();

        source.sinkTo(kafkaSink);

        env.execute(UUID.randomUUID().toString());

    }
}
