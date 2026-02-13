/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.IsolationLevel;

import java.util.Locale;
import java.util.Properties;

import static com.kuzetech.bigdata.flink.kafka.KafkaUtil.DEFAULT_PRODUCER_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class TransactionJob {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = FlinkUtil.createCheckpointEnv();

        KafkaSourceBuilder<String> sourceBuilder = KafkaSource.<String>builder()
                .setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT))
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setBootstrapServers("localhost:9092")
                .setTopics("source")
                .setGroupId("transaction-job-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setClientIdPrefix("transaction-job-client");

        SingleOutputStreamOperator<String> sourceStream = env.fromSource(sourceBuilder.build(), WatermarkStrategy.noWatermarks(), "source")
                .uid("source")
                .name("source");

        SingleOutputStreamOperator<String> processStream = sourceStream.map(value -> {
                    System.out.println("Processing: " + value);
                    return value;
                })
                .uid("process")
                .name("process");

        Properties props = new Properties();
        props.putAll(DEFAULT_PRODUCER_CONFIG);
        KafkaSinkBuilder<String> sinkBuilder = KafkaSink.<String>builder()
                .setKafkaProducerConfig(props)
                .setProperty(TRANSACTION_TIMEOUT_CONFIG, "900000")
                .setProperty(ENABLE_IDEMPOTENCE_CONFIG, "true")
                .setProperty(ACKS_CONFIG, "all")
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setTransactionalIdPrefix("transaction-job-test-id")
                .setBootstrapServers("localhost:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("sink")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                );

        processStream.sinkTo(sinkBuilder.build())
                .uid("sink")
                .name("sink");

        env.execute("TransactionJob");
    }
}
